package org.example.azoi.dto.problemtransmit.othertransmit;

import java.util.List;

public class PageVO<T> {
    private List<T> content;
    private long total;
    private int page;
    private int size;
    private int totalPages;

    public static <T> PageVO<T> of(List<T> content, long total, int page, int size) {
        PageVO<T> vo = new PageVO<>();
        vo.content = content;
        vo.total = total;
        vo.page = page;
        vo.size = size;
        vo.totalPages = (int) Math.ceil((double) total / size);
        return vo;
    }

    public static <T> PageVO<T> of(org.springframework.data.domain.Page<T> page) {
        return of(
                page.getContent(),
                page.getTotalElements(),
                page.getNumber() + 1,   // Page 从 0 开始，VO 从 1 开始
                page.getSize()
        );
    }
}
