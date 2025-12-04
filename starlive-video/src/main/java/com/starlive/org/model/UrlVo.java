package com.starlive.org.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UrlVo {
    private String uploadId;
    private List<String> urls;
}
