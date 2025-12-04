package com.starlive.org.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
public class WorkInfoListVo implements Serializable {
    private static final long serialVersionUID = 1234999L;
    private int worknum;
    private List<WorkInfoVo> workInfoList;
}
