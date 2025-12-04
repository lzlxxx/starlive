#直播间唯一标识表，所有的表的外键都引用此表
CREATE TABLE live (
    id bigint unsigned NOT NULL AUTO_INCREMENT comment '唯一主键',
    room_id bigint unsigned NOT NULL COMMENT '房间号',
    user_id bigint unsigned NOT NULL COMMENT '主播id',
    live_state tinyint unsigned default 2 not null comment '直播间状态,0封禁、1开播、2关闭',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建直播房间时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (id),
    UNIQUE KEY (room_id)
);
create index room_id_idx on live(room_id);
create index user_id_idx on live(user_id);


#直播间信息统计表
create table live_count(
    id bigint unsigned not null auto_increment comment '主键id',
    room_id bigint unsigned not null comment '直播间id',
    user_id bigint unsigned not null COMMENT '主播id',
    max_people bigint unsigned comment '观看人数峰值',
    live_likes bigint unsigned comment '直播间点赞总数',
    live_follows bigint unsigned comment '直播间关注总数',
    comments bigint unsigned comment '弹幕总数',
    click_commodity bigint unsigned comment '点击商品链接总数',
    click_activity bigint unsigned comment '点击活动链接总数',
    create_time timestamp default current_timestamp comment '开播时间',
    update_time timestamp default current_timestamp on update current_timestamp comment '下播时间',
    primary key (id),
    foreign key (room_id) references live(room_id),
    foreign key (user_id) references live(user_id)
);


create index create_time_idx on live_count(create_time);


#直播间基本信息表
create table live_info(
    id bigint unsigned not null auto_increment comment '直播间基本信息唯一id',
    user_id bigint unsigned not null comment '主播id',
    room_id bigint unsigned not null comment '房间号',
    live_name varchar(255) not null comment '直播间名称',
    live_description varchar(255) not null comment '直播简介',
    live_category varchar(255) not null comment '直播类型',
    live_photo_url varchar(255) not null comment '第三方图片服务器地址',
    live_state_info varchar(255) not null comment '此次直播间状态描述，正常关闭、封禁、涉营销等信息描述',
    create_time timestamp default current_timestamp comment '创建时间',
    update_time timestamp default current_timestamp on update current_timestamp comment '修改时间',
    primary key (id),
    foreign key (room_id) references live(room_id),
    foreign key (user_id) references live(user_id)
);
create index room_id_idx on live_info(room_id);
create index user_id_idx on live_info(user_id);

#直播间弹幕表----此表暂不适用，后期讨论弹幕再启用、修改
create table comments(
    id bigint unsigned not null auto_increment comment '评论唯一id',
    room_id bigint unsigned not null comment '所属直播间',
    user_id bigint unsigned not null comment '用户id',
    content text comment '弹幕内容',
    statue tinyint unsigned default 1 comment '状态：删除0、发布1',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP comment '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
    primary key (id),
    foreign key (room_id) references live(room_id),
    foreign key (user_id) references live(user_id)
)