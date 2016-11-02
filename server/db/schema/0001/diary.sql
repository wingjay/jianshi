CREATE TABLE IF NOT EXISTS `Diary` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `uuid` varchar(64) NOT NULL,
    `user_id` bigint(20) unsigned NOT NULL,
    `title` text NOT NULL,
    `content` text NOT NULL,
    `time_created` bigint(20) unsigned NOT NULL,
    `time_modified` bigint(20) unsigned NOT NULL default 0,
    `time_removed` bigint(20) unsigned NOT NULL default 0,
    PRIMARY KEY (`id`),
    UNIQUE (`uuid`, `user_id`, `time_removed`),
    INDEX `id-time_removed` (`id`, `time_removed`),
    INDEX `uuid-user_id-time_removed` (`uuid`, `user_id`, `time_removed`),
    INDEX `user_id-time_modified` (`user_id`, `time_modified`)
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;