-- phpMyAdmin SQL Dump
-- version 4.7.7
-- https://www.phpmyadmin.net/
--
-- Host: localhost:8889
-- Generation Time: Apr 17, 2018 at 11:20 AM
-- Server version: 5.6.38
-- PHP Version: 7.2.1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

--
-- Database: `java`
--

-- --------------------------------------------------------

--
-- Table structure for table `Accounts`
--

CREATE TABLE `Accounts` (
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

CREATE TABLE `Email` (
  `EmailID` int(11) NOT NULL,
  `usernameTo` varchar(30) NOT NULL,
  `usernameFrom` varchar(30) NOT NULL,
  `emailSubject` varchar(50) NOT NULL,
  `message` varchar(1000) NOT NULL,
  `attachmentFile` varbinary(8000) DEFAULT NULL,
  `attachment` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `Email`
--

INSERT INTO `Email` (`EmailID`, `usernameTo`, `usernameFrom`, `emailSubject`, `message`, `attachmentFile`, `attachment`) VALUES
(47, 'U1', 'U1', 'i am the subject', 'hihihi', NULL, NULL),
(48, 'U1', 'U1', 'hi', 'hi', 0x5b42403466356532333032, 'OraclePromo(2).flv');

-- --------------------------------------------------------

--
-- Table structure for table `Marks`
--

CREATE TABLE `Marks` (
  `StudentID` int(11) NOT NULL,
  `Mark` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `Marks`
--

INSERT INTO `Marks` (`StudentID`, `Mark`) VALUES
(1, 1),
(2, 2);

-- --------------------------------------------------------

--
-- Table structure for table `Users`
--

CREATE TABLE `Users` (
  `ID` int(3) NOT NULL,
  `Name` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `Users`
--

INSERT INTO `Users` (`ID`, `Name`) VALUES
(1, 'U1'),
(2, 'U2'),
(3, 'U3'),
(4, 'U4'),
(5, 'pouyt'),
(6, 'dadashi'),
(7, 'Sambad'),
(8, 'soheyl'),
(9, '');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `Email`
--
ALTER TABLE `Email`
  ADD PRIMARY KEY (`EmailID`);

--
-- Indexes for table `Users`
--
ALTER TABLE `Users`
  ADD PRIMARY KEY (`ID`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `Email`
--
ALTER TABLE `Email`
  MODIFY `EmailID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=49;

--
-- AUTO_INCREMENT for table `Users`
--
ALTER TABLE `Users`
  MODIFY `ID` int(3) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;
