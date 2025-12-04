package com.starlive.org.service.impl;

import com.starlive.org.dto.req.VideoTemplateReqDTO;
import com.starlive.org.enity.TagTemplate;
import com.starlive.org.enity.VideoTemplateDoc;
import com.starlive.org.dto.resp.VideoTemplateRespDTO;
import com.starlive.org.service.VideoRecommendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.common.lucene.search.function.FunctionScoreQuery;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.client.erhlc.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class VideoRecommendServiceImpl implements VideoRecommendService {
    private final ElasticsearchTemplate elasticsearchTemplate;
    /**
     * 线程池，并行对对应标签进行查询，最大程度的利用cpu的性能
     */
    private final ExecutorService executorService = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors(),
            9999,
            TimeUnit.SECONDS,
            new SynchronousQueue<>(),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );
    @Override
    public List<VideoTemplateRespDTO> getVideoFromRecommend(VideoTemplateReqDTO requestParam) {
//        根据当前视频的tag进行筛选推荐
        Set<TagTemplate> tags = requestParam.getTags();
//        n个标签、n个查询列表
        List<Future<List<VideoTemplateDoc>>> futures = new ArrayList<>();
        // 对每个标签进行并行查询
        for (TagTemplate tag : tags) {
            futures.add(executorService.submit(this::getRandomVideo)); // 直接调用现有的 getRandomVideo 方法
        }

        // 等待所有任务完成，并收集所有结果
        List<VideoTemplateDoc> allVideos = new ArrayList<>();
        for (Future<List<VideoTemplateDoc>> future : futures) {
            try {
                allVideos.addAll(future.get()); // 获取查询结果并合并
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        // 对所有视频进行随机排序
        Collections.shuffle(allVideos);
        return mapToResponseDTO(allVideos);
    }

    /**
     * 获取随机视频
     */
    private List<VideoTemplateDoc> getRandomVideo(){
        // 构建查询
        FunctionScoreQueryBuilder funcQuery = QueryBuilders
                .functionScoreQuery(QueryBuilders.matchAllQuery(),
                        new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{
                                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                                        ScoreFunctionBuilders.randomFunction()
                                )
                        }
                )
                .scoreMode(FunctionScoreQuery.ScoreMode.SUM)
                .boostMode(CombineFunction.MULTIPLY);
        Query query = new NativeSearchQuery(funcQuery);
        SearchHits<VideoTemplateDoc> searchHits = elasticsearchTemplate.search(query,VideoTemplateDoc.class);
        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }
    /**
     * 将查询到的VideoDoc映射为VideoResp
     */
    private List<VideoTemplateRespDTO> mapToResponseDTO(List<VideoTemplateDoc> videos) {
        return videos.stream()
                .map(videoDoc -> {
                    // 直接构建 VideoTemplateRespDTO
                    return new VideoTemplateRespDTO(
                            videoDoc.getId(),
                            videoDoc.getTitle(),
                            videoDoc.getDescription(),
                            videoDoc.getAuthor(),
                            videoDoc.getUrl(),
                            videoDoc.getTags(),
                            videoDoc.getStatus() != null ? videoDoc.getStatus().getCode() : null
                    );
                })
                .collect(Collectors.toList());
    }
}
