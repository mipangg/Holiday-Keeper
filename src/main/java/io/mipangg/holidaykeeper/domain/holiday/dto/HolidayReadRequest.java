package io.mipangg.holidaykeeper.domain.holiday.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

public record HolidayReadRequest(
        @Schema(description = "조회할 페이지 번호 (기본값: 0, 0부터 시작)")
        @Min(0)
        @Max(100)
        Integer page,

        @Schema(description = "한 페이지당 조회할 데이터 개수 (기본값: 20)")
        @Min(0)
        @Max(100)
        Integer size,

        @Schema(description = "공휴일 타입 필터", example = "Public")
        @Nullable
        String type,

        @Schema(description = "조회 시작 날짜", example = "2025-01-01")
        @Positive
        @Nullable
        Integer from,

        @Schema(description = "조회 종료 날짜", example = "2025-03-31")
        @Positive
        @Nullable
        Integer to
) {
    public HolidayReadRequest {
        if (page == null) {
            page = 0;
        }

        if (size == null) {
            size = 20;
        }
    }
}
