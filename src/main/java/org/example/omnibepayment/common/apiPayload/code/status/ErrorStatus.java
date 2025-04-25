package org.example.omnibepayment.common.apiPayload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.omnibepayment.common.apiPayload.code.BaseErrorCode;
import org.example.omnibepayment.common.apiPayload.code.ErrorReasonDto;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 일반 상태
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"COMMON500","서버 에러"),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다"),
    _FORBIDDEN(HttpStatus.FORBIDDEN,"COMMON402","금지된 요청입니다."),
    _NOT_FOUND(HttpStatus.NOT_FOUND,"COMMON403","데이터를 찾지 못했습니다."),

    // member 상태
    _NOT_MATCH_PASSWORD(HttpStatus.BAD_REQUEST,"MEMBER4001","비밀번호가 동일하지 않습니다."),
    _LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "MEMBER4002", "로그인에 실패했습니다."),
    _ALREADY_EXIST_LOGINID(HttpStatus.BAD_REQUEST,"MEMBER4003","이미 존재하는 아이디입니다."),
    _NOT_FOUND_LOGINID(HttpStatus.NOT_FOUND,"MEMBER4004","없는 아이디입니다."),
    _NOT_MATCH_NEWPASSWORD(HttpStatus.BAD_REQUEST,"MEMBER4005","새로운 비밀번호가 동일하지 않습니다."),
    _DUPLICATE_PASSWORD(HttpStatus.BAD_REQUEST,"MEMBER4006","기존 비밀번호와 동일합니다."),
    _ALREADY_RESIGN(HttpStatus.BAD_REQUEST,"MEMBER4007","이미 탈퇴한 사용자입니다."),
    _NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND,"MEMBER4008","사용자를 찾지 못하였습니다."),

    // token 상태
    _NOTFOUND_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED,"TOKEN4001","리프레쉬 토큰이 없습니다."),
    _EXFIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED,"TOKEN4002","만료된 리프레쉬 토큰입니다."),
    _INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED,"TOKEN4003","유효하지 않은 리프레쉬 토큰입니다."),
    _NULL_REFRESH_TOKEN(HttpStatus.NOT_FOUND,"TOKEN4004","리프레쉬 토큰이 헤더에 없습니다."),
    _NULL_ACCESS_TOKEN(HttpStatus.NOT_FOUND,"TOKEN4005","엑세스 토큰이 헤더에 없습니다."),
    _EXFIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED,"TOKEN4006","만료된 엑세스 토큰입니다."),
    _INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED,"TOKEN4007","유효하지 않은 엑세스 토큰입니다."),
    _NOTFOUND_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED,"TOKEN4008","엑세스 토큰이 없습니다."),
    _LOGOUT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR,"TOKEN5001","서버 오류 입니다. 다시 로그아웃 해주세요."),

    // card 상태
    _CARD_ALREADY_EXISTS(HttpStatus.BAD_REQUEST,"CARD4001","이미 존재하는 사용자입니다."),
    _NOT_FOUND_CARD(HttpStatus.NOT_FOUND,"CARD4002","사용자의 카드가 없습니다."),
    _NOT_MATCH_CARDPASSWORD(HttpStatus.BAD_REQUEST,"CARD4003","카드비밀번호가 일치하지 않습니다."),
    _CARD_NUMBER_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR,"CARD5001","중복된 카드 번호로 인한 생성 실패입니다."),
    _CARD_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR,"CARD5002","카드 생성 실패입니다."),

    // sponsor 상태
    _ALREADY_EXIST_SPONSOR(HttpStatus.BAD_REQUEST,"SPONSOR4001","이미 존재하는 스폰서입니다."),
    _NOT_FOUND_SPONSOR(HttpStatus.NOT_FOUND,"SPONSOR4002","없는 스폰서입니다."),
    _SPONSOR_FORBIDDEN(HttpStatus.BAD_REQUEST,"SPONSOR4003","잘못된 스폰서입니다."),
    _SPONSOR_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR,"SPONSOR5001","스폰서 생성 실패입니다."),

    // 카테고리 상태
    _ALREADY_EXIST_CATEGORY(HttpStatus.BAD_REQUEST,"CATEGORY4001","이미 존재하는 카테고리입니다."),
    _CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND,"CATEGORY4002","카테고리가 없습니다."),

    // 혜택 상태
    _NOT_FOUND_BENEFIT(HttpStatus.NOT_FOUND,"BENEFIT4001","혜택이 없습니다."),
    _INVALID_BENEFIT_STATUS(HttpStatus.BAD_REQUEST,"BENEFIT4002","유효하지 않은 헤택 상태입니다."),
    _EXCEED_BENEFIT_AMOUNT(HttpStatus.BAD_REQUEST,"BENEFIT4003","혜택의 수량을 초과하였습니다."),

    // 상품 상태
    _NOT_FOUND_PRODUCT(HttpStatus.NOT_FOUND,"PRODUCT4001","상품이 없습니다."),
    _INVALID_PRODUCT_REQUEST(HttpStatus.BAD_REQUEST,"PRODUCT4002","유효하지 않은 상품 요청입니다.."),

    // 서비스 상태
    _USER_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"SERVICE5000","USER 서버 에러"),
    _CARD_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"SERVICE5001","CARD 서버 에러"),
    _SPONSOR_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"SERVICE5002","SPONSOR 서버 에러"),
    _PAYMENT_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"SERVICE5003","PAYMENT 서버 에러"),

    // DB 상태
    _DATABASE_SAVE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"DATABASE4001","데이터베이스 저장 오류입니다."),

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDto getReason() {
        return ErrorReasonDto.builder()
                .code(code)
                .message(message)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDto getReasonHttpStatus() {
        return ErrorReasonDto.builder()
                .code(code)
                .message(message)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}
