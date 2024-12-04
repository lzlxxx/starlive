package com.starlive.org.elasticrepository;

import com.starlive.org.vo.EventDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventDocumentRepository extends ElasticsearchRepository<EventDocument, Long> {
    @Query("{\n" +
            "  \"bool\": {\n" +
            "    \"should\": [\n" +
            "      { \"wildcard\": { \"description\": { \"value\": \"*?0*\" } } },\n" +  // 中间匹配
            "      { \"wildcard\": { \"organizerName\": { \"value\": \"*?0*\" } } },\n" +  // 中间匹配
            "      { \"wildcard\": { \"location\": { \"value\": \"*?0*\" } } }  \n" +  // 中间匹配
            "    ],\n" +
            "    \"minimum_should_match\": 1\n" +   // 至少匹配一个字段
            "  }\n" +
            "}")
    Page<EventDocument> findEvent(String keyword, Pageable pageable);

}
