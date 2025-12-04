package com.starlive.org.service.impl;


import com.starlive.org.exception.ServiceException;
import com.starlive.org.service.ElasticSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.starlive.org.errcode.BaseErrorCode.REMOTE_SEARCH_RESULT_NULL_ERROR;

/**
 * es索引及文档增删该查
 * @param <T>
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ElasticSearchServiceImpl<T> implements ElasticSearchService<T> {
    private final ElasticsearchTemplate elasticsearchTemplate;
    private final Class<T> entity;
    @Override
    public boolean createIndex() {
        IndexOperations indexOperations = elasticsearchTemplate.indexOps(entity);
        if(!indexOperations.exists()){
            indexOperations.create();
            indexOperations.putMapping(entity);
            return true;
        }
        return false;
    }

    @Override
    public void saveDocument(T documentTemplate) {
        elasticsearchTemplate.save(documentTemplate);
    }


    @Override
    public List<T> matchQuery(String query) {
        Criteria criteria = new Criteria("title").matches(query);
        CriteriaQuery criteriaQuery = new CriteriaQuery(criteria);
        SearchHits<T> searchHit = elasticsearchTemplate.search(criteriaQuery,entity);
        return searchHit.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    @Override
    public T termQuery(Long query) {
        try {
            Criteria criteria = new Criteria("Id").is(query);
            CriteriaQuery criteriaQuery = new CriteriaQuery(criteria);
            SearchHit<T> searchHits = elasticsearchTemplate.searchOne(criteriaQuery,entity);
            if (searchHits == null){
                return null;
            }
            return searchHits.getContent();
        }catch (Exception e){
            throw new ServiceException("未找到对应结果", REMOTE_SEARCH_RESULT_NULL_ERROR);
        }


    }

    @Override
    public List<T> templateFuzzyQuery(String query) {
        Criteria criteria = new Criteria("title").fuzzy(query)
                .or("description").fuzzy(query)
                .or(new Criteria("tags.tag").fuzzy(query))
                .or("author").fuzzy(query);
        CriteriaQuery criteriaQuery = new CriteriaQuery(criteria);
        SearchHits<T> searchHits = elasticsearchTemplate.search(criteriaQuery,entity);
        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    @Override
    public List<T> rangeQuery(String range,int start,int end) {
        Criteria criteria = new Criteria(range).greaterThanEqual(start).lessThanEqual(end);
        CriteriaQuery criteriaQuery = new CriteriaQuery(criteria);
        SearchHits<T> searchHits = elasticsearchTemplate.search(criteriaQuery,entity);
        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    @Override
    public List<T> multiMatchQuery(Set<String> query) {
        return null;
    }
}
