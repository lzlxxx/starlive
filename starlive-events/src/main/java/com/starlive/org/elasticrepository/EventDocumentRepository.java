package com.starlive.org.elasticrepository;

import co.elastic.clients.elasticsearch.core.search.Hit;
import com.starlive.org.vo.EventDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch._types.Result;
import java.util.UUID;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import co.elastic.clients.elasticsearch.core.GetRequest;
import co.elastic.clients.elasticsearch.core.GetResponse;
import java.util.Optional;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;

@Repository
public class EventDocumentRepository {
    
    @Autowired
    private ElasticsearchClient esClient;

    private static final String INDEX_NAME = "events";
    /**
     * 搜索活动
     */
    public Page<EventDocument> findEvent(String keyword, Pageable pageable) throws IOException {
        // 构建should查询
        List<Query> shouldQueries = new ArrayList<>();
        shouldQueries.add(Query.of(q -> q
                .wildcard(WildcardQuery.of(w -> w
                        .field("description")
                        .wildcard("*" + keyword + "*")))));
        shouldQueries.add(Query.of(q -> q
                .wildcard(WildcardQuery.of(w -> w
                        .field("organizerName")
                        .wildcard("*" + keyword + "*")))));
        shouldQueries.add(Query.of(q -> q
                .wildcard(WildcardQuery.of(w -> w
                        .field("location")
                        .wildcard("*" + keyword + "*")))));

        // 构建bool查询
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(INDEX_NAME)  // 替换为您的索引名称
                .query(q -> q
                        .bool(b -> b
                                .should(shouldQueries)
                                .minimumShouldMatch("1")))
                .from((int) pageable.getOffset())
                .size(pageable.getPageSize()));

        SearchResponse<EventDocument> response = esClient.search(searchRequest, EventDocument.class);
        
        List<EventDocument> events = response.hits().hits().stream()
                .map(hit -> hit.source())
                .toList();
        
        return new PageImpl<>(events, pageable, response.hits().total().value());
    }

    /**
     * 根据地理位置搜索活动
     */
    public Page<EventDocument> findEventsByGeoLocation(double latitude, double longitude, double radius, 
            Pageable pageRequest) throws IOException {
        
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(INDEX_NAME)
                .query(q -> q
                        .bool(b -> b
                                .filter(f -> f
                                        .geoDistance(g -> g
                                                .field("locationPoint")
                                                .distance(radius + "km")
                                                .location(l -> l
                                                        .coords(List.of(longitude, latitude)))))))
                .from((int) pageRequest.getOffset())
                .size(pageRequest.getPageSize()));

        SearchResponse<EventDocument> response = esClient.search(searchRequest, EventDocument.class);
        
        List<EventDocument> events = response.hits().hits().stream()
                .map(Hit::source)
                .toList();

        return new PageImpl<>(events, pageRequest, response.hits().total().value());
    }

    /**
     * 根据地理位置搜索活动（返回List）
     */
    public List<EventDocument> findEventsByGeoLocationBind(double latitude, double longitude, 
            int radius) throws IOException {
        
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(INDEX_NAME)
                .query(q -> q
                        .bool(b -> b
                                .filter(f -> f
                                        .geoDistance(g -> g
                                                .field("locationPoint")
                                                .distance(radius + "km")
                                                .location(l -> l
                                                        .coords(List.of(longitude, latitude)))))))
                .size(10000));

        SearchResponse<EventDocument> response = esClient.search(searchRequest, EventDocument.class);
        
        return response.hits().hits().stream()
                .map(Hit::source)
                .toList();
    }

    /**
     * 分页查询所有活动
     */
    public Page<EventDocument> findAllWithPagination(Pageable pageable) throws IOException {
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(INDEX_NAME)
                .query(q -> q
                        .matchAll(MatchAllQuery.of(m -> m)))
                .from((int) pageable.getOffset())
                .size(pageable.getPageSize()));

        SearchResponse<EventDocument> response = esClient.search(searchRequest, EventDocument.class);
        
        List<EventDocument> events = response.hits().hits().stream()
                .map(Hit::source)
                .toList();
        
        return new PageImpl<>(events, pageable, response.hits().total().value());
    }

    /**
     * 保存或更新活动文档
     */
    public void save(EventDocument eventDocument) throws IOException {
        // 检查索引是否存在，不存在则创建
        boolean indexExists = esClient.indices().exists(e -> e.index(INDEX_NAME)).value();
        
        if (!indexExists) {
            // 创建索引并设置映射
            CreateIndexRequest createIndexRequest = CreateIndexRequest.of(c -> c
                .index(INDEX_NAME)
                .mappings(m -> m
                    .properties("id", p -> p.long_(l -> l))
                    .properties("organizerId", p -> p.long_(l -> l))
                    .properties("title", p -> p.text(t -> t
                        .analyzer("ik_max_word")
                        .searchAnalyzer("ik_smart")))
                    .properties("description", p -> p.text(t -> t
                        .analyzer("ik_max_word")
                        .searchAnalyzer("ik_smart")))
                    .properties("startTime", p -> p.date(d -> d))
                    .properties("endTime", p -> p.date(d -> d))
                    .properties("location", p -> p.text(t -> t
                        .analyzer("ik_max_word")
                        .searchAnalyzer("ik_smart")))
                    .properties("locationPoint", p -> p.geoPoint(g -> g))
                    .properties("organizerName", p -> p.text(t -> t
                        .analyzer("ik_max_word")
                        .searchAnalyzer("ik_smart")))
                    .properties("rewards", p -> p.nested(n -> n
                        .properties("rewardId", np -> np.long_(l -> l))
                        .properties("rewardName", np -> np.text(t -> t
                            .analyzer("ik_max_word")
                            .searchAnalyzer("ik_smart")))
                        .properties("rewardDescription", np -> np.text(t -> t
                            .analyzer("ik_max_word")
                            .searchAnalyzer("ik_smart")))
                        .properties("rewardQuantity",np->np.integer(l->l))))
                    .properties("schedule", p -> p.nested(n -> n
                        .properties("scheduleId", np -> np.long_(l -> l))
                            .properties("scheduleName",np -> np.text(l -> l.analyzer("ik_max_word").searchAnalyzer("ik_smart")))
                        .properties("scheduleTime", np -> np.date(d -> d))
                            .properties("scheduleLocation",np -> np.text(l -> l.analyzer("ik_max_word").searchAnalyzer("ik_smart")))
                        .properties("scheduleDescription", np -> np.text(t -> t
                            .analyzer("ik_max_word")
                            .searchAnalyzer("ik_smart")))))
                    .properties("status", p -> p.keyword(k -> k))
                    .properties("delFlag", p -> p.integer(i -> i))
                    .properties("posterUrl", p -> p.keyword(k -> k))
                ));
            
            esClient.indices().create(createIndexRequest);
        }

        // 如果没有ID，生成一个UUID作为文档ID
        String documentId = eventDocument.getId() != null ? 
            eventDocument.getId().toString() : UUID.randomUUID().toString();
            
        IndexRequest<EventDocument> request = IndexRequest.of(i -> i
                .index(INDEX_NAME)
                .id(documentId)
                .document(eventDocument));
                
        IndexResponse response = esClient.index(request);
        
        // 如果是新创建的文档，设置ID
        if (response.result().equals(Result.Created) && eventDocument.getId() == null) {
            eventDocument.setId(Long.parseLong(documentId));
        }

    }

    /**
     * 根据ID查询活动文档
     *
     * @param id 文档ID
     * @return Optional<EventDocument>
     */
    public Optional<EventDocument> findById(Long id) throws IOException {
        try {
            GetRequest request = GetRequest.of(r -> r
                    .index(INDEX_NAME)
                    .id(id.toString()));
            
            GetResponse<EventDocument> response = esClient.get(request, EventDocument.class);
            
            return Optional.ofNullable(response.found() ? response.source() : null);
            
        } catch (Exception e) {
            // 如果是404错误（文档不存在），返回空Optional
            if (e.getMessage() != null && e.getMessage().contains("404")) {
                return Optional.empty();
            }
            throw e;
        }
    }
}
