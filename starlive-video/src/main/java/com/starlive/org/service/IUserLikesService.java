package com.starlive.org.service;


import com.starlive.org.model.LikePage;

import java.util.List;

public interface IUserLikesService {

    //根据传入的id查询喜欢的列表
    public List<LikePage> findLike(Integer id, Integer pageNum);
}
