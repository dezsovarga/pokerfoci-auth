CREATE DATABASE  IF NOT EXISTS `pokerfoci_auth`;
USE `pokerfoci_auth`;

--
-- Table structure for table `account`
--

DROP TABLE IF EXISTS `account`;
CREATE TABLE `account` (
  `id` bigint NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


DROP TABLE IF EXISTS `account_role`;
CREATE TABLE `account_role` (
  `account_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`account_id`,`role_id`),
  KEY `FKrs2s3m3039h0xt8d5yhwbuyam` (`role_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` bigint NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

ALTER TABLE Account CHANGE id id bigint(10) AUTO_INCREMENT;

INSERT INTO account (`email`,`first_name`,`last_name`,`password`,`username`,`active`,`skill`,`event_id`) VALUES ('szury@varga.com',NULL,NULL,'$2a$10$p3oF0jCZ8xPG9QkxSYHjZu4Y.LIsen8Cmjk1NuG6/Xcy94mtmA6T.','szury',1,0,NULL);
INSERT INTO account (`email`,`first_name`,`last_name`,`password`,`username`,`active`,`skill`,`event_id`) VALUES ('dezso@varga.com',NULL,NULL,'$2a$10$h6so626kXGNxko5xahMEHuIoRyT4KhlUs9xi40yEI2NvlBSCy2.iq','dezso',1,0,NULL);
INSERT INTO account (`email`,`first_name`,`last_name`,`password`,`username`,`active`,`skill`,`event_id`) VALUES ('kuki@kuki.com',NULL,NULL,'$2a$10$PbQZy2Fd5Skbkz/mDuSbeOrH9nWeUaFCrBWXSHehdFq3UEx2wmpL6','kuki',1,70,NULL);
INSERT INTO account (`email`,`first_name`,`last_name`,`password`,`username`,`active`,`skill`,`event_id`) VALUES ('vinitor@vinitor.com',NULL,NULL,'$2a$10$msrELENU8G7QPA5zdDljN.sq6KQ8UXBVepFZeMcFVSaVp0.M9vcsG','vinitor',1,65,NULL);
INSERT INTO account (`email`,`first_name`,`last_name`,`password`,`username`,`active`,`skill`,`event_id`) VALUES ('orban@orban.com',NULL,NULL,'$2a$10$eSKZf2dI1wJDk9j1x/YoFuuVSBglmeR6DZYlJT9vwc1OBy/bJMUJK','orban',1,68,NULL);
INSERT INTO account (`email`,`first_name`,`last_name`,`password`,`username`,`active`,`skill`,`event_id`) VALUES ('csabesz@csabesz.com',NULL,NULL,'$2a$10$TmOTQgshxQfB4apWLKw2OeDBLkDdZ3UF0u9zozYfB8gNlHIoLG5ie','csabesz',1,75,NULL);
INSERT INTO account (`email`,`first_name`,`last_name`,`password`,`username`,`active`,`skill`,`event_id`) VALUES ('andrei@andrei.com',NULL,NULL,'$2a$10$.MmxJkiaMpSMxqGHgdU0FOUprLGdKlF0jROKW3LCH7r2l9lQEBd6C','andrei',1,55,NULL);
INSERT INTO account (`email`,`first_name`,`last_name`,`password`,`username`,`active`,`skill`,`event_id`) VALUES ('szlo@szlo.com',NULL,NULL,'$2a$10$4eiIa1Bo3WJpNzTDGV0IbeeVgEIB2Vha8PYHaC2Mm9kHAHoBvrpqa','szlo',1,80,NULL);
INSERT INTO account (`email`,`first_name`,`last_name`,`password`,`username`,`active`,`skill`,`event_id`) VALUES ('tibi@tibi.com',NULL,NULL,'$2a$10$Lo.RABuH9YTl4AkT3XSL3eFNl5SiJficp3/bU/Q9FJfCZkPG3Gxk.','tibi',1,70,NULL);
INSERT INTO account (`email`,`first_name`,`last_name`,`password`,`username`,`active`,`skill`,`event_id`) VALUES ('pistike@varga.com',NULL,NULL,'$2a$10$rm3Nnpzo516uN0q4n68p3ulKZ7sbJVVkkUcYT6iz/ViUklgN3pQRK','pistike',1,75,NULL);
INSERT INTO account (`email`,`first_name`,`last_name`,`password`,`username`,`active`,`skill`,`event_id`) VALUES ('kuplung@varga.com',NULL,NULL,'$2a$10$Kt2wkIaFhbaFn3waZZBulO4BvTXzo3LmhzvCfvGMBh7nomrzXe.bS','kuplung',1,60,NULL);
INSERT INTO account (`email`,`first_name`,`last_name`,`password`,`username`,`active`,`skill`,`event_id`) VALUES ('horvathbotond@varga.com',NULL,NULL,'$2a$10$grCdyJ0w8yyQIGBQm5BbEObMSXWJfWfby2ivBcGEXXLRKoOyHyelW','horvathbotond',1,50,NULL);
INSERT INTO account (`email`,`first_name`,`last_name`,`password`,`username`,`active`,`skill`,`event_id`) VALUES ('mikloszsolt@varga.com',NULL,NULL,'$2a$10$Eo5rONtzo.xiMhlq8ktmO.Adrix9uhLvXlIO9IxsGI/c9UoU5qRbC','mikloszsolt',1,70,NULL);
INSERT INTO account (`email`,`first_name`,`last_name`,`password`,`username`,`active`,`skill`,`event_id`) VALUES ('dragos@dragos.com',NULL,NULL,'$2a$10$cjoEiekJCyPLi4paSx/.9eNV3gnUEasahylBthpYGxhgPYRU33sna','dragos',1,65,NULL);
INSERT INTO account (`email`,`first_name`,`last_name`,`password`,`username`,`active`,`skill`,`event_id`) VALUES ('atarr@atarr.com',NULL,NULL,'$2a$10$/oMoAOOSuyHrzBMb6Hd9Cu06lGzMXG4JdpwHJR3o3vIz0DEIpKDNi','atarr',1,75,NULL);
INSERT INTO account (`email`,`first_name`,`last_name`,`password`,`username`,`active`,`skill`,`event_id`) VALUES ('berti@berti.com',NULL,NULL,'$2a$10$POl.RvdGCRy/Xn3b/p0An.PIuux6.QYu6hisD8j8bMht40LnD8uxG','berti',1,60,NULL);
INSERT INTO account (`email`,`first_name`,`last_name`,`password`,`username`,`active`,`skill`,`event_id`) VALUES ('ferenc@ferenc.com',NULL,NULL,'$2a$10$mRzvARX1fjcDiz0XB3wjmePieXLwLN6E2XWzjwWHfn3J3/abQwY4i','ferenc',1,60,NULL);
INSERT INTO account (`email`,`first_name`,`last_name`,`password`,`username`,`active`,`skill`,`event_id`) VALUES ('piku@piku.com',NULL,NULL,'$2a$10$rfaMY3uLTkGjdhOWtihYa.NJYdtIva3q.b2XDHXJwYseSgDk5lQK6','piku',1,65,NULL);
