create database StarLive;
use StarLive;

CREATE TABLE users (
                       user_id BIGINT AUTO_INCREMENT PRIMARY KEY comment '用户唯一ID',
                       username VARCHAR(50) NOT NULL UNIQUE comment '用户名',
                       password VARCHAR(255) NOT NULL comment '密码（需加密处理）',
                       email VARCHAR(100) NOT NULL UNIQUE comment '邮箱',
                       avatar_url VARCHAR(255) comment '头像的URL地址',
                       location  POINT not null comment '地理位置信息（经纬度）',
                       ip_address VARCHAR(45) comment '用户IP地址（IPv6兼容）',
                       is_test  boolean comment '是否是管理员（测试）账户' ,
                       bio TEXT comment '用户简介',
                       followers_count BIGINT DEFAULT 0 COMMENT '粉丝数',
                       create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP comment '注册时间',
                       update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间'

);
create  index user_id_idx on users(user_id);
create  index username_idx on users(username);
create spatial index location_idx on users(location);

alter table users add column phone VARCHAR(50) comment '手机号码';
#改后缀加索引

CREATE TABLE followers (
                           follower_id BIGINT comment '关注者ID',
                           followed_id BIGINT comment '被关注者ID',
                           status TINYINT DEFAULT 1 COMMENT '关注状态，1表示关注，0表示取消关注',
                           create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP comment '关注时间',
                           update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间',
                           PRIMARY KEY (follower_id, followed_id),
                           FOREIGN KEY (follower_id) REFERENCES users(user_id) ON DELETE CASCADE,
                           FOREIGN KEY (followed_id) REFERENCES users(user_id) ON DELETE CASCADE
);
create  index follower_id_idx on followers(follower_id);
create  index followed_id_idx on followers(followed_id);


CREATE TABLE friends (
                         user_id BIGINT comment '用户ID',
                         friend_id BIGINT comment '好友ID',
                         create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP comment '好友关系创建时间',
                         PRIMARY KEY (user_id, friend_id),
                         FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                         FOREIGN KEY (friend_id) REFERENCES users(user_id) ON DELETE CASCADE
);
CREATE TABLE companion (
                           user_id BIGINT comment '用户ID',
                           companion_id BIGINT comment '搭子ID',
                           create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP comment '搭子关系创建时间',
                           end_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP comment '搭子关系结束时间',
                           PRIMARY KEY (user_id, companion_id),
                           FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                           FOREIGN KEY (companion_id) REFERENCES users(user_id) ON DELETE CASCADE
);
create  index user_id_idx on friends(user_id);
create  index friend_id_idx on friends(friend_id);
CREATE TABLE user_data (
                           user_id BIGINT PRIMARY KEY comment '用户ID',
                           data JSON comment '使用JSON格式存储用户动态数据',
                           FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
create  index user_id_idx on user_data(user_id);
CREATE TABLE collection (
                            collection_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '收藏记录的唯一标识符',
                            user_id BIGINT NOT NULL COMMENT '用户ID',
                            content_id BIGINT NOT NULL COMMENT '视频或直播的唯一标识符',
                            content_type ENUM('video', 'live') NOT NULL DEFAULT 'video' COMMENT '内容类型，区分是视频还是直播间',
                            folder_id BIGINT NOT NULL COMMENT '收藏夹ID，指向用户收藏夹表',
                            create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
                            FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                            FOREIGN KEY (folder_id) REFERENCES collection_folder(folder_id) ON DELETE CASCADE,
                            UNIQUE INDEX unique_collection (user_id, content_id, folder_id, content_type)
);
CREATE TABLE likes (
                       like_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '点赞记录的唯一标识符',
                       user_id BIGINT NOT NULL COMMENT '用户ID',
                       content_id BIGINT NOT NULL COMMENT '视频ID',
                       status TINYINT DEFAULT 1 COMMENT '点赞状态默认为1点赞',
                       create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
                       FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                       UNIQUE INDEX unique_like (user_id, content_id)
);
CREATE TABLE collection (
                            collection_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '收藏记录的唯一标识符',
                            user_id BIGINT NOT NULL COMMENT '用户ID',
                            content_id BIGINT NOT NULL COMMENT '视频或直播的唯一标识符',
                            content_type ENUM('video', 'live') NOT NULL DEFAULT 'video' COMMENT '内容类型，区分是视频还是直播间',
                            folder_id BIGINT NOT NULL COMMENT '收藏夹ID，指向用户收藏夹表',
                            create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
                            FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                            FOREIGN KEY (folder_id) REFERENCES collection_folder(folder_id) ON DELETE CASCADE,
                            UNIQUE INDEX unique_collection (user_id, content_id, folder_id, content_type)
);

CREATE TABLE collection_folder (
                                   folder_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '收藏夹的唯一标识符',
                                   user_id BIGINT NOT NULL COMMENT '用户ID',
                                   collection_count INT DEFAULT 0 COMMENT '收藏夹中收藏内容的数量',
                                   folder_name VARCHAR(255) NOT NULL DEFAULT '默认收藏夹' COMMENT '收藏夹名称',
                                   folder_type ENUM('video', 'live') NOT NULL DEFAULT 'video' COMMENT '收藏夹类型，video 表示仅存视频，live 表示仅存直播',
                                   create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                   UNIQUE (user_id, folder_name),
                                   FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE

);


