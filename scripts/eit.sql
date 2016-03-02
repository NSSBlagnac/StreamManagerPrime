-- phpMyAdmin SQL Dump
-- version 4.2.12deb1
-- http://www.phpmyadmin.net
--
-- Client :  localhost
-- Généré le :  Mer 16 Décembre 2015 à 10:48
-- Version du serveur :  5.5.40-0+wheezy1
-- Version de PHP :  5.6.2-1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Base de données :  `MyTVTools`
--

-- --------------------------------------------------------

--
-- Structure de la table `eit`
--

CREATE TABLE IF NOT EXISTS `eit` (
  `ideit` int(11) NOT NULL,
  `lcn` int(11) DEFAULT '0',
  `usi` int(11) DEFAULT '0',
  `name` varchar(128) DEFAULT NULL,
  `description` varchar(128) DEFAULT NULL,
  `user1` varchar(45) DEFAULT NULL,
  `section_0` mediumtext,
  `section_1` mediumtext,
  `enable` tinyint(4) DEFAULT '0',
  `eit_ts` bigint(20) DEFAULT NULL,
  `videofile` varchar(255) DEFAULT NULL,
  `videoid` int(11) NOT NULL,
  `address` varchar(15) NOT NULL,
  `port` int(11) DEFAULT NULL,
  `tsid` int(11) NOT NULL,
  `sid` int(11) NOT NULL,
  `onid` int(11) NOT NULL,
  `status` varchar(20) DEFAULT 'NEW',
  `lastUpdate` datetime DEFAULT NULL,
  `protected` tinyint(4) NOT NULL DEFAULT '0',
  `to_inject` tinyint(4) NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Index pour les tables exportées
--

--
-- Index pour la table `eit`
--
ALTER TABLE `eit`
 ADD PRIMARY KEY (`ideit`), ADD UNIQUE KEY `address_UNIQUE` (`address`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
