package com.starlive.org.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExistVo implements Serializable {
    private static final long serialVersionUID = 145545464654L;
    private int isExist;
}
