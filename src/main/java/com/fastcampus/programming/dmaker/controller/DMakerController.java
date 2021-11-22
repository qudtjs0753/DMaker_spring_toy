package com.fastcampus.programming.dmaker.controller;

import com.fastcampus.programming.dmaker.dto.*;
import com.fastcampus.programming.dmaker.exception.DMakerException;
import com.fastcampus.programming.dmaker.service.DMakerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

//Spring framework의 annotation
//Dmaker Controller를 RestController라는 type의 bean으로 등록
//A convenience annotation that is itself annotated with @Controller and @ResponseBody.
//@RestController는 @Controller와 @Response 두개가 결합된 annotation.

/** DMakerController(Bean)  DMakerService(Bean)  DMakerRepository(Bean)
 ===================================================================
controller가 service를 가져다쓰고싶다면? DMakerService에서 했던것 처럼
constructor를 통한 주입해주면 됨*/
@Slf4j
@RestController
@RequiredArgsConstructor
public class DMakerController {
    private final DMakerService dMakerService;

    //DTO를 통해 entity와 응답을 내려주는 데이터를 분리시켜줌으로서
    //유연성을 높여줌.
    @GetMapping("/developers")
    public List<DeveloperDto> getAllDevelopers(){
        log.info("GET /developers HTTP/1.1");

        return dMakerService.getAllEmployedDevelopers();
    }

    @GetMapping("/developers/{memberId}")
    public DeveloperDetailDto getDeveloperDetail(
            @PathVariable String memberId
    ){
        log.info("GET /developers HTTP/1.1");

        return dMakerService.getDeveloperDetail(memberId);
    }

    //@RequestBody annotation : req body 내부 데이터를 뒤 변수에 담아줌.
    //@Valid : request body 값을 request 변수에 담아줄 때 validation
    //문제 있으면 methodargumentnotvalid exception처리함.
    @PostMapping("/create-developer")
    public CreateDeveloper.Response createDevelopers(
           @Valid @RequestBody CreateDeveloper.Request request
    ){
        //요거 찍고 싶을때 Request class에 Tostring 박아주면 편리
        log.info("request : {}", request);

        return dMakerService.createDeveloper(request);
    }

    @PutMapping("/developer/{memberId}")
    public DeveloperDetailDto editDeveloper(
            @PathVariable String memberId,
            @Valid @RequestBody EditDeveloper.Request request
    ){
        log.info("PUT HTTP/1.1");

        return dMakerService.editDeveloper(memberId, request);
    }

    @DeleteMapping("/developer/{memberId}")
    public DeveloperDetailDto deleteDeveloper(
        @PathVariable String memberId
    ){
        return dMakerService.deleteDeveloper(memberId);
    }



}
