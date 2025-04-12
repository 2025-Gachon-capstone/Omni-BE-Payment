package org.example.omnibepayment.common.apiPayload.code;

public interface BaseErrorCode {
    public ErrorReasonDto getReason();
    public ErrorReasonDto getReasonHttpStatus();
}
