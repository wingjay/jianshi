CREATE TABLE IF NOT EXISTS `EventLog` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `user_id` bigint(20) NOT NULL,
    `event_name` text NOT NULL,
    `page_source` text,
    `time_created` bigint(20) unsigned NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
