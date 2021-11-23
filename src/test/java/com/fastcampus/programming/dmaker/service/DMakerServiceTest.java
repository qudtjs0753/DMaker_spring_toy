package com.fastcampus.programming.dmaker.service;

import com.fastcampus.programming.dmaker.code.StatusCode;
import com.fastcampus.programming.dmaker.dto.CreateDeveloper;
import com.fastcampus.programming.dmaker.dto.DeveloperDetailDto;
import com.fastcampus.programming.dmaker.dto.DeveloperDto;
import com.fastcampus.programming.dmaker.entity.Developer;
import com.fastcampus.programming.dmaker.repository.DeveloperRepository;
import com.fastcampus.programming.dmaker.repository.RetiredDeveloperRepository;
import com.fastcampus.programming.dmaker.type.DeveloperLevel;
import com.fastcampus.programming.dmaker.type.DeveloperSkillType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;

import static com.fastcampus.programming.dmaker.type.DeveloperLevel.JUNIOR;
import static com.fastcampus.programming.dmaker.type.DeveloperLevel.SENIOR;
import static com.fastcampus.programming.dmaker.type.DeveloperSkillType.FRONT_END;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

//격리성을 올리기 위해 Mockito를 사용 (Mocking을 위해)

//Junit은 자바테스트이므로
//단순 자바 테스트가 아니라 Mockito라는 외부 기능을 활용해서 테스트 진행하겠다라고 선언하는 것
@ExtendWith(MockitoExtension.class)
class DMakerServiceTest {
    //@Mock annotation으로 DMakerService의 dependency들을 가져옴
    //이렇게 해주면 Mock에 등록됨.
    //InjectMocks 했을 때 얘네 2개 자동으로 등록시켜준다.
    @Mock
    private DeveloperRepository developerRepository;
    @Mock
    private RetiredDeveloperRepository retiredDeveloperRepository;

    @InjectMocks//가짜를 inject 시켜주겠다고 하는 것.
    private DMakerService dMakerService;

    @Test
    public void testSomething(){
        //mock들의 동작을 정의
        //Mocking
        //developerRepository의 findByMemberId를 실행하면
        //아래에 설정해놓은 가짜 결과를 띄워주겠다고 하는 것.
        given(developerRepository.findByMemberId(anyString()))
                .willReturn(Optional.of(Developer.builder()
                        .developerLevel(SENIOR)
                        .developerSkillType(FRONT_END)
                        .experienceYears(12)
                        .statusCode(StatusCode.EMPLOYED)
                        .name("name")
                        .age(12)
                        .build()));

        DeveloperDetailDto developerDetail = dMakerService.getDeveloperDetail("memberId");

        assertEquals(SENIOR, developerDetail.getDeveloperLevel());
        assertEquals(FRONT_END, developerDetail.getDeveloperSkillType());
        assertEquals(12, developerDetail.getExperienceYears());
    }
}