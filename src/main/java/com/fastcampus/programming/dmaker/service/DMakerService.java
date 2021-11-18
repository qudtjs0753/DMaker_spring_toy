package com.fastcampus.programming.dmaker.service;

import com.fastcampus.programming.dmaker.dto.CreateDeveloper;
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

import java.util.Optional;

import static com.fastcampus.programming.dmaker.exception.DMakerErrorCode.DUPLICATED_MEMBER_ID;
import static com.fastcampus.programming.dmaker.exception.DMakerErrorCode.LEVEL_EXPERIENCE_YEARS_NOT_MATCHED;

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
    public void createDeveloper(CreateDeveloper.Request request){
            validateCreateDeveloperRequest(request);
            //builder를 통해 응집성 있게 각 데이터를 세팅해줌.
            //business logic start
            Developer developer = Developer.builder()
                    .developerLevel(DeveloperLevel.JUNIOR)
                    .developerSkillType(DeveloperSkillType.FRONT_END)
                    .experienceYears(2)
                    .name("Olaf")
                    .age(5)
                    .build();
            developerRepository.save(developer);
    }

    private void validateCreateDeveloperRequest(CreateDeveloper.Request request) {
        //business validation
        //business에서 예외적인 상황에서는
        //custom exception을 사용하는게 좋습니다.
        DeveloperLevel developerLevel = request.getDeveloperLevel();
        Integer experienceYears = request.getExperienceYears();
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


}
