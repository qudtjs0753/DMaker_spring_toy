package com.fastcampus.programming.dmaker.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

//enum에 constructor 왜 필요..?
//enum에 프로퍼티(Message)가 있을 때
//프로퍼티를 이용해서 enum을 생성시키려면 필요.
@Getter
@AllArgsConstructor
public enum DMakerErrorCode {
    NO_DEVELOPER("해당되는 개발자가 없습니다."),
    DUPLICATED_MEMBER_ID("MemberID가 중복되는 개발자가 있습니다."),
    LEVEL_EXPERIENCE_YEARS_NOT_MATCHED("개발자 레벨과 연차가 맞지 않습니다."),

    INTERNAL_SERVER_ERROR("서버에 오류가 발생했습니다."),
    INVALID_REQUEST("잘못된 요청입니다.");

    private final String Message;
}
