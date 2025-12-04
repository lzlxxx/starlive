package com.starlive.org.model;

import lombok.Data;

import java.util.List;

@Data
public class Result<T> {
    private List<T> data;
    private int total;
    private int page;
    private int pageSize;
    private boolean hasMore;

    public Result(List<T> data, int total, int page, int pageSize) {
        this.data = data;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
        this.hasMore = (page + 1) * pageSize < total;
    }
}