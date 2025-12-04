-- 视频服务表
CREATE TABLE video (

    id INT AUTO_INCREMENT COMMENT '视频唯一标识符',
    uploader_id INT NOT NULL COMMENT '上传者的用户ID',
    title VARCHAR(255) NOT NULL COMMENT '视频标题',
    description TEXT COMMENT '视频描述',
    file_path VARCHAR(255) NOT NULL COMMENT '视频文件的存储路径',
    thumbnail_path VARCHAR(255) COMMENT '视频缩略图的存储路径',
    create_time    datetime NOT NULL COMMENT '创建时间',
    upload_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '视频上传时间，默认为当前时间',
    duration INT COMMENT '视频时长（秒）',
    views INT DEFAULT 0 COMMENT '视频观看次数',
    likes INT DEFAULT 0 COMMENT '视频点赞次数',
    total_collections INT DEFAULT 0 COMMENT '视频累计收藏总数',
    comments INT DEFAULT 0 COMMENT '视频评论次数',
    tags VARCHAR(255) COMMENT '视频标签，逗号分隔',
    category VARCHAR(255) COMMENT '视频分类',
    status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending' COMMENT '视频状态，如待审核、已通过、已拒绝',
    privacy ENUM('public', 'private', 'unlisted') DEFAULT 'public' COMMENT '视频隐私设置',
    pv INT DEFAULT 0 COMMENT '页面访问量'
)ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
COMMENT='视频信息表'
ROW_FORMAT=DYNAMIC;

-- 视频评论表
CREATE TABLE comments (
    `id`              varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论ID',
    `father_comment_id`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '父评论ID',
    `to_user_id`      varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '回复的目标用户ID',
    `video_id`        varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '视频ID',
    `from_user_id`    varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论者ID',
    `comment`         text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
    `create_time`     datetime NOT NULL COMMENT '创建时间',
    `update_time`     datetime NULL DEFAULT NULL COMMENT '最后更新时间',
    `likes`           int NOT NULL DEFAULT 0 COMMENT '点赞数量',
    `replies_count`   int NOT NULL DEFAULT 0 COMMENT '回复数量',
    `status`          enum('active', 'inactive', 'deleted') NOT NULL DEFAULT 'active' COMMENT '评论状态',
    `report_count`    int NOT NULL DEFAULT 0 COMMENT '举报次数',
    `is_pinned`       tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否置顶',
    `parent_id`       varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '顶级评论ID（如果此评论是回复，则指向顶级评论的ID）',
    PRIMARY KEY (`id`),
    INDEX `idx_create_time` (`create_time`) COMMENT '按创建时间索引',
    INDEX `idx_likes` (`likes`) COMMENT '按点赞数索引'
) ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
COMMENT='视频评论表'
ROW_FORMAT=DYNAMIC;
create index id on comments(id);
create index father_comment_id on comments(father_comment_id);
create index video_id on comments(video_id);

--minio中视频文件
CREATE TABLE files (
                       id               BIGINT AUTO_INCREMENT,
                       user_id          BIGINT NOT NULL COMMENT '上传者id',
                       upload_id        VARCHAR(255) COMMENT '文件上传id',
                       md5              VARCHAR(255) COMMENT '文件计算md5',
                       url              VARCHAR(255) COMMENT '文件访问地址',
                       bucket           VARCHAR(64) COMMENT '存储桶',
                       object           VARCHAR(255) COMMENT 'minio中文件名',
                       origin_file_name VARCHAR(255) COMMENT '原始文件名',
                       size             BIGINT COMMENT '文件大小',
                       type             VARCHAR(64) COMMENT '文件类型',
                       chunk_size       BIGINT COMMENT '分片大小',
                       chunk_count      INT COMMENT '分片数量',
                       is_delete        CHAR DEFAULT '0' COMMENT '是否删除',
                       created_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                       updated_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                       PRIMARY KEY (id)
)ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
ROW_FORMAT=DYNAMIC
comment='Minio中的视频文件'

