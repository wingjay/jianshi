CREATE TABLE IF NOT EXISTS `User` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `name` varchar(255),
    `email` varchar(255) NOT NULL,
    `email_hash` bigint(20) NOT NULL,
    `password` varchar(255) NOT NULL,
    `time_created` bigint(20) unsigned NOT NULL,
    `time_modified` bigint(20) unsigned NOT NULL,
    `time_removed` bigint(20) unsigned NOT NULL default 0,
    PRIMARY KEY (`id`),
    UNIQUE (`email_hash`, `time_removed`),
    INDEX `user_id` (`id`, `time_removed`),
    INDEX `time_created` (`time_created`, `time_removed`)
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;