-- phpMyAdmin SQL Dump
-- version 4.2.12deb1
-- http://www.phpmyadmin.net
--
-- Client :  localhost
-- Généré le :  Mer 16 Décembre 2015 à 10:49
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
-- Structure de la table `video`
--

CREATE TABLE IF NOT EXISTS `video` (
  `idvideo` int(11) NOT NULL,
  `filename` varchar(255) CHARACTER SET utf8 NOT NULL,
  `description` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `color` tinyint(4) NOT NULL DEFAULT '1',
  `resolution` varchar(5) CHARACTER SET utf8 NOT NULL DEFAULT 'HD',
  `ocs` tinyint(4) NOT NULL DEFAULT '0',
  `csa5` tinyint(4) NOT NULL DEFAULT '0',
  `format` varchar(5) CHARACTER SET utf8 NOT NULL DEFAULT '16/9',
  `status` varchar(20) CHARACTER SET utf8 NOT NULL DEFAULT 'NEW',
  `enabled` tinyint(4) NOT NULL DEFAULT '1',
  `protected` tinyint(4) NOT NULL DEFAULT '1',
  `audio` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `subtitle` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `pmt` text CHARACTER SET utf8
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Index pour les tables exportées
--

--
-- Index pour la table `video`
--
ALTER TABLE `video`
 ADD PRIMARY KEY (`idvideo`), ADD UNIQUE KEY `filename` (`filename`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
