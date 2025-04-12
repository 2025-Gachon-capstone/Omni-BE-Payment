package org.example.omnibepayment.common.apiPayload.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.omnibepayment.common.apiPayload.code.BaseErrorCode;
import org.example.omnibepayment.common.apiPayload.code.ErrorReasonDto;


@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {

    private BaseErrorCode code;

    public ErrorReasonDto getErrorReason(){
        return this.code.getReason();
    }

    public ErrorReasonDto getErrorReasonHttpStatus(){
        return this.code.getReasonHttpStatus();
    }

}
