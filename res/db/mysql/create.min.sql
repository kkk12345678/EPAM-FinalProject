START TRANSACTION;

DROP DATABASE IF EXISTS `ubd01_epamfinal`;

CREATE DATABASE `ubd01_epamfinal`
    CHARACTER SET utf8mb4;

USE `ubd01_epamfinal`;

CREATE TABLE `languages` (
                             `id` INT AUTO_INCREMENT PRIMARY KEY,
                             `name` VARCHAR(20) UNIQUE NOT NULL,
                             `image` VARCHAR(256) DEFAULT NULL,
                             `code` VARCHAR(10) DEFAULT NULL,
                             `locale` VARCHAR(10) DEFAULT NULL
) ENGINE=INNODB;

INSERT INTO `languages` (`id`, `name`, `image`, `code`, `locale`) VALUES
(1, 'English', '/static/img/language/en.png', NULL, 'en'),
(2, 'Українська', '/static/img/language/ua.png', NULL, 'ua');

CREATE TABLE `publishers` (
                              `id` INT AUTO_INCREMENT PRIMARY KEY,
                              `tag` VARCHAR(256) UNIQUE NOT NULL
) ENGINE=INNODB;

CREATE TABLE `publisher_descriptions` (
                                          `publisher_id` INT NOT NULL,
                                          `language_id` INT NOT NULL,
                                          `name` VARCHAR(256) NOT NULL,
                                          `description` TEXT,
                                          PRIMARY KEY (`publisher_id` , `language_id`),
                                          FOREIGN KEY (`publisher_id`) REFERENCES `publishers` (`id`) ON UPDATE RESTRICT ON DELETE CASCADE,
                                          FOREIGN KEY (`language_id`) REFERENCES `languages` (`id`) ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE=INNODB;

CREATE TABLE `categories` (
                              `id` INT AUTO_INCREMENT PRIMARY KEY,
                              `tag` VARCHAR(256) UNIQUE NOT NULL
) ENGINE=INNODB;

CREATE TABLE `category_descriptions` (
                                         `category_id` INT NOT NULL,
                                         `language_id` INT NOT NULL,
                                         `name` VARCHAR(256) NOT NULL,
                                         `description` TEXT,
                                         PRIMARY KEY (`category_id` , `language_id`),
                                         FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON UPDATE RESTRICT ON DELETE CASCADE,
                                         FOREIGN KEY (`language_id`) REFERENCES `languages` (`id`) ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE=INNODB;

CREATE TABLE `books` (
                         `id` INT AUTO_INCREMENT PRIMARY KEY,
                         `isbn` VARCHAR(13) UNIQUE NOT NULL,
                         `quantity` INT NOT NULL DEFAULT '0',
                         `publisher_id` INT NOT NULL,
                         `price` DECIMAL(10,00) NOT NULL DEFAULT '0',
                         `publishing_date` DATE NOT NULL,
                         `category_id` INT NOT NULL,
                         `tag` VARCHAR(256) UNIQUE NOT NULL,
                         FOREIGN KEY (`publisher_id`) REFERENCES `publishers` (`id`) ON UPDATE RESTRICT ON DELETE RESTRICT,
                         FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE=INNODB;

CREATE TABLE `book_descriptions` (
                                     `book_id` INT NOT NULL,
                                     `language_id` INT NOT NULL,
                                     `description` TEXT,
                                     `title` VARCHAR(256) NOT NULL,
                                     PRIMARY KEY (`book_id` , `language_id`),
                                     FOREIGN KEY (`book_id`) REFERENCES `books` (`id`) ON UPDATE RESTRICT ON DELETE CASCADE,
                                     FOREIGN KEY (`language_id`) REFERENCES `languages` (`id`) ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE=INNODB;

CREATE TABLE `users` (
                         `id` INT AUTO_INCREMENT PRIMARY KEY,
                         `email` VARCHAR(96) UNIQUE NOT NULL,
                         `password` VARCHAR(64) NOT NULL,
                         `is_admin` TINYINT NOT NULL,
                         `first_name` VARCHAR(96) NOT NULL,
                         `last_name` VARCHAR(96) NOT NULL,
                         `is_blocked` TINYINT NOT NULL
) ENGINE=INNODB;

INSERT INTO `users` (`id`, `email`, `password`, `is_admin`, `first_name`, `last_name`, `is_blocked`) VALUES
(1, 'kostiantyn.korolkov@gmail.com', '15d09b0846d4753f15c0d6487e332530b6797a3a001845440916f77ba1433f7c', 1, 'Костянтин', 'Корольков', 0);

CREATE TABLE `statuses` (
                            `id` INT AUTO_INCREMENT PRIMARY KEY,
                            `name` VARCHAR(20) UNIQUE NOT NULL
) ENGINE=INNODB;

INSERT INTO `statuses` (`id`, `name`) VALUES
(1, 'registered'),
(2, 'paid'),
(3, 'canceled');

CREATE TABLE `orders` (
                          `id` INT AUTO_INCREMENT PRIMARY KEY,
                          `customer_id` INT NOT NULL,
                          `status_id` INT NOT NULL DEFAULT '1',
                          `date_added` DATE NOT NULL,
                          `total` DECIMAL(10,00) NOT NULL,
                          FOREIGN KEY (`customer_id`) REFERENCES `users` (`id`) ON UPDATE RESTRICT ON DELETE RESTRICT,
                          FOREIGN KEY (`status_id`) REFERENCES `statuses` (`id`) ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE=INNODB;

CREATE TABLE `order_details` (
                                 `order_id` INT NOT NULL,
                                 `book_id` INT NOT NULL,
                                 `quantity` INT NOT NULL,
                                 PRIMARY KEY (`order_id`,`book_id`),
                                 FOREIGN KEY (`book_id`) REFERENCES `books` (`id`) ON UPDATE RESTRICT ON DELETE RESTRICT,
                                 FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE=INNODB;

COMMIT;

