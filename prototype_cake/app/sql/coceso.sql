SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

CREATE DATABASE `coceso` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `coceso`;

CREATE TABLE `incidents` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `modified` datetime NOT NULL,
  `finished` datetime DEFAULT NULL,
  `type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0-undefined; 1-Info; 2-Verlegung; 3-Auftrag/Einsatz',
  `priority` tinyint(1) NOT NULL DEFAULT '0',
  `text` text NOT NULL,
  `comment` text NOT NULL,
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0-Neu; 1-Offen; 2-Disponiert; 3-in Arbeit; 4-Abgeschlossen',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `incidents_units` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `incident_id` int(10) unsigned NOT NULL,
  `unit_id` int(10) unsigned NOT NULL,
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '0-Nicht zugewiesen/Abgeschlossen; 1-Zugewiesen; 2-ZBO; 3-BO; 4-ZAO; 5-AO',
  `modified` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `incidents_units` (`incident_id`,`unit_id`),
  KEY `unit_id` (`unit_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `units` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL,
  `short` varchar(30) NOT NULL,
  `type` enum('Trupp','KFZ') NOT NULL,
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0-AD; 1-NEB; 2-EB; 3-Bereitschaft',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `incidents_units`
  ADD CONSTRAINT `incidents_units_ibfk_1` FOREIGN KEY (`incident_id`) REFERENCES `incidents` (`id`),
  ADD CONSTRAINT `incidents_units_ibfk_2` FOREIGN KEY (`unit_id`) REFERENCES `units` (`id`);

GRANT USAGE ON *.* TO 'coceso'@'localhost' IDENTIFIED BY 'nJEJrp9xrZUQhQpy';
GRANT ALL PRIVILEGES ON `coceso`.* TO 'coceso'@'localhost';
