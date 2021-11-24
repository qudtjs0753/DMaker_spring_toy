package com.fastcampus.programming.dmaker.service;

import com.fastcampus.programming.dmaker.code.StatusCode;
import com.fastcampus.programming.dmaker.dto.CreateDeveloper;
import com.fastcampus.programming.dmaker.dto.DeveloperDetailDto;
import com.fastcampus.programming.dmaker.entity.Developer;
import com.fastcampus.programming.dmaker.exception.DMakerErrorCode;
import com.fastcampus.programming.dmaker.exception.DMakerException;
import com.fastcampus.programming.dmaker.repository.DeveloperRepository;
import com.fastcampus.programming.dmaker.repository.RetiredDeveloperRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.fastcampus.programming.dmaker.type.DeveloperLevel.SENIOR;
import static com.fastcampus.programming.dmaker.type.DeveloperSkillType.FRONT_END;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

    private Developer defaultDeveloper = Developer.builder()
            .developerLevel(SENIOR)
            .developerSkillType(FRONT_END)
            .experienceYears(12)
            .statusCode(StatusCode.EMPLOYED)
            .name("name")
            .age(12)
            .build();

    private CreateDeveloper.Request defaultCreateRequest = CreateDeveloper.Request.builder()
            .developerLevel(SENIOR)
            .developerSkillType(FRONT_END)
            .experienceYears(12)
            .memberId("memberId")
            .name("name")
            .age(32)
            .build();
    @Test
    public void testSomething(){
        //mock들의 동작을 정의
        //Mocking
        //developerRepository의 findByMemberId를 실행하면
        //아래에 설정해놓은 가짜 결과를 띄워주겠다고 하는 것.

        given(developerRepository.findByMemberId(anyString()))
                .willReturn(Optional.of(defaultDeveloper));


        DeveloperDetailDto developerDetail = dMakerService.getDeveloperDetail("memberId");

        assertEquals(SENIOR, developerDetail.getDeveloperLevel());
        assertEquals(FRONT_END, developerDetail.getDeveloperSkillType());
        assertEquals(12, developerDetail.getExperienceYears());
    }
    @Test
    void createDeveloperTest_success() {
        //given

        //주어졌을 때 return 없어야 중복되는게 없으니까 empty값을 기대
        given(developerRepository.findByMemberId(anyString()))
                .willReturn(Optional.empty());
        //저장한 애를 capture
        ArgumentCaptor<Developer> captor =
                ArgumentCaptor.forClass(Developer.class);

        //when
        CreateDeveloper.Response developer = dMakerService.createDeveloper(defaultCreateRequest);

        //then
        //times: mockito에서 verify를 해줌.
        //특정 mock이 몇번 호출되었는지를 검증해줌.
        verify(developerRepository, times(1))
                .save(captor.capture());
        //capture된 데이터 확인
        Developer saveDeveloper = captor.getValue();
        assertEquals(SENIOR, saveDeveloper.getDeveloperLevel());
        assertEquals(FRONT_END, saveDeveloper.getDeveloperSkillType());
        assertEquals(12, saveDeveloper.getExperienceYears());
    }
    @Test
    void createDeveloperTest_failed_with_duplicated() {

        given(developerRepository.findByMemberId(anyString()))
                .willReturn(Optional.of(defaultDeveloper));
        ArgumentCaptor<Developer> captor =
                ArgumentCaptor.forClass(Developer.class);

        //when
        //then
        //times: mockito에서 verify를 해줌.
        //특정 mock이 몇번 호출되었는지를 검증해줌.
        //exception 날아올 것으로 예상되는 class와
        //해당 exception을 던질 동작을 받음.
        DMakerException dMakerException = assertThrows(DMakerException.class,
                () -> dMakerService.createDeveloper(defaultCreateRequest)
        );

        assertEquals(DMakerErrorCode.DUPLICATED_MEMBER_ID, dMakerException.getDMakerErrorCode());

    }

}