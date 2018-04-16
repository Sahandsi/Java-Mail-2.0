-- phpMyAdmin SQL Dump
-- version 3.5.4
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Apr 16, 2018 at 01:27 PM
-- Server version: 5.1.73-log
-- PHP Version: 7.0.10

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `b5038121_db2`
--

-- --------------------------------------------------------

--
-- Table structure for table `Accounts`
--

CREATE TABLE IF NOT EXISTS `Accounts` (
  `acctnum` int(11) NOT NULL,
  `surname` varchar(45) NOT NULL,
  `firstNames` varchar(45) NOT NULL,
  `balance` float NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `Accounts`
--

INSERT INTO `Accounts` (`acctnum`, `surname`, `firstNames`, `balance`) VALUES
(234567, 'White', 'Peter', 35.6),
(333333, 'Jones', 'Sally', 5000),
(112233, 'Smith', 'John James', 752.85);

-- --------------------------------------------------------

--
-- Table structure for table `Email`
--

CREATE TABLE IF NOT EXISTS `Email` (
  `EmailID` int(11) NOT NULL AUTO_INCREMENT,
  `usernameTo` varchar(30) NOT NULL,
  `usernameFrom` varchar(30) NOT NULL,
  `message` varchar(1000) NOT NULL,
  `attachmentFile` varbinary(8000) NOT NULL,
  `attachment` varchar(50) NOT NULL,
  PRIMARY KEY (`EmailID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `Marks`
--

CREATE TABLE IF NOT EXISTS `Marks` (
  `StudentID` int(11) NOT NULL,
  `Mark` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `Marks`
--

INSERT INTO `Marks` (`StudentID`, `Mark`) VALUES
(1, 1),
(2, 2);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
