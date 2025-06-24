-- 用户表
USE ark_pictures_db;

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
    `id`            bigint NOT NULL AUTO_INCREMENT COMMENT 'id' PRIMARY KEY,
    `userName`      varchar(256)                        NULL COMMENT '用户昵称',
    `planetCode`    varchar(256)                        NULL COMMENT '星球编号',
    `role`          tinyint                             DEFAULT '0' NOT NULL COMMENT '身份 0-普通用户 1-管理员',
    `avatarUrl`     varchar(1024)                       NULL COMMENT '用户头像',
    `userPassword`  varchar(512)                        NOT NULL COMMENT '密码',
    `phone`         varchar(128)                        NULL COMMENT '电话',
    `userStatus`    int DEFAULT '0'                     NULL COMMENT '状态 0-正常',
    `createTime`    datetime DEFAULT CURRENT_TIMESTAMP  NULL COMMENT '创建时间',
    `updateTime`    datetime DEFAULT CURRENT_TIMESTAMP  NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `isDelete`      tinyint DEFAULT '0'                 NOT NULL COMMENT '是否删除'
) COMMENT='用户表' collate = utf8mb4_unicode_ci;
