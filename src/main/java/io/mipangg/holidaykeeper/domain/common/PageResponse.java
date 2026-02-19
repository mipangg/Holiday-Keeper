package io.mipangg.holidaykeeper.domain.common;

import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PageResponse<T> {
    private List<T> content;        // 현재 페이지 데이터 목록
    private int page;        // 현재 페이지 번호 (0부터 시작)
    private int size;           // 페이지 크기
    private int totalPages;         // 전체 페이지 수
    private long totalElements;     // 전체 데이터 수
    private boolean hasNext;        // 다음 페이지 존재 여부

    // Page<T> 객체를 PageResponse로 변환하는 생성자
    public PageResponse(Page<T> page) {
        this.content = page.getContent();
        this.page = page.getNumber();
        this.size = page.getSize();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.hasNext = page.hasNext();
    }

}
