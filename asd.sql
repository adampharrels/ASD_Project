-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- 主機： 127.0.0.1
-- 產生時間： 2025 年 10 月 01 日 00:26
-- 伺服器版本： 10.6.5-MariaDB
-- PHP 版本： 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- 資料庫： `asd`
--

-- --------------------------------------------------------

--
-- 資料表結構 `booktime`
--

CREATE TABLE `booktime` (
  `timeID` int(15) UNSIGNED NOT NULL,
  `room_id` int(15) UNSIGNED NOT NULL,
  `start_Time` datetime NOT NULL,
  `end_Time` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- 傾印資料表的資料 `booktime`
--

INSERT INTO `booktime` (`timeID`, `room_id`, `start_Time`, `end_Time`) VALUES
(0, 0, '0000-00-00 00:00:00', '0000-00-00 03:30:00'),
(1, 1, '2025-10-01 12:30:21', '2025-10-01 13:00:00'),
(2, 4, '2025-10-01 14:24:00', '2025-10-01 15:30:00'),
(3, 4, '2025-10-01 15:30:00', '2025-10-01 17:00:00');

-- --------------------------------------------------------

--
-- 資料表結構 `room`
--

CREATE TABLE `room` (
  `room_id` int(15) UNSIGNED NOT NULL,
  `room_name` varchar(12) NOT NULL,
  `room_type` varchar(50) NOT NULL,
  `capacity` int(1) UNSIGNED NOT NULL,
  `speaker` tinyint(1) NOT NULL,
  `whiteboard` tinyint(1) NOT NULL,
  `monitor` tinyint(1) NOT NULL,
  `hdmi_cable` tinyint(1) NOT NULL,
  `image` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- 傾印資料表的資料 `room`
--

INSERT INTO `room` (`room_id`, `room_name`, `room_type`, `capacity`, `speaker`, `whiteboard`, `monitor`, `hdmi_cable`, `image`) VALUES
(1, 'CB06.06.112', 'Group Study Room', 8, 1, 1, 1, 1, 'Group_Study_Room'),
(2, 'CB06.06.113', 'Group Study Room', 8, 0, 0, 1, 1, 'Group_Study_Room'),
(3, 'CB07.02.010A', 'Online Learning Room', 2, 0, 0, 1, 1, 'Online_Learning_Room'),
(4, 'CB07.02.010B', 'Online Learning Room', 2, 1, 0, 1, 1, 'Online_Learning_Room');

--
-- 已傾印資料表的索引
--

--
-- 資料表索引 `booktime`
--
ALTER TABLE `booktime`
  ADD PRIMARY KEY (`timeID`);

--
-- 資料表索引 `room`
--
ALTER TABLE `room`
  ADD PRIMARY KEY (`room_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
