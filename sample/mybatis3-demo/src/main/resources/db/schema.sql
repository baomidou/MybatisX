
CREATE TABLE IF NOT EXISTS `T_BLOG`
(
    id          BIGINT(20)     NOT NULL AUTO_INCREMENT,
    title       VARCHAR(30)    NULL DEFAULT NULL,
    content     VARCHAR(30)    NULL DEFAULT NULL,
    age         INTEGER(11)    NULL DEFAULT NULL,
    money       decimal(27, 9) NULL DEFAULT NULL,
    blob_text   BLOB           NULL DEFAULT NULL,
    create_time datetime       NULL DEFAULT NULL,
        PRIMARY KEY (id)
);
