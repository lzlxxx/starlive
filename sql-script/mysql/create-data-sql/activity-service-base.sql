use  starlive;
CREATE TABLE events (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL COMMENT '活动名称',
    description TEXT COMMENT '活动描述',
    start_time DATETIME NOT NULL COMMENT '活动开始时间',
    end_time DATETIME NOT NULL COMMENT '活动结束时间',
    location VARCHAR(255) COMMENT '活动地点',
    location_point POINT not null comment '地理位置信息（经纬度）',
    organizer_id BIGINT NOT NULL COMMENT '活动组织者ID',
    status ENUM('draft', 'published', 'ongoing', 'finished', 'cancelled') DEFAULT 'draft' COMMENT '活动状态',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标志，0为未删除，1为已删除',
    poster_url VARCHAR(255) COMMENT '活动海报'
);
ALTER TABLE events
    DROP COLUMN location_point;

-- 2. 添加新的列，指定SRID
ALTER TABLE events
    ADD COLUMN location_point POINT SRID 4326;
CREATE TABLE event_participants (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    event_id BIGINT NOT NULL COMMENT '活动ID',
    user_id BIGINT NOT NULL COMMENT '参与者用户ID',
    status ENUM('registered', 'attended', 'cancelled') DEFAULT 'registered' COMMENT '参与状态',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标志，0为未删除，1为已删除'
);
CREATE INDEX event_id_idx ON event_organizers(event_id);

CREATE TABLE event_organizers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    event_id BIGINT NOT NULL COMMENT '活动ID',
    organizer_id BIGINT NOT NULL COMMENT '组织者用户ID',
    role ENUM('host', 'coordinator', 'sponsor') DEFAULT 'host' COMMENT '组织者角色',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标志，0为未删除，1为已删除'
);
CREATE INDEX event_id_idx ON event_organizers(event_id);

CREATE TABLE event_rewards (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    event_id BIGINT NOT NULL COMMENT '活动ID',
    reward_name VARCHAR(255) NOT NULL COMMENT '奖励名称',
    description TEXT COMMENT '奖励描述',
    quantity INT DEFAULT 1 COMMENT '奖励数量',
    distributed BOOLEAN DEFAULT FALSE COMMENT '是否已发放',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标志，0为未删除，1为已删除'
);
CREATE INDEX event_id_idx ON event_organizers(event_id);

CREATE TABLE event_schedule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    event_id BIGINT NOT NULL COMMENT '活动ID',
    schedule_name VARCHAR(255) NOT NULL COMMENT '日程名称',
    schedule_time DATETIME NOT NULL COMMENT '日程时间',
    location VARCHAR(255) COMMENT '日程地点',
    description TEXT COMMENT '日程描述',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '日程创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '日程更新时间',
    del_flag TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标志，0为未删除，1为已删除'
);
CREATE INDEX event_id_idx ON event_schedule(event_id);

CREATE TABLE event_status_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    event_id BIGINT NOT NULL COMMENT '活动ID',
    old_status ENUM('draft', 'published', 'ongoing', 'finished', 'cancelled') COMMENT '原状态',
    new_status ENUM('draft', 'published', 'ongoing', 'finished', 'cancelled') COMMENT '新状态',
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '状态改变时间',
    changed_by BIGINT NOT NULL COMMENT '状态修改者ID',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标志，0为未删除，1为已删除'
);
CREATE INDEX event_id_idx ON event_status_logs(event_id);

