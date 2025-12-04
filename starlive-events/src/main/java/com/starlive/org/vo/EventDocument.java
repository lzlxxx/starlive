package com.starlive.org.vo;

import co.elastic.clients.elasticsearch._types.GeoLocation;
import co.elastic.clients.elasticsearch._types.mapping.FieldType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mysql.cj.result.Field;
import com.starlive.org.config.GeoLocationJsonDeserializer;
import com.starlive.org.config.GeoLocationJsonSerializer;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventDocument {
    private Long id;
    
    @JsonProperty("organizerId")
    private Long organizerId;//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")


private String title;
    
    private String description;
    
    @JsonProperty("startTime")
    private Date startTime;
    
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonProperty("endTime")
    private Date endTime;
    
    private String location;
    
    @JsonProperty("locationPoint")
    @JsonSerialize(using = GeoLocationJsonSerializer.class)
    @JsonDeserialize(using = GeoLocationJsonDeserializer.class)
    private GeoLocation locationPoint;
    
    @JsonProperty("organizerName")
    private String organizerName;

    private List<EventRewardsVo> rewards = new ArrayList<>();
    private List<EventScheduleVo> schedule = new ArrayList<>();

    private String status;

    @JsonProperty("delFlag")
    private Integer delFlag;
    
    @JsonProperty("posterUrl")
    private String posterUrl;
    
    public void setLocationPoint(double latitude, double longitude) {
        this.locationPoint = GeoLocation.of(b -> b
                .coords(List.of(latitude,longitude)));
    }
    
    public Double getLat() {
        if (locationPoint != null && locationPoint.isCoords()) {
            List<Double> coords = locationPoint.coords();
            return coords != null && coords.size() > 1 ? coords.get(1) : null;
        }
        return null;
    }
    
    public Double getLon() {
        if (locationPoint != null && locationPoint.isCoords()) {
            List<Double> coords = locationPoint.coords();
            return coords != null && !coords.isEmpty() ? coords.get(0) : null;
        }
        return null;
    }
}
