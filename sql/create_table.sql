create database if not exists ark_pictures_db;

USE ark_pictures_db;

-- 用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
                        `id`            bigint NOT NULL AUTO_INCREMENT COMMENT 'id' PRIMARY KEY,
                        `userName`      varchar(256)                        NULL COMMENT '用户昵称',
                        `planetCode`    varchar(256)                        NULL COMMENT '星球编号',
                        `role`          tinyint                             DEFAULT '0' NOT NULL COMMENT '身份 0-普通用户 1-管理员',
                        `avatarUrl`     varchar(1024)                       NULL COMMENT '用户头像',
                        `userPassword`  varchar(512)                        NOT NULL COMMENT '密码',
                        `phone`         varchar(128)                        unique NULL COMMENT '电话',
                        `userStatus`    int DEFAULT '0'                     NULL COMMENT '状态 0-正常',
                        `createTime`    datetime DEFAULT CURRENT_TIMESTAMP  NULL COMMENT '创建时间',
                        `updateTime`    datetime DEFAULT CURRENT_TIMESTAMP  NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                        `isDelete`      tinyint DEFAULT '0'                 NOT NULL COMMENT '是否删除'
) COMMENT='用户表' collate = utf8mb4_unicode_ci;

-- 图片表
DROP TABLE IF EXISTS `picture`;
CREATE TABLE `picture` (
                           id           bigint NOT NULL auto_increment comment 'id' primary key,
                           originUrl    varchar(512)                       not null comment '原图 url',
                           thumbUrl     varchar(512)                       not null comment '缩略图 url',
                           name         varchar(128)                       not null comment '图片名称',
                           introduction varchar(512)                       null comment '简介',
                           category     varchar(64)                        null comment '分类',
                           tags         varchar(512)                       null comment '标签（JSON 数组）',
                           picSize      bigint                             null comment '图片体积',
                           picWidth     int                                null comment '图片宽度',
                           picHeight    int                                null comment '图片高度',
                           picScale     double                             null comment '图片宽高比例',
                           picFormat    varchar(32)                        null comment '图片格式',
                           userId       bigint                             not null comment '创建用户 id',
                           thumbCount   bigint default 0                   not null comment '点赞量',
                           createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
                           editTime     datetime default CURRENT_TIMESTAMP not null comment '编辑时间',
                           updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
                           isDelete     tinyint  default 0                 not null comment '是否删除',
                           INDEX idx_category (category),         -- 提升基于分类的查询性能
                           INDEX idx_tags (tags),                 -- 提升基于标签的查询性能
                           INDEX idx_userId (userId)              -- 提升基于用户 ID 的查询性能
) COMMENT '图片' collate = utf8mb4_unicode_ci;

-- 点赞表
DROP TABLE IF EXISTS `thumb`;
CREATE TABLE `thumb` (
                         `id`           bigint NOT NULL AUTO_INCREMENT COMMENT 'id' PRIMARY KEY,
                         `userId`       bigint NOT NULL COMMENT '用户 id',
                         `pictureId`    bigint NOT NULL COMMENT '图片 id',
                         `createTime`   datetime DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
                        index `idx_userId` (`userId`)
) COMMENT='点赞表' collate = utf8mb4_unicode_ci;

ALTER TABLE `picture`
    -- 添加新列
    ADD COLUMN reviewStatus INT DEFAULT 0 NOT NULL COMMENT '审核状态：0-待审核; 1-通过; 2-拒绝',
    ADD COLUMN reviewMessage VARCHAR(512) NULL COMMENT '审核信息',
    ADD COLUMN reviewerId BIGINT NULL COMMENT '审核人ID',
    ADD COLUMN reviewTime DATETIME NULL COMMENT '审核时间';

-- 创建基于 reviewStatus 列的索引
CREATE INDEX idx_reviewStatus ON picture (reviewStatus);