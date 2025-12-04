package com.starlive.org.service;

import java.util.List;
import java.util.Set;

public interface ElasticSearchService<T>{
    /**
     * 创建索引：总体分成三个索引——用户、视频、直播间
     */
    boolean createIndex();
    /**
     * 添加文档
     */
    void saveDocument(T document);

    /**
     * 匹配查询
     */
    List<T> matchQuery(String query);

    /**
     * 多字段查询
     */
    List<T> multiMatchQuery(Set<String> query);

    /**
     * 精确查询
     */
    T termQuery(Long query);
    /**
     * 模糊查询
     */
    List<T> templateFuzzyQuery(String query);

    /**
     * 范围查询
     */
    List<T> rangeQuery(String range,int start,int end);

}
