package com.starlive.org.service.imp;


import com.starlive.org.enity.Video;
import com.starlive.org.mapper.IUserLikesMapper;
import com.starlive.org.model.LikePage;
import com.starlive.org.service.IUserLikesService;
import com.starlive.org.util.RedisUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserLikesServiceImpl implements IUserLikesService {

    @Resource
    private IUserLikesMapper userLikesMapper;
    @Resource
    private RedisUtil redisUtil;

    @Value("${page.size}")
    private int PAGE_SIZE;

    @Override
    public List<LikePage> findLike(Integer id, Integer pageNum) {

        String key = id + "," +pageNum;
        List<LikePage> likeList = null;

        //1.根据缓存key优先查redis缓存
        if (( likeList = (List<LikePage>) redisUtil.get(key)) != null){
            return  likeList;
        }
        //2.如果缓存未命中，根据页码获取当前页数据和下一页数据视频id
        List<Integer> videoIdList = userLikesMapper.getLikesList(id, (pageNum - 1) * PAGE_SIZE, PAGE_SIZE * 2);
        //3.如果数据列为空,代表所查询的页超出数据所在页范围
        if (videoIdList.size() == 0){
            return null;
        }
        //3.根据视频id批量查询视频
        List<Video> videos = userLikesMapper.findVideos(videoIdList);
        //4.存放当前喜欢页信息
        likeList = new ArrayList<>();
        //5.存放下一页缓存的数据
        ArrayList<LikePage> cacheLikeList = new ArrayList<>();
        //6.遍历出所有视频信息,将当前页数据存入likeList, 下一页数据存入cacheLikeList
       for (int i = 0; i < videos.size(); i++){
            //6.1 按顺序取出videos列表里面的实体数据
            Video video = videos.get(i);
            //6.2 转换成前端所需要的数据
            LikePage likePage = new LikePage();
            BeanUtils.copyProperties(video, likePage);
            //6.3 页数判断
            if (i >= PAGE_SIZE) {
                //6.4 超过当前页的存入缓存list
                cacheLikeList.add(likePage);
            }else {
                //6.5 当前页list
                likeList.add(likePage);
            }
        }
        //7.将当前页数据插入缓存
        redisUtil.setAndTTL(key, likeList, 120);
       //8.将下一页的数据也存入缓存,加快下一次访问
        if (cacheLikeList.size() != 0){
            String cacheKey = id + "," + (pageNum + 1);
            redisUtil.setAndTTL(cacheKey, cacheLikeList, 120);
        }
        //9.返回当前页
        return likeList;
    }
}
