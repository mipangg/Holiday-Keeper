package io.mipangg.holidaykeeper.global.exception;

import lombok.Getter;

@Getter
public class CustomLogicException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String detail;
    public CustomLogicException(ErrorCode errorCode, String detail) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.detail = detail;
    }

    public CustomLogicException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.detail = "";
    }

    public CustomLogicException(ErrorCode errorCode, Object... args) {
        super(String.format(errorCode.getMessage(), args));
        this.errorCode = errorCode;
        this.detail = "";
    }

}