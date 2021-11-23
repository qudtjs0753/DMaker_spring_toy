package com.fastcampus.programming.dmaker.controller;

import com.fastcampus.programming.dmaker.dto.DeveloperDto;
import com.fastcampus.programming.dmaker.service.DMakerService;
import com.fastcampus.programming.dmaker.type.DeveloperLevel;
import com.fastcampus.programming.dmaker.type.DeveloperSkillType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DMakerController.class) //원하는 controller bean만 쫙 올려서 사용할 수 있게 해줌
class DMakerControllerTest {
    //contoller에 요청값을 가지고 호출을 해줘야되는데
    //직접 호출시 parameter binding validation등을 할 수 없기때문에
    //mockmvc를 이용
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DMakerService dMakerService; //Dependency 가짜 bean으로 등록


    protected MediaType contentType =
            new MediaType(MediaType.APPLICATION_JSON.getType(),
                    MediaType.APPLICATION_JSON.getSubtype(),
                    StandardCharsets.UTF_8);


    @Test
    void getAllDeveloper() throws Exception {
        DeveloperDto juniorDeveloperDto = DeveloperDto.builder()
                .developerSkillType(DeveloperSkillType.BACK_END)
                .developerLevel(DeveloperLevel.JUNIOR)
                .memberId("memberId1").build();
        DeveloperDto seniorDeveloperDto = DeveloperDto.builder()
                .developerSkillType(DeveloperSkillType.FRONT_END)
                .developerLevel(DeveloperLevel.SENIOR)
                .memberId("memberId2").build();
        given(dMakerService.getAllEmployedDevelopers())
                .willReturn(Arrays.asList(juniorDeveloperDto,seniorDeveloperDto));

        //mockmvc가 get으로 /developers를 호출하면
        //contenttype을 jsontype으로 줄테니
        //너도 json으로 줘. 그리고 이건 UTF-8 인코딩이야
        //라고 말해줌. (위에서 contenttype설정해준 걸로)
        mockMvc.perform(get("/developers").contentType(contentType))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(
                        jsonPath("$.[0].developerSkillType",
                                is((DeveloperSkillType.BACK_END.name())))
                ).andExpect(
                        jsonPath("$.[0].developerLevel",
                                is(DeveloperLevel.JUNIOR.name()))
                ).andExpect(
                        jsonPath("$.[1].developerSkillType",
                                is(DeveloperSkillType.FRONT_END.name()))
                ).andExpect(
                        jsonPath("$.[1].developerLevel",
                                is(DeveloperLevel.SENIOR.name()))
                );
    }
}