package com.bsep_sbz.SIEMCenter.controller.dto;

import java.util.ArrayList;
import java.util.List;

public class PageableDto<T> {
    private List<T> content;
    private boolean first;
    private boolean last;
    private int totalPages;

    public PageableDto(List<T> content, boolean first, boolean last, int totalPages) {
        this.content = content;
        this.first = first;
        this.last = last;
        this.totalPages = totalPages;
    }

    public PageableDto() {
        this.content = new ArrayList<>();
        this.first = true;
        this.last = true;
        this.totalPages = 1;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
