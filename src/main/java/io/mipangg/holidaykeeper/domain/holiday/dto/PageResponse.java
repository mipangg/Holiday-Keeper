package io.mipangg.holidaykeeper.domain.holiday.dto;


import java.util.List;
import lombok.Builder;
import org.springframework.data.domain.Page;

@Builder
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious
) {

    public static <T> PageResponse<T> from(Page<?> origin, List<T> content) {
        return new PageResponse<>(
                content,
                origin.getNumber(),
                origin.getSize(),
                origin.getTotalElements(),
                origin.getTotalPages(),
                origin.hasNext(),
                origin.hasPrevious()
        );
    }

}