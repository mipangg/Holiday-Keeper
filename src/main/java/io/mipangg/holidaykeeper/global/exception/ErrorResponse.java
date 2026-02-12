package io.mipangg.holidaykeeper.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private final String code;
    private final String message;
    private final int status;
    private final String detail;

    public static ErrorResponse of(CustomLogicException e) {
        return new ErrorResponse(
                e.getErrorCode().name(),
                e.getMessage(),
                e.getErrorCode().getHttpStatus().value(),
                e.getDetail()
        );
    }
}