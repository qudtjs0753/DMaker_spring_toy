package com.fastcampus.programming.dmaker.service;

import com.fastcampus.programming.dmaker.code.StatusCode;
import com.fastcampus.programming.dmaker.dto.CreateDeveloper;
import com.fastcampus.programming.dmaker.dto.DeveloperDetailDto;
import com.fastcampus.programming.dmaker.dto.DeveloperDto;
import com.fastcampus.programming.dmaker.dto.EditDeveloper;
import com.fastcampus.programming.dmaker.entity.Developer;
import com.fastcampus.programming.dmaker.entity.RetiredDeveloper;
import com.fastcampus.programming.dmaker.exception.DMakerException;
import com.fastcampus.programming.dmaker.repository.DeveloperRepository;
import com.fastcampus.programming.dmaker.repository.RetiredDeveloperRepository;
import com.fastcampus.programming.dmaker.type.DeveloperLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import static com.fastcampus.programming.dmaker.exception.DMakerErrorCode.*;

@Service
@RequiredArgsConstructor
public class DMakerService {
    //@Autowired나 @Inject를 안쓰는 이유?
    //service가 이 annotation에 종속적으로 되므로
    //test가 힘들어짐
    //그럼 어떻게 하느냐?
    //constructor를 통해 주입을 받는다.
    //constructor 없으면 쟤네 필요하지만 그거 없으니까
    //DeveloperRepository를 사용 가능
    //근데 이 constructor를 또 lombok annotation 통해 자동생성.
    private final DeveloperRepository developerRepository;
    private final RetiredDeveloperRepository retiredDeveloperRepository;
    @Transactional //AOP!!
    //여기 request에 @Valid 필요한지 안필요한지 생각하기.
    public CreateDeveloper.Response createDeveloper(CreateDeveloper.Request request){
            validateCreateDeveloperRequest(request);
            //builder를 통해 응집성 있게 각 데이터를 세팅해줌.
            //business logic start
            Developer developer = Developer.builder()
                    .developerLevel(request.getDeveloperLevel())
                    .developerSkillType(request.getDeveloperSkillType())
                    .experienceYears(request.getExperienceYears())
                    .memberId(request.getMemberId())
                    .statusCode(StatusCode.EMPLOYED)
                    .name(request.getName())
                    .age(request.getAge())
                    .build();
            developerRepository.save(developer);
            //response DTO를 만들때는 결국 developer entity를 생성하고
            //바로 그 entity를 통해 만들게 된다.
            //이로 인해 developer entity와 강한 결합을 하게 됨.
            //response class에 fronEntity를 만들어서 이걸 처리해줌으로서
            //결합을 약화시켜줌.
            return CreateDeveloper.Response.fromEntity(developer);
    }

    private void validateCreateDeveloperRequest(CreateDeveloper.Request request) {
        //business validation
        //business에서 예외적인 상황에서는
        //custom exception을 사용하는게 좋습니다.
        //if else로 막 담다보면 한도끝도 없이 코드의 복잡도가 올라간다. 특히 재활용성이 떨어진다.
        validateDeveloperLevel(request.getDeveloperLevel(),
                request.getExperienceYears());

        //이미 중복되는 developer가 있으면 throw.
        developerRepository.findByMemberId(request.getMemberId())
                .ifPresent((developer -> {
                    throw new DMakerException(DUPLICATED_MEMBER_ID);
                }));
//        원래는 아래처럼 씀. 근데 이걸 위처럼 하나로 쓸 수 있음.
//        Optional<Developer> developer = developerRepository.findByMemberId(request.getMemberId());
//        if(developer.isPresent())
//            throw new DMakerException(DUPLICATED_MEMBER_ID);
    }

    //이거 어떻게 동작하는건지?
    //:: 표시 잘 모르겠음.
    public List<DeveloperDto> getAllEmployedDevelopers() {
        return developerRepository.findDevelopersByStatusCodeEquals(StatusCode.EMPLOYED)
                .stream().map(DeveloperDto::fromEntity)
                .collect(Collectors.toList());
    }

    public DeveloperDetailDto getDeveloperDetail(String memberId) {
        //findByMemberId는 optional이라 map함수 지원
        //developer null이면 NO_DEVELOPER Exception던져라.
        return developerRepository.findByMemberId(memberId)
                .map(DeveloperDetailDto::fromEntity)
                .orElseThrow(() -> new DMakerException(NO_DEVELOPER));
    }

    @Transactional
    public DeveloperDetailDto editDeveloper(String memberId, EditDeveloper.Request request) {
        validateEditDeveloperRequest(request, memberId);

        Developer developer = developerRepository.findByMemberId(memberId).orElseThrow(
                ()-> new DMakerException(NO_DEVELOPER)
        );

        developer.setDeveloperLevel(request.getDeveloperLevel());
        developer.setDeveloperSkillType(request.getDeveloperSkillType());
        developer.setExperienceYears(request.getExperienceYears());

        return DeveloperDetailDto.fromEntity(developer);
    }

    private void validateEditDeveloperRequest(
            EditDeveloper.Request request,
            String memberId
    ) {
        validateDeveloperLevel(
                request.getDeveloperLevel(),
                request.getExperienceYears()
        );

    }

    private void validateDeveloperLevel(DeveloperLevel developerLevel, Integer experienceYears) {
        if(developerLevel ==DeveloperLevel.SENIOR
                && experienceYears < 10){
            //import static하면 이렇게 깔끔하게 됨.
            throw new DMakerException(LEVEL_EXPERIENCE_YEARS_NOT_MATCHED);
        }
        if(developerLevel == DeveloperLevel.JUNGNIOR
                &&(experienceYears < 4|| experienceYears >10)){
            throw new DMakerException(LEVEL_EXPERIENCE_YEARS_NOT_MATCHED);
        }
        if(developerLevel == DeveloperLevel.JUNIOR && experienceYears > 4){
            throw new DMakerException(LEVEL_EXPERIENCE_YEARS_NOT_MATCHED);
        }
    }

    @Transactional //JPA 더티체킹도 이 annotation으로 적용됨.
    public DeveloperDetailDto deleteDeveloper(String memberId) {
        //1. EMPLOYED -> RETIRED
        //이렇게만 해도 Transaction이 있기 때문에 이 메소드가 종료되면 자동으로 retired로 커밋
        //즉 하나의 작업 예약한 것.
        Developer developer = developerRepository.findByMemberId(memberId)
                .orElseThrow(() -> new DMakerException(NO_DEVELOPER));
        developer.setStatusCode(StatusCode.RETIRED);
        //만약 이 시점에서 throw exception 발생하면? -> rollback 일어남.
        //즉, 이전에 했던 setStatuscode 다 취소. if throw -> rollback.

        //2. save into RetiredDeveloper
        RetiredDeveloper retiredDeveloper = RetiredDeveloper.builder()
                .memberId(memberId)
                .name(developer.getName())
                .build();
        retiredDeveloperRepository.save(retiredDeveloper);
        return DeveloperDetailDto.fromEntity(developer);
    }
}

