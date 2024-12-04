package com.starlive.org.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeoPoint implements Serializable {
    /**
     * 纬度
     */
    private double lat;
    /**
     * 经度
     */
    private double lng;
}