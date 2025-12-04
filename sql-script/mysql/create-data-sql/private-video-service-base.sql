
CREATE TABLE user_private_video_favorites (
                                              id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '收藏记录唯一ID',
                                              user_id BIGINT NOT NULL COMMENT '用户ID，关联users表的user_id',
                                              video_id INT NOT NULL COMMENT '视频ID，关联videos表的id',
                                              is_private BOOLEAN DEFAULT TRUE COMMENT '是否为私密视频',
                                              created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏创建时间',
                                              updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
                                              status ENUM('active', 'removed') DEFAULT 'active' COMMENT '收藏状态，如active或removed',
                                              notes VARCHAR(255) COMMENT '用户对收藏的视频添加的备注',
                                              FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
                                              FOREIGN KEY (video_id) REFERENCES videos(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
  COMMENT='用户收藏的私密视频记录表';
CREATE INDEX idx_user_id ON user_private_video_favorites (user_id);
