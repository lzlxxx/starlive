package com.starlive.org.dto;

import co.elastic.clients.elasticsearch._types.GeoLocation;
import com.fasterxml.jackson.annotation.JsonFormat;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.starlive.org.config.GeoLocationJsonDeserializer;
import com.starlive.org.config.GeoLocationJsonSerializer;
import jakarta.validation.constraints.*;
import lombok.Data;



import java.util.Date;
import java.util.List;

@Data
public class  EventRequest {
    @NotBlank
    private String organizerName;//组织者的用户名
    @NotBlank
    private String title;
    @Size(max=100)
    private String description;
//    @FutureOrPresent
//    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
    private Date startTime;
//    @FutureOrPresent
//    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
    private Date endTime;
    @NotBlank
    private String location;
    @NotNull
    @JsonSerialize(using = GeoLocationJsonSerializer.class)
    @JsonDeserialize(using = GeoLocationJsonDeserializer.class)
    private GeoLocation locationPoint;
    @NotNull
    private Long organizerId;
    private String posterUrl;
    private List<EventRewardDto> rewards;

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
