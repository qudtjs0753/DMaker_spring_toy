package com.fastcampus.programming.dmaker.service;

import com.fastcampus.programming.dmaker.dto.CreateDeveloper;
import com.fastcampus.programming.dmaker.dto.DeveloperDetailDto;
import com.fastcampus.programming.dmaker.dto.DeveloperDto;
import com.fastcampus.programming.dmaker.dto.EditDeveloper;
import com.fastcampus.programming.dmaker.entity.Developer;
import com.fastcampus.programming.dmaker.exception.DMakerErrorCode;
import com.fastcampus.programming.dmaker.exception.DMakerException;
import com.fastcampus.programming.dmaker.repository.DeveloperRepository;
import com.fastcampus.programming.dmaker.type.DeveloperLevel;
import com.fastcampus.programming.dmaker.type.DeveloperSkillType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.transaction.Transactional;
import javax.validation.Valid;

import java.util.List;
import java.util.Optional;
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
    public List<DeveloperDto> getAllDevelopers() {
        return developerRepository.findAll()
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
}

