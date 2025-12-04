package com.starlive.org.model;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_private_video_favorites")
public class UserPrivateVideoFavorites {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "BIGINT COMMENT '收藏记录唯一ID'")
    private Long id;

    @Column(name = "user_id", nullable = false, columnDefinition = "BIGINT COMMENT '用户ID，关联users表的user_id'")
    private Long user_Id;

    @Column(name = "video_id", nullable = false, columnDefinition = "INT COMMENT '视频ID，关联videos表的id'")
    private Integer video_Id;

    @Column(name = "is_private", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE COMMENT '是否为私密视频'")
    private Boolean is_Private = true;

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏创建时间'")
    private LocalDateTime created_At;

    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间'")
    private LocalDateTime updated_At;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "ENUM('active', 'removed') DEFAULT 'active' COMMENT '收藏状态，如active或removed'")
    private Status status = Status.active;

    @Column(name = "notes", columnDefinition = "VARCHAR(255) COMMENT '用户对收藏的视频添加的备注'")
    private String notes;

    public enum Status {
        active("active"),
        removed("removed");

        private final String value;

        Status(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Status fromValue(String value) {
            for (Status status : Status.values()) {
                if (status.value.equals(value)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("No enum constant for value: " + value);
        }
    }

}
