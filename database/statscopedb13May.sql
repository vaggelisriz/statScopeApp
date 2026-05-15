-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Εξυπηρετητής: 127.0.0.1
-- Χρόνος δημιουργίας: 13 Μάη 2026 στις 21:44:24
-- Έκδοση διακομιστή: 10.4.32-MariaDB
-- Έκδοση PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Βάση δεδομένων: `statscopedb`
--

-- --------------------------------------------------------

--
-- Δομή πίνακα για τον πίνακα `championships`
--

CREATE TABLE `championships` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Άδειασμα δεδομένων του πίνακα `championships`
--

INSERT INTO `championships` (`id`, `name`, `created_at`) VALUES
(1, 'SuperLeague', '2026-05-09 12:44:47'),
(2, 'Finals', '2026-05-09 12:47:14'),
(3, 'Placement Round', '2026-05-09 12:48:51');

-- --------------------------------------------------------

--
-- Δομή πίνακα για τον πίνακα `matches`
--

CREATE TABLE `matches` (
  `id` int(11) NOT NULL,
  `championship_id` int(11) DEFAULT NULL,
  `home_team_id` int(11) DEFAULT NULL,
  `away_team_id` int(11) DEFAULT NULL,
  `match_round` int(11) DEFAULT NULL,
  `home_score` int(11) DEFAULT 0,
  `away_score` int(11) DEFAULT 0,
  `status` enum('scheduled','live','completed') DEFAULT 'scheduled'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Άδειασμα δεδομένων του πίνακα `matches`
--

INSERT INTO `matches` (`id`, `championship_id`, `home_team_id`, `away_team_id`, `match_round`, `home_score`, `away_score`, `status`) VALUES
(79, 1, 1, 4, 1, 0, 0, 'scheduled'),
(80, 1, 2, 3, 1, 0, 0, 'scheduled'),
(81, 1, 1, 3, 2, 0, 0, 'scheduled'),
(82, 1, 4, 2, 2, 0, 0, 'scheduled'),
(83, 1, 1, 2, 3, 0, 0, 'scheduled'),
(84, 1, 3, 4, 3, 0, 0, 'scheduled'),
(85, 2, 1, 4, 1, 0, 0, 'scheduled'),
(86, 2, 2, 3, 1, 0, 0, 'scheduled'),
(87, 2, 1, 3, 2, 0, 0, 'scheduled'),
(88, 2, 4, 2, 2, 0, 0, 'scheduled'),
(89, 2, 1, 2, 3, 0, 0, 'scheduled'),
(90, 2, 3, 4, 3, 0, 0, 'scheduled'),
(91, 3, 4, 3, 1, 0, 0, 'scheduled');

-- --------------------------------------------------------

--
-- Δομή πίνακα για τον πίνακα `match_events`
--

CREATE TABLE `match_events` (
  `id` int(11) NOT NULL,
  `match_id` int(11) NOT NULL,
  `player_id` int(11) NOT NULL,
  `event_type` enum('shot','tackle','pass','cross','assist','mistake','foul_won','foul_committed','corner_won','card_yellow','card_red','card_won_yellow','card_won_red') NOT NULL,
  `outcome` enum('success','failure','goal','saved','off_target','blocked') DEFAULT 'success',
  `event_minute` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Δομή πίνακα για τον πίνακα `players`
--

CREATE TABLE `players` (
  `id` int(11) NOT NULL,
  `team_id` int(11) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `position` varchar(50) DEFAULT NULL,
  `photo` varchar(255) DEFAULT NULL,
  `age` int(11) DEFAULT NULL,
  `number` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Άδειασμα δεδομένων του πίνακα `players`
--

INSERT INTO `players` (`id`, `team_id`, `name`, `position`, `photo`, `age`, `number`) VALUES
(82, 5, 'N. Fadiga', 'Defender', 'https://media.api-sports.io/football/players/82.png', 26, 27),
(118, 3, 'Gelson Martins', 'Midfielder', 'https://media.api-sports.io/football/players/118.png', 30, 10),
(141, 13, 'M. Wagué', 'Defender', 'https://media.api-sports.io/football/players/141.png', 27, 22),
(179, 2, 'M. Sissoko', 'Midfielder', 'https://media.api-sports.io/football/players/179.png', 36, 17),
(206, 4, 'João Mário', 'Midfielder', 'https://media.api-sports.io/football/players/206.png', 33, 10),
(220, 11, 'E. Salcedo', 'Attacker', 'https://media.api-sports.io/football/players/220.png', 24, 9),
(287, 1, 'D. Lovren', 'Defender', 'https://media.api-sports.io/football/players/287.png', 36, 6),
(402, 5, 'H. Mendyl', 'Defender', 'https://media.api-sports.io/football/players/402.png', 28, 37),
(513, 2, 'Renato Sanches', 'Midfielder', 'https://media.api-sports.io/football/players/513.png', 28, 8),
(579, 1, 'A. Živković', 'Midfielder', 'https://media.api-sports.io/football/players/579.png', 29, 14),
(586, 6, 'P. Tsintotas', 'Goalkeeper', 'https://media.api-sports.io/football/players/586.png', 32, 16),
(594, 11, 'V. Lampropoulos', 'Defender', 'https://media.api-sports.io/football/players/594.png', 35, 24),
(602, 5, 'K. Galanopoulos', 'Midfielder', 'https://media.api-sports.io/football/players/602.png', 28, 6),
(606, 4, 'P. Mantalos', 'Midfielder', 'https://media.api-sports.io/football/players/606.png', 34, 20),
(607, 2, 'A. Bakasetas', 'Midfielder', 'https://media.api-sports.io/football/players/607.png', 32, 11),
(609, 5, 'G. Gianniotas', 'Attacker', 'https://media.api-sports.io/football/players/609.png', 32, 70),
(684, 1, 'Taison', 'Attacker', 'https://media.api-sports.io/football/players/684.png', 37, 11),
(824, 10, 'H. Magnússon', 'Defender', 'https://media.api-sports.io/football/players/824.png', 32, 32),
(969, 2, 'T. Jedvaj', 'Defender', 'https://media.api-sports.io/football/players/969.png', 30, 21),
(971, 3, 'P. Retsos', 'Defender', 'https://media.api-sports.io/football/players/971.png', 27, 45),
(1217, 1, 'M. Ozdoev', 'Midfielder', 'https://media.api-sports.io/football/players/1217.png', 33, 27),
(1490, 12, 'Miguel Luís', 'Midfielder', 'https://media.api-sports.io/football/players/1490.png', 26, 90),
(1584, 5, 'Loren Morón', 'Attacker', 'https://media.api-sports.io/football/players/1584.png', 32, 80),
(1587, 8, 'L. Choutesiotis', 'Goalkeeper', 'https://media.api-sports.io/football/players/1587.png', 31, 1),
(1589, 10, 'Y. Lodygin', 'Goalkeeper', 'https://media.api-sports.io/football/players/1589.png', 35, 12),
(1591, 3, 'K. Tzolakis', 'Goalkeeper', 'https://media.api-sports.io/football/players/1591.png', 23, 88),
(1602, 11, 'T. Androutsos', 'Midfielder', 'https://media.api-sports.io/football/players/1602.png', 28, 14),
(1603, 12, 'A. Bouchalakis', 'Midfielder', 'https://media.api-sports.io/football/players/1603.png', 32, 41),
(1604, 1, 'M. Camara', 'Midfielder', 'https://media.api-sports.io/football/players/1604.png', 28, 2),
(1605, 3, 'Daniel Podence', 'Midfielder', 'https://media.api-sports.io/football/players/1605.png', 30, 56),
(1627, 2, 'D. Calabria', 'Defender', 'https://media.api-sports.io/football/players/1627.png', 29, 2),
(1733, 13, 'A. Ivan', 'Attacker', 'https://media.api-sports.io/football/players/1733.png', 28, 9),
(1818, 4, 'M. Gaćinović', 'Midfielder', 'https://media.api-sports.io/football/players/1818.png', 30, 8),
(1828, 4, 'L. Jović', 'Attacker', 'https://media.api-sports.io/football/players/1828.png', 28, 9),
(1835, 4, 'T. Strakosha', 'Goalkeeper', 'https://media.api-sports.io/football/players/1835.png', 30, 1),
(1932, 8, 'J. Uronen', 'Defender', 'https://media.api-sports.io/football/players/1932.png', 31, 21),
(1986, 4, 'D. Vida', 'Defender', 'https://media.api-sports.io/football/players/1986.png', 36, 21),
(2118, 4, 'R. Marin', 'Midfielder', 'https://media.api-sports.io/football/players/2118.png', 29, 18),
(2164, 1, 'T. Kędziora', 'Defender', 'https://media.api-sports.io/football/players/2164.png', 31, 16),
(2183, 10, 'B. Verbič', 'Midfielder', 'https://media.api-sports.io/football/players/2183.png', 32, 77),
(2321, 6, 'E. Yablonskiy', 'Midfielder', 'https://media.api-sports.io/football/players/2321.png', 30, 5),
(2355, 3, 'A. Paschalakis', 'Goalkeeper', 'https://media.api-sports.io/football/players/2355.png', 36, 1),
(2357, 14, 'M. Siampanis', 'Goalkeeper', 'https://media.api-sports.io/football/players/2357.png', 26, 1),
(2361, 2, 'S. Ingason', 'Defender', 'https://media.api-sports.io/football/players/2361.png', 32, 15),
(2364, 13, 'L. Lyratzis', 'Defender', 'https://media.api-sports.io/football/players/2364.png', 25, 19),
(2374, 1, 'D. Pelkas', 'Midfielder', 'https://media.api-sports.io/football/players/2374.png', 32, 10),
(2377, 8, 'T. Tsingaras', 'Midfielder', 'https://media.api-sports.io/football/players/2377.png', 25, 5),
(2381, 13, 'N. Karelis', 'Attacker', 'https://media.api-sports.io/football/players/2381.png', 33, 80),
(2385, 2, 'K. Świderski', 'Attacker', 'https://media.api-sports.io/football/players/2385.png', 28, 19),
(2386, 8, 'G. Tzovaras', 'Attacker', 'https://media.api-sports.io/football/players/2386.png', 26, 99),
(2446, 14, 'J. Añor', 'Midfielder', 'https://media.api-sports.io/football/players/2446.png', 31, 10),
(2458, 14, 'Jan Hurtado', 'Attacker', 'https://media.api-sports.io/football/players/2458.png', 25, 9),
(2475, 4, 'R. Pereyra', 'Midfielder', 'https://media.api-sports.io/football/players/2475.png', 34, 37),
(2722, 3, 'A. El Kaabi', 'Attacker', 'https://media.api-sports.io/football/players/2722.png', 32, 9),
(2785, 14, 'H. Hermannsson', 'Defender', 'https://media.api-sports.io/football/players/2785.png', 30, 30),
(2811, 8, 'S. Zuber', 'Midfielder', 'https://media.api-sports.io/football/players/2811.png', 34, 77),
(2878, 1, 'J. Sánchez', 'Defender', 'https://media.api-sports.io/football/players/2878.png', 28, 35),
(3084, 5, 'T. Kadewere', 'Attacker', 'https://media.api-sports.io/football/players/3084.png', 29, 9),
(3575, 10, 'E. Çokaj', 'Midfielder', 'https://media.api-sports.io/football/players/3575.png', 26, 23),
(5215, 9, 'L. Garate', 'Attacker', 'https://media.api-sports.io/football/players/5215.png', 32, 23),
(5216, 12, 'J. García', 'Attacker', 'https://media.api-sports.io/football/players/5216.png', 33, 31),
(5585, 6, 'J. Mendieta', 'Midfielder', 'https://media.api-sports.io/football/players/5585.png', 32, 32),
(5727, 6, 'J. Bartolo', 'Attacker', 'https://media.api-sports.io/football/players/5727.png', 29, 7),
(5974, 6, 'J. Chicco', 'Midfielder', 'https://media.api-sports.io/football/players/5974.png', 27, 14),
(6064, 3, 'F. Ortega', 'Defender', 'https://media.api-sports.io/football/players/6064.png', 26, 3),
(6252, 10, 'S. Palacios', 'Attacker', 'https://media.api-sports.io/football/players/6252.png', 33, 34),
(6446, 14, 'M. Comba', 'Midfielder', 'https://media.api-sports.io/football/players/6446.png', 31, 20),
(6697, 2, 'L. Chaves', 'Goalkeeper', 'https://media.api-sports.io/football/players/6697.png', 30, 12),
(7398, 4, 'B. Varga', 'Attacker', 'https://media.api-sports.io/football/players/7398.png', 31, 25),
(7538, 8, 'P. Michorl', 'Midfielder', 'https://media.api-sports.io/football/players/7538.png', 30, 8),
(7625, 11, 'T. Fountas', 'Attacker', 'https://media.api-sports.io/football/players/7625.png', 30, 11),
(7627, 4, 'R. Ljubičić', 'Midfielder', 'https://media.api-sports.io/football/players/7627.png', 26, 23),
(8445, 9, 'D. Batubinsika', 'Defender', 'https://media.api-sports.io/football/players/8445.png', 29, 28),
(8543, 4, 'A. Koita', 'Midfielder', 'https://media.api-sports.io/football/players/8543.png', 27, 11),
(8557, 14, 'Núrio Fortuna', 'Defender', 'https://media.api-sports.io/football/players/8557.png', 30, 25),
(8646, 7, 'L. Amani', 'Midfielder', 'https://media.api-sports.io/football/players/8646.png', 27, 88),
(8730, 8, 'D. Jubitana', 'Attacker', 'https://media.api-sports.io/football/players/8730.png', 26, 11),
(8777, 2, 'A. Touba', 'Defender', 'https://media.api-sports.io/football/players/8777.png', 27, 5),
(9672, 8, 'Mansur', 'Defender', 'https://media.api-sports.io/football/players/9672.png', 32, 70),
(9914, 5, 'Fabiano', 'Defender', 'https://media.api-sports.io/football/players/9914.png', 34, 4),
(10157, 3, 'Rodinei', 'Midfielder', 'https://media.api-sports.io/football/players/10157.png', 33, 23),
(11282, 10, 'A. Ožbolt', 'Attacker', 'https://media.api-sports.io/football/players/11282.png', 29, 9),
(12797, 13, 'Alex Teixeira', 'Attacker', 'https://media.api-sports.io/football/players/12797.png', 36, 11),
(14361, 11, 'K. Krizmanić', 'Defender', 'https://media.api-sports.io/football/players/14361.png', 25, 2),
(14394, 1, 'L. Ivanušec', 'Midfielder', 'https://media.api-sports.io/football/players/14394.png', 27, 18),
(14465, 5, 'L. Majkić', 'Goalkeeper', 'https://media.api-sports.io/football/players/14465.png', 26, 21),
(15347, 10, 'I. Kosti', 'Midfielder', 'https://media.api-sports.io/football/players/15347.png', 25, 18),
(15373, 12, 'Christian Manrique', 'Defender', 'https://media.api-sports.io/football/players/15373.png', 27, 15),
(15386, 12, 'C. Mavrias', 'Defender', 'https://media.api-sports.io/football/players/15386.png', 31, 35),
(15735, 8, 'T. van Weert', 'Attacker', 'https://media.api-sports.io/football/players/15735.png', 35, 9),
(18760, 1, 'J. Kenny', 'Defender', 'https://media.api-sports.io/football/players/18760.png', 28, 3),
(19270, 4, 'N. Eliasson', 'Midfielder', 'https://media.api-sports.io/football/players/19270.png', 30, 19),
(20294, 12, 'C. Sielis', 'Defender', 'https://media.api-sports.io/football/players/20294.png', 25, 16),
(20689, 4, 'H. Moukoudi', 'Defender', 'https://media.api-sports.io/football/players/20689.png', 28, 2),
(21101, 8, 'S. Moutoussamy', 'Midfielder', 'https://media.api-sports.io/football/players/21101.png', 29, 92),
(21630, 13, 'Y. Armougom', 'Defender', 'https://media.api-sports.io/football/players/21630.png', 27, 31),
(21996, 1, 'A. Baba', 'Defender', 'https://media.api-sports.io/football/players/21996.png', 31, 21),
(22141, 6, 'I. Sylla', 'Defender', 'https://media.api-sports.io/football/players/22141.png', 31, 4),
(22153, 11, 'A. Leya', 'Attacker', 'https://media.api-sports.io/football/players/22153.png', 28, 99),
(23301, 5, 'M. Alfarela', 'Attacker', 'https://media.api-sports.io/football/players/23301.png', 28, 19),
(24284, 9, 'Y. Maçon', 'Defender', 'https://media.api-sports.io/football/players/24284.png', 27, 27),
(24617, 11, 'L. Shengelia', 'Midfielder', 'https://media.api-sports.io/football/players/24617.png', 30, 27),
(24665, 8, 'L. Gugeshashvili', 'Goalkeeper', 'https://media.api-sports.io/football/players/24665.png', 26, 25),
(25299, 5, 'F. Jensen', 'Midfielder', 'https://media.api-sports.io/football/players/25299.png', 28, 97),
(25310, 1, 'J. Pavlenka', 'Goalkeeper', 'https://media.api-sports.io/football/players/25310.png', 33, 1),
(25350, 4, 'M. Grujić', 'Midfielder', 'https://media.api-sports.io/football/players/25350.png', 29, 4),
(26313, 5, 'A. Donis', 'Attacker', 'https://media.api-sports.io/football/players/26313.png', 29, 11),
(26568, 8, 'M. Baku', 'Attacker', 'https://media.api-sports.io/football/players/26568.png', 27, 32),
(26591, 10, 'J. Abu Hanna', 'Defender', 'https://media.api-sports.io/football/players/26591.png', 27, 4),
(26648, 13, 'A. Karasalidis', 'Defender', 'https://media.api-sports.io/football/players/26648.png', 34, 4),
(26679, 9, 'T. Papageorgiou', 'Defender', 'https://media.api-sports.io/football/players/26679.png', 38, 22),
(26681, 8, 'D. Stavrópoulos', 'Defender', 'https://media.api-sports.io/football/players/26681.png', 28, 4),
(26696, 8, 'S. Tsiloulis', 'Midfielder', 'https://media.api-sports.io/football/players/26696.png', 30, 23),
(26698, 10, 'G. Manthatis', 'Defender', 'https://media.api-sports.io/football/players/26698.png', 28, 14),
(26701, 11, 'P. Katsikas', 'Goalkeeper', 'https://media.api-sports.io/football/players/26701.png', 26, 13),
(26702, 9, 'N. Melissas', 'Goalkeeper', 'https://media.api-sports.io/football/players/26702.png', 32, 1),
(26723, 6, 'K. Bouloulis', 'Midfielder', 'https://media.api-sports.io/football/players/26723.png', 32, 12),
(26730, 5, 'S. Dioudis', 'Goalkeeper', 'https://media.api-sports.io/football/players/26730.png', 33, 13),
(26732, 7, 'V. Xenopoulos', 'Goalkeeper', 'https://media.api-sports.io/football/players/26732.png', 27, 1),
(26733, 11, 'I. Chatzitheodoridis', 'Defender', 'https://media.api-sports.io/football/players/26733.png', 28, 12),
(26740, 9, 'E. Pantelakis', 'Defender', 'https://media.api-sports.io/football/players/26740.png', 30, 6),
(26741, 11, 'A. Poungouras', 'Defender', 'https://media.api-sports.io/football/players/26741.png', 30, 15),
(26744, 9, 'K. Apostolakis', 'Defender', 'https://media.api-sports.io/football/players/26744.png', 24, 2),
(26747, 14, 'I. Bouzoukis', 'Midfielder', 'https://media.api-sports.io/football/players/26747.png', 27, 18),
(26755, 9, 'P. Staikos', 'Midfielder', 'https://media.api-sports.io/football/players/26755.png', 29, 26),
(26756, 6, 'T. Tzandaris', 'Midfielder', 'https://media.api-sports.io/football/players/26756.png', 32, 8),
(26757, 6, 'D. Emmanouilidis', 'Attacker', 'https://media.api-sports.io/football/players/26757.png', 25, 11),
(26759, 6, 'F. Macheda', 'Attacker', 'https://media.api-sports.io/football/players/26759.png', 34, 41),
(26771, 11, 'N. Marinakis', 'Defender', 'https://media.api-sports.io/football/players/26771.png', 32, 4),
(26775, 14, 'T. Tsokanis', 'Midfielder', 'https://media.api-sports.io/football/players/26775.png', 34, 6),
(26821, 5, 'G. Athanasiadis', 'Goalkeeper', 'https://media.api-sports.io/football/players/26821.png', 32, 33),
(26823, 6, 'N. Papadopoulos', 'Goalkeeper', 'https://media.api-sports.io/football/players/26823.png', 35, 1),
(26826, 11, 'G. Christopoulos', 'Defender', 'https://media.api-sports.io/football/players/26826.png', 25, 22),
(26828, 2, 'G. Kiriakopoulos', 'Defender', 'https://media.api-sports.io/football/players/26828.png', 29, 77),
(26833, 6, 'K. Triantafyllópoulos', 'Defender', 'https://media.api-sports.io/football/players/26833.png', 32, 13),
(26847, 6, 'N. Kaltsas', 'Attacker', 'https://media.api-sports.io/football/players/26847.png', 35, 20),
(26848, 2, 'G. Kotsiras', 'Defender', 'https://media.api-sports.io/football/players/26848.png', 33, 27),
(26853, 12, 'Ž. Živković', 'Goalkeeper', 'https://media.api-sports.io/football/players/26853.png', 36, 12),
(26862, 1, 'K. Thymianis', 'Defender', 'https://media.api-sports.io/football/players/26862.png', 24, 25),
(26891, 6, 'P. Deligiannidis', 'Midfielder', 'https://media.api-sports.io/football/players/26891.png', 29, 64),
(26902, 1, 'G. Giakoumakis', 'Attacker', 'https://media.api-sports.io/football/players/26902.png', 31, 7),
(26905, 11, 'J. Neira', 'Attacker', 'https://media.api-sports.io/football/players/26905.png', 36, 10),
(26912, 9, 'T. Venetikidis', 'Goalkeeper', 'https://media.api-sports.io/football/players/26912.png', 24, 21),
(26916, 14, 'G. Kargas', 'Defender', 'https://media.api-sports.io/football/players/26916.png', 31, 4),
(26922, 13, 'A. Liasos', 'Midfielder', 'https://media.api-sports.io/football/players/26922.png', 25, 8),
(26936, 4, 'S. Pilios', 'Defender', 'https://media.api-sports.io/football/players/26936.png', 25, 3),
(26948, 10, 'P. Liagas', 'Defender', 'https://media.api-sports.io/football/players/26948.png', 26, 24),
(26952, 11, 'Z. Karachalios', 'Midfielder', 'https://media.api-sports.io/football/players/26952.png', 29, 6),
(26957, 10, 'G. Nikas', 'Midfielder', 'https://media.api-sports.io/football/players/26957.png', 26, 8),
(26962, 10, 'M. Vichos', 'Defender', 'https://media.api-sports.io/football/players/26962.png', 25, 3),
(27035, 8, 'D. Tsakmakis', 'Defender', 'https://media.api-sports.io/football/players/27035.png', 26, 44),
(27246, 9, 'G. Pasas', 'Attacker', 'https://media.api-sports.io/football/players/27246.png', 35, 19),
(27334, 10, 'P. Simelidis', 'Midfielder', 'https://media.api-sports.io/football/players/27334.png', 33, 31),
(27350, 7, 'A. Tsilingiris', 'Goalkeeper', 'https://media.api-sports.io/football/players/27350.png', 25, 75),
(30393, 2, 'A. Lafont', 'Goalkeeper', 'https://media.api-sports.io/football/players/30393.png', 26, 40),
(30500, 9, 'E. Ferigra', 'Defender', 'https://media.api-sports.io/football/players/30500.png', 26, 14),
(30507, 1, 'S. Meïté', 'Midfielder', 'https://media.api-sports.io/football/players/30507.png', 31, 8),
(30536, 2, 'F. Đuričić', 'Midfielder', 'https://media.api-sports.io/football/players/30536.png', 33, 31),
(30571, 1, 'K. Despodov', 'Midfielder', 'https://media.api-sports.io/football/players/30571.png', 29, 77),
(30789, 5, 'C. Kouamé', 'Attacker', 'https://media.api-sports.io/football/players/30789.png', 28, 31),
(30943, 9, 'Ľ. Tupta', 'Midfielder', 'https://media.api-sports.io/football/players/30943.png', 27, 29),
(31521, 1, 'A. Vogliacco', 'Defender', 'https://media.api-sports.io/football/players/31521.png', 27, 4),
(31538, 4, 'A. Brignoli', 'Goalkeeper', 'https://media.api-sports.io/football/players/31538.png', 34, 91),
(35576, 4, 'O. Pineda', 'Midfielder', 'https://media.api-sports.io/football/players/35576.png', 29, 13),
(35665, 9, 'Á. Sagal', 'Midfielder', 'https://media.api-sports.io/football/players/35665.png', 32, 25),
(37025, 14, 'L. Lamprou', 'Midfielder', 'https://media.api-sports.io/football/players/37025.png', 28, 7),
(37157, 2, 'T. Vilhena', 'Midfielder', 'https://media.api-sports.io/football/players/37157.png', 31, 52),
(37185, 5, 'O. Boussaid', 'Midfielder', 'https://media.api-sports.io/football/players/37185.png', 25, 49),
(37186, 2, 'C. Dessers', 'Attacker', 'https://media.api-sports.io/football/players/37186.png', 31, 33),
(38692, 2, 'E. Palmer-Brown', 'Defender', 'https://media.api-sports.io/football/players/38692.png', 28, 14),
(38755, 7, 'C. Nunnely', 'Midfielder', 'https://media.api-sports.io/football/players/38755.png', 26, 11),
(40369, 14, 'G. Migas', 'Defender', 'https://media.api-sports.io/football/players/40369.png', 31, 22),
(40452, 12, 'K. Michalak', 'Midfielder', 'https://media.api-sports.io/football/players/40452.png', 28, 71),
(40546, 13, 'V. De Marco', 'Defender', 'https://media.api-sports.io/football/players/40546.png', 33, 81),
(40625, 7, 'S. Musiolik', 'Attacker', 'https://media.api-sports.io/football/players/40625.png', 29, 70),
(41130, 3, 'Chiquinho', 'Midfielder', 'https://media.api-sports.io/football/players/41130.png', 30, 22),
(41186, 14, 'Joca', 'Midfielder', 'https://media.api-sports.io/football/players/41186.png', 29, 8),
(41465, 9, 'L. Rosić', 'Defender', 'https://media.api-sports.io/football/players/41465.png', 32, 33),
(41485, 14, 'André Moreira', 'Goalkeeper', 'https://media.api-sports.io/football/players/41485.png', 30, 12),
(41586, 7, 'Bernardo Martins', 'Midfielder', 'https://media.api-sports.io/football/players/41586.png', 28, 8),
(41588, 11, 'I. Vukotić', 'Midfielder', 'https://media.api-sports.io/football/players/41588.png', 26, 8),
(42315, 3, 'M. Taremi', 'Attacker', 'https://media.api-sports.io/football/players/42315.png', 33, 99),
(43078, 12, 'S. Mladen', 'Defender', 'https://media.api-sports.io/football/players/43078.png', 34, 5),
(43095, 12, 'A. Mățan', 'Midfielder', 'https://media.api-sports.io/football/players/43095.png', 26, 10),
(44333, 7, 'Botía', 'Defender', 'https://media.api-sports.io/football/players/44333.png', 36, 4),
(44582, 12, 'Farley Rosa', 'Midfielder', 'https://media.api-sports.io/football/players/44582.png', 31, 19),
(44784, 1, 'G. Taylor', 'Defender', 'https://media.api-sports.io/football/players/44784.png', 28, 32),
(44849, 13, 'S. Oméonga', 'Midfielder', 'https://media.api-sports.io/football/players/44849.png', 29, 40),
(45211, 4, 'J. Penrice', 'Defender', 'https://media.api-sports.io/football/players/45211.png', 27, 29),
(45671, 12, 'K. Aleksić', 'Attacker', 'https://media.api-sports.io/football/players/45671.png', 27, 14),
(46078, 6, 'N. Šipčić', 'Defender', 'https://media.api-sports.io/football/players/46078.png', 30, 3),
(46085, 11, 'F. Bainovič', 'Midfielder', 'https://media.api-sports.io/football/players/46085.png', 29, 25),
(46652, 12, 'Unai García', 'Defender', 'https://media.api-sports.io/football/players/46652.png', 33, 4),
(46679, 8, 'Quini', 'Defender', 'https://media.api-sports.io/football/players/46679.png', 36, 17),
(46709, 5, 'Álvaro Tejero', 'Defender', 'https://media.api-sports.io/football/players/46709.png', 29, 15),
(46737, 1, 'Joan Sastre', 'Defender', 'https://media.api-sports.io/football/players/46737.png', 28, 23),
(46793, 7, 'David Simón', 'Defender', 'https://media.api-sports.io/football/players/46793.png', 37, 2),
(46849, 2, 'Javi Hernández', 'Midfielder', 'https://media.api-sports.io/football/players/46849.png', 27, 26),
(47061, 7, 'Jorge Pombo', 'Midfielder', 'https://media.api-sports.io/football/players/47061.png', 31, 6),
(47087, 5, 'U. Račić', 'Midfielder', 'https://media.api-sports.io/football/players/47087.png', 27, 10),
(47281, 3, 'Dani García', 'Midfielder', 'https://media.api-sports.io/football/players/47281.png', 35, 14),
(47415, 7, 'Rubén Pérez', 'Midfielder', 'https://media.api-sports.io/football/players/47415.png', 36, 21),
(47447, 9, 'P. Sisto', 'Attacker', 'https://media.api-sports.io/football/players/47447.png', 31, 77),
(47457, 3, 'Rúben Vezo', 'Defender', 'https://media.api-sports.io/football/players/47457.png', 31, 21),
(47545, 9, 'G. Kakuta', 'Midfielder', 'https://media.api-sports.io/football/players/47545.png', 34, 5),
(47663, 12, 'G. Granath', 'Defender', 'https://media.api-sports.io/football/players/47663.png', 28, 2),
(48029, 1, 'A. Jeremejeff', 'Attacker', 'https://media.api-sports.io/football/players/48029.png', 32, 19),
(48091, 5, 'N. Sonko-Sundberg', 'Defender', 'https://media.api-sports.io/football/players/48091.png', 29, 20),
(48488, 4, 'D. Kutesa', 'Midfielder', 'https://media.api-sports.io/football/players/48488.png', 28, 7),
(48590, 9, 'J. Ngoy', 'Attacker', 'https://media.api-sports.io/football/players/48590.png', 28, 7),
(49888, 3, 'Y. Yazıcı', 'Midfielder', 'https://media.api-sports.io/football/players/49888.png', 29, 97),
(49943, 7, 'L. Villafáñez', 'Midfielder', 'https://media.api-sports.io/football/players/49943.png', 34, 19),
(50009, 4, 'J. Jønsson', 'Midfielder', 'https://media.api-sports.io/football/players/50009.png', 33, 6),
(50231, 9, 'J. Atanasov', 'Midfielder', 'https://media.api-sports.io/football/players/50231.png', 26, 31),
(51734, 13, 'F. Tinaglini', 'Goalkeeper', 'https://media.api-sports.io/football/players/51734.png', 27, 77),
(53511, 10, 'O. Ožegović', 'Attacker', 'https://media.api-sports.io/football/players/53511.png', 31, 45),
(53594, 13, 'I. Kalinin', 'Defender', 'https://media.api-sports.io/football/players/53594.png', 30, 88),
(55392, 6, 'N. Alho', 'Midfielder', 'https://media.api-sports.io/football/players/55392.png', 32, 17),
(55467, 6, 'R. Ivanov', 'Defender', 'https://media.api-sports.io/football/players/55467.png', 31, 2),
(55876, 5, 'M. Frýdek', 'Defender', 'https://media.api-sports.io/football/players/55876.png', 33, 17),
(56025, 2, 'A. Čerin', 'Midfielder', 'https://media.api-sports.io/football/players/56025.png', 26, 16),
(56114, 2, 'M. Siopis', 'Midfielder', 'https://media.api-sports.io/football/players/56114.png', 31, 6),
(57147, 9, 'D. Ólafsson', 'Defender', 'https://media.api-sports.io/football/players/57147.png', 30, 20),
(59233, 10, 'F. Pedrozo', 'Attacker', 'https://media.api-sports.io/football/players/59233.png', 33, 15),
(61257, 4, 'L. Rota', 'Defender', 'https://media.api-sports.io/football/players/61257.png', 28, 12),
(63542, 5, 'M. Hongla', 'Midfielder', 'https://media.api-sports.io/football/players/63542.png', 27, 78),
(66427, 7, 'J. Pokorný', 'Defender', 'https://media.api-sports.io/football/players/66427.png', 29, 38),
(67955, 5, 'Carles Pérez', 'Midfielder', 'https://media.api-sports.io/football/players/67955.png', 27, 7),
(68179, 9, 'A. Anagnostopoulos', 'Goalkeeper', 'https://media.api-sports.io/football/players/68179.png', 31, 94),
(68184, 7, 'Hugo Sousa', 'Defender', 'https://media.api-sports.io/football/players/68184.png', 33, 33),
(68187, 5, 'L. Rose', 'Defender', 'https://media.api-sports.io/football/players/68187.png', 33, 92),
(68467, 7, 'A. Petkov', 'Defender', 'https://media.api-sports.io/football/players/68467.png', 26, 5),
(70078, 2, 'F. Pellistri', 'Attacker', 'https://media.api-sports.io/football/players/70078.png', 24, 28),
(81224, 7, 'M. Ramírez', 'Goalkeeper', 'https://media.api-sports.io/football/players/81224.png', 25, 99),
(84081, 3, 'G. Biancone', 'Defender', 'https://media.api-sports.io/football/players/84081.png', 25, 4),
(88819, 10, 'G. Kornezos', 'Defender', 'https://media.api-sports.io/football/players/88819.png', 27, 5),
(91215, 1, 'A. Tsiftsis', 'Goalkeeper', 'https://media.api-sports.io/football/players/91215.png', 26, 99),
(92525, 11, 'Borja González', 'Defender', 'https://media.api-sports.io/football/players/92525.png', 30, 17),
(92954, 9, 'S. Mourgos', 'Midfielder', 'https://media.api-sports.io/football/players/92954.png', 27, 11),
(93004, 13, 'S. Ben Sallam', 'Midfielder', 'https://media.api-sports.io/football/players/93004.png', 24, 6),
(93671, 2, 'Pedro Chirivella', 'Midfielder', 'https://media.api-sports.io/football/players/93671.png', 28, 4),
(110541, 9, 'G. Naor', 'Midfielder', 'https://media.api-sports.io/football/players/110541.png', 26, 15),
(122040, 11, 'N. Christogeorgos', 'Goalkeeper', 'https://media.api-sports.io/football/players/122040.png', 25, 31),
(122131, 13, 'M. Tsaousis', 'Defender', 'https://media.api-sports.io/football/players/122131.png', 25, 14),
(126701, 10, 'H. Layous', 'Midfielder', 'https://media.api-sports.io/football/players/126701.png', 25, 7),
(127340, 6, 'Eder González', 'Midfielder', 'https://media.api-sports.io/football/players/127340.png', 28, 10),
(129169, 2, 'A. Zaroury', 'Attacker', 'https://media.api-sports.io/football/players/129169.png', 25, 9),
(129903, 9, 'F. Pérez', 'Attacker', 'https://media.api-sports.io/football/players/129903.png', 26, 8),
(133918, 13, 'Adrián Riera', 'Attacker', 'https://media.api-sports.io/football/players/133918.png', 29, 7),
(134431, 3, 'L. Pirola', 'Defender', 'https://media.api-sports.io/football/players/134431.png', 23, 5),
(135835, 8, 'G. Mitoglou', 'Defender', 'https://media.api-sports.io/football/players/135835.png', 26, 24),
(135853, 11, 'G. Kanellópoulos', 'Midfielder', 'https://media.api-sports.io/football/players/135853.png', 25, 7),
(135857, 6, 'A. Tereziou', 'Defender', 'https://media.api-sports.io/football/players/135857.png', 25, 27),
(138828, 7, 'Y. Larouci', 'Defender', 'https://media.api-sports.io/football/players/138828.png', 24, 76),
(140969, 9, 'Z. Chatzistravos', 'Midfielder', 'https://media.api-sports.io/football/players/140969.png', 26, 18),
(141841, 4, 'Filipe Relvas', 'Defender', 'https://media.api-sports.io/football/players/141841.png', 26, 44),
(142193, 12, 'Y. Kucherenko', 'Goalkeeper', 'https://media.api-sports.io/football/players/142193.png', 26, 99),
(144743, 5, 'B. Garré', 'Midfielder', 'https://media.api-sports.io/football/players/144743.png', 25, 90),
(151837, 7, 'P. Mijić', 'Attacker', 'https://media.api-sports.io/football/players/151837.png', 27, 90),
(152893, 6, 'A. Simoni', 'Attacker', 'https://media.api-sports.io/football/players/152893.png', 29, 96),
(154742, 2, 'M. Pantović', 'Midfielder', 'https://media.api-sports.io/football/players/154742.png', 23, 72),
(159415, 10, 'I. Tsivelekidis', 'Defender', 'https://media.api-sports.io/football/players/159415.png', 26, 37),
(161576, 1, 'D. Tsopouroglou', 'Midfielder', 'https://media.api-sports.io/football/players/161576.png', 23, 33),
(161577, 14, 'V. Grosdis', 'Midfielder', 'https://media.api-sports.io/football/players/161577.png', 23, 17),
(161705, 12, 'G. Satsias', 'Midfielder', 'https://media.api-sports.io/football/players/161705.png', 23, 18),
(161958, 9, 'V. Sourlis', 'Midfielder', 'https://media.api-sports.io/football/players/161958.png', 23, 90),
(162229, 1, 'G. Michailidis', 'Defender', 'https://media.api-sports.io/football/players/162229.png', 25, 5),
(162410, 1, 'G. Konstantelias', 'Midfielder', 'https://media.api-sports.io/football/players/162410.png', 22, 65),
(162481, 3, 'Diogo Nascimento', 'Midfielder', 'https://media.api-sports.io/football/players/162481.png', 23, 8),
(162990, 14, 'L. Abanda', 'Defender', 'https://media.api-sports.io/football/players/162990.png', 25, 97),
(163220, 7, 'J. Antonisse', 'Attacker', 'https://media.api-sports.io/football/players/163220.png', 23, 7),
(167705, 9, 'L. Andrada', 'Midfielder', 'https://media.api-sports.io/football/players/167705.png', 24, 10),
(170077, 11, 'T. Nuss', 'Attacker', 'https://media.api-sports.io/football/players/170077.png', 24, 18),
(180128, 6, 'S. Mitrović', 'Attacker', 'https://media.api-sports.io/football/players/180128.png', 23, 75),
(181526, 10, 'Lucas Anacker', 'Goalkeeper', 'https://media.api-sports.io/football/players/181526.png', 29, 88),
(181802, 7, 'Gerson Sousa', 'Attacker', 'https://media.api-sports.io/football/players/181802.png', 23, 10),
(182717, 14, 'Carles Soria', 'Attacker', 'https://media.api-sports.io/football/players/182717.png', 29, 2),
(193175, 13, 'A. Maskanakis', 'Midfielder', 'https://media.api-sports.io/football/players/193175.png', 21, 17),
(193327, 6, 'Miki Muñoz', 'Midfielder', 'https://media.api-sports.io/football/players/193327.png', 30, 22),
(194816, 12, 'Y. Badji', 'Attacker', 'https://media.api-sports.io/football/players/194816.png', 24, 25),
(194879, 9, 'T. Iliadis', 'Defender', 'https://media.api-sports.io/football/players/194879.png', 29, 4),
(195580, 3, 'Clayton', 'Attacker', 'https://media.api-sports.io/football/players/195580.png', 26, 19),
(195929, 3, 'S. Hezze', 'Midfielder', 'https://media.api-sports.io/football/players/195929.png', 24, 32),
(197032, 13, 'Riquelme', 'Defender', 'https://media.api-sports.io/football/players/197032.png', 23, 12),
(204037, 3, 'Costinha', 'Defender', 'https://media.api-sports.io/football/players/204037.png', 25, 20),
(204163, 13, 'I. Gelashvili', 'Defender', 'https://media.api-sports.io/football/players/204163.png', 24, 5),
(204290, 10, 'T. Tsapras', 'Midfielder', 'https://media.api-sports.io/football/players/204290.png', 24, 6),
(210033, 13, 'E. Brooks', 'Midfielder', 'https://media.api-sports.io/football/players/210033.png', 24, 28),
(215975, 7, 'K. Roukounakis', 'Midfielder', 'https://media.api-sports.io/football/players/215975.png', 24, 17),
(216762, 1, 'C. Zafeiris', 'Midfielder', 'https://media.api-sports.io/football/players/216762.png', 22, 20),
(237121, 4, 'Zini', 'Attacker', 'https://media.api-sports.io/football/players/237121.png', 23, 90),
(266121, 12, 'C. Belevonis', 'Attacker', 'https://media.api-sports.io/football/players/266121.png', 23, 8),
(276605, 14, 'D. Agyakwa', 'Defender', 'https://media.api-sports.io/football/players/276605.png', 24, 99),
(277165, 13, 'Volnei Feltes', 'Defender', 'https://media.api-sports.io/football/players/277165.png', 25, 30),
(278196, 3, 'S. Onyemaechi', 'Defender', 'https://media.api-sports.io/football/players/278196.png', 26, 70),
(278447, 6, 'V. Chatziemmanouil', 'Goalkeeper', 'https://media.api-sports.io/football/players/278447.png', 26, 61),
(289455, 14, 'M. González', 'Midfielder', 'https://media.api-sports.io/football/players/289455.png', 23, 21),
(290630, 14, 'N. Makni', 'Attacker', 'https://media.api-sports.io/football/players/290630.png', 24, 19),
(292170, 12, 'Lenny Lobato', 'Midfielder', 'https://media.api-sports.io/football/players/292170.png', 24, 70),
(292545, 6, 'D. Grozdanić', 'Defender', 'https://media.api-sports.io/football/players/292545.png', 23, 30),
(302432, 1, 'A. Bianco', 'Midfielder', 'https://media.api-sports.io/football/players/302432.png', 23, 22),
(303481, 10, 'L. Jallow', 'Midfielder', 'https://media.api-sports.io/football/players/303481.png', 23, 19),
(310442, 11, 'N. Athanasiou', 'Midfielder', 'https://media.api-sports.io/football/players/310442.png', 24, 3),
(311376, 10, 'G. Balzi', 'Midfielder', 'https://media.api-sports.io/football/players/311376.png', 24, 11),
(313222, 3, 'A. Kalogeropoulos', 'Defender', 'https://media.api-sports.io/football/players/313222.png', 21, 6),
(315772, 7, 'T. Johnson Eboh', 'Midfielder', 'https://media.api-sports.io/football/players/315772.png', 23, 14),
(323785, 2, 'V. Taborda', 'Attacker', 'https://media.api-sports.io/football/players/323785.png', 24, 20),
(325717, 3, 'André Luiz', 'Attacker', 'https://media.api-sports.io/football/players/325717.png', 23, 17),
(333039, 14, 'T. Triantafyllou', 'Defender', 'https://media.api-sports.io/football/players/333039.png', 22, 89),
(334590, 14, 'I. Kyrkos', 'Midfielder', 'https://media.api-sports.io/football/players/334590.png', 22, 29),
(335912, 5, 'M. Kerkez', 'Defender', 'https://media.api-sports.io/football/players/335912.png', 25, 88),
(336674, 5, 'G. Misehouy', 'Midfielder', 'https://media.api-sports.io/football/players/336674.png', 20, 26),
(340661, 12, 'L. Smyrlis', 'Attacker', 'https://media.api-sports.io/football/players/340661.png', 21, 7),
(340663, 1, 'D. Monastirlis', 'Goalkeeper', 'https://media.api-sports.io/football/players/340663.png', 21, 41),
(342895, 13, 'M. Sofianos', 'Attacker', 'https://media.api-sports.io/football/players/342895.png', 22, 21),
(342896, 7, 'Dimitris Theodoridis', 'Attacker', 'https://media.api-sports.io/football/players/342896.png', 23, 9),
(342981, 6, 'S. Angelidis', 'Goalkeeper', 'https://media.api-sports.io/football/players/342981.png', 20, 71),
(343194, 3, 'N. Botis', 'Goalkeeper', 'https://media.api-sports.io/football/players/343194.png', 21, 31),
(343337, 13, 'C. Georgiadis', 'Defender', 'https://media.api-sports.io/football/players/343337.png', 20, 23),
(344552, 10, 'G. Sourdis', 'Goalkeeper', 'https://media.api-sports.io/football/players/344552.png', 24, 99),
(344561, 4, 'Á. Angelópoulos', 'Goalkeeper', 'https://media.api-sports.io/football/players/344561.png', 22, 81),
(344570, 7, 'A. Christópoulos', 'Attacker', 'https://media.api-sports.io/football/players/344570.png', 22, 72),
(348211, 8, 'T. Karamanis', 'Defender', 'https://media.api-sports.io/football/players/348211.png', 22, 6),
(349497, 11, 'G. Apostolakis', 'Midfielder', 'https://media.api-sports.io/football/players/349497.png', 21, 21),
(351886, 4, 'H. Sahabo', 'Midfielder', 'https://media.api-sports.io/football/players/351886.png', 20, 80),
(358495, 13, 'A. Tsompanidis', 'Goalkeeper', 'https://media.api-sports.io/football/players/358495.png', 21, 20),
(361490, 4, 'C. Kosidis', 'Defender', 'https://media.api-sports.io/football/players/361490.png', 20, 34),
(361491, 10, 'M. Filon', 'Defender', 'https://media.api-sports.io/football/players/361491.png', 20, 22),
(364513, 3, 'L. Scipioni', 'Midfielder', 'https://media.api-sports.io/football/players/364513.png', 21, 16),
(374960, 5, 'M. Panagidis', 'Midfielder', 'https://media.api-sports.io/football/players/374960.png', 21, 77),
(376324, 5, 'M. Voriazidis', 'Midfielder', 'https://media.api-sports.io/football/players/376324.png', 21, 22),
(382218, 11, 'G. Theodosoulakis', 'Attacker', 'https://media.api-sports.io/football/players/382218.png', 21, 46),
(383505, 6, 'T. Kakadiaris', 'Goalkeeper', 'https://media.api-sports.io/football/players/383505.png', 18, 91),
(384452, 6, 'C. Okoh', 'Attacker', 'https://media.api-sports.io/football/players/384452.png', 22, 26),
(388962, 11, 'K. Lilo', 'Goalkeeper', 'https://media.api-sports.io/football/players/388962.png', 22, 1),
(390556, 12, 'E. Nikolaou', 'Midfielder', 'https://media.api-sports.io/football/players/390556.png', 21, 77),
(392481, 12, 'A. Bregou', 'Midfielder', 'https://media.api-sports.io/football/players/392481.png', 19, 30),
(392514, 3, 'C. Mouzakitis', 'Midfielder', 'https://media.api-sports.io/football/players/392514.png', 19, 96),
(395460, 12, 'G. Agapakis', 'Midfielder', 'https://media.api-sports.io/football/players/395460.png', 23, 23),
(395477, 9, 'A. Ouattara', 'Defender', 'https://media.api-sports.io/football/players/395477.png', 22, 12),
(399648, 13, 'G. Doiranlis', 'Midfielder', 'https://media.api-sports.io/football/players/399648.png', 23, 24),
(402422, 6, 'K. Pomonis', 'Defender', 'https://media.api-sports.io/football/players/402422.png', 23, 29),
(402797, 8, 'M. Mountes', 'Midfielder', 'https://media.api-sports.io/football/players/402797.png', 22, 12),
(403672, 5, 'Dudu', 'Midfielder', 'https://media.api-sports.io/football/players/403672.png', 23, 28),
(406591, 7, 'C. Konaté', 'Defender', 'https://media.api-sports.io/football/players/406591.png', 21, 22),
(408225, 5, 'M. Gning', 'Attacker', 'https://media.api-sports.io/football/players/408225.png', 19, 25),
(413307, 14, 'D. Martínez', 'Midfielder', 'https://media.api-sports.io/football/players/413307.png', 21, 16),
(417556, 11, 'K. Kostoulas', 'Defender', 'https://media.api-sports.io/football/players/417556.png', 20, 5),
(424376, 6, 'G. Charalampoglou', 'Attacker', 'https://media.api-sports.io/football/players/424376.png', 21, 49),
(426673, 4, 'M. Todorski', 'Defender', 'https://media.api-sports.io/football/players/426673.png', 21, 15),
(437091, 10, 'C. Papadopoulos', 'Midfielder', 'https://media.api-sports.io/football/players/437091.png', 21, 74),
(443716, 3, 'A. Liatsikouras', 'Midfielder', 'https://media.api-sports.io/football/players/443716.png', 17, 67),
(443719, 8, 'S. Pnevmonidis', 'Midfielder', 'https://media.api-sports.io/football/players/443719.png', 18, 10),
(446029, 1, 'A. Mythou', 'Attacker', 'https://media.api-sports.io/football/players/446029.png', 18, 56),
(446107, 1, 'D. Bataoulas', 'Defender', 'https://media.api-sports.io/football/players/446107.png', 18, 97),
(449603, 2, 'S. Kontouris', 'Midfielder', 'https://media.api-sports.io/football/players/449603.png', 20, 18),
(450565, 7, 'M. Patiras', 'Midfielder', 'https://media.api-sports.io/football/players/450565.png', 20, 30),
(451997, 3, 'G. Kouraklis', 'Goalkeeper', 'https://media.api-sports.io/football/players/451997.png', 18, 61),
(454698, 8, 'E. Ukaki', 'Defender', 'https://media.api-sports.io/football/players/454698.png', 21, 7),
(460580, 1, 'D. Berdos', 'Attacker', 'https://media.api-sports.io/football/players/460580.png', 17, 39),
(471796, 1, 'D. Chatsidis', 'Attacker', 'https://media.api-sports.io/football/players/471796.png', 19, 52),
(482601, 12, 'Manos Chrysovalantis', 'Defender', 'https://media.api-sports.io/football/players/482601.png', 20, 3),
(482616, 11, 'M. Chnaris', 'Midfielder', 'https://media.api-sports.io/football/players/482616.png', 17, 91),
(482621, 11, 'K. Lagoudakis', 'Midfielder', 'https://media.api-sports.io/football/players/482621.png', 17, 52),
(482623, 12, 'E. Papazois', 'Goalkeeper', 'https://media.api-sports.io/football/players/482623.png', 18, 21),
(490156, 2, 'Santino Andino', 'Midfielder', 'https://media.api-sports.io/football/players/490156.png', 20, 10),
(492399, 6, 'V. Archontakakis', 'Goalkeeper', 'https://media.api-sports.io/football/players/492399.png', 19, 55),
(494623, 9, 'K. Grozos', 'Defender', 'https://media.api-sports.io/football/players/494623.png', 18, 3),
(524952, 5, 'E. Karaj', 'Goalkeeper', 'https://media.api-sports.io/football/players/524952.png', 20, 91),
(527409, 8, 'Konstantinos Batos', 'Midfielder', 'https://media.api-sports.io/football/players/527409.png', 19, 33),
(528313, 14, 'Klearchos Vainopoulos', 'Midfielder', 'https://media.api-sports.io/football/players/528313.png', 20, 13),
(528314, 11, 'Lefteris Kontekas', 'Defender', 'https://media.api-sports.io/football/players/528314.png', 16, 44),
(540063, 12, 'L. Kojic', 'Midfielder', 'https://media.api-sports.io/football/players/540063.png', 25, 28),
(544039, 2, 'K. Kotsaris', 'Goalkeeper', 'https://media.api-sports.io/football/players/544039.png', 29, 70),
(544882, 5, 'K. Charoupas', 'Attacker', 'https://media.api-sports.io/football/players/544882.png', 18, 41),
(550627, 2, 'A. Jagusic', 'Midfielder', 'https://media.api-sports.io/football/players/550627.png', 20, 88),
(553618, 13, 'V. Rumyantsev', 'Midfielder', 'https://media.api-sports.io/football/players/553618.png', 21, 63),
(553932, 6, 'K. Ketu', 'Attacker', 'https://media.api-sports.io/football/players/553932.png', 28, 40),
(553959, 6, 'Christos Almyras', 'Midfielder', 'https://media.api-sports.io/football/players/553959.png', 20, 68),
(554036, 11, 'Pavlos Kenourgiakis', 'Defender', 'https://media.api-sports.io/football/players/554036.png', 17, 90),
(561126, 6, 'P. Castano', 'Defender', 'https://media.api-sports.io/football/players/561126.png', 26, 19),
(561127, 12, 'Esteban Diego', 'Attacker', 'https://media.api-sports.io/football/players/561127.png', 25, 22),
(561129, 12, 'A. Apostolopoulos', 'Defender', 'https://media.api-sports.io/football/players/561129.png', 22, 65),
(561130, 12, 'J. Aguirre', 'Attacker', 'https://media.api-sports.io/football/players/561130.png', 25, 9),
(561134, 2, 'G. Katris', 'Defender', 'https://media.api-sports.io/football/players/561134.png', 20, 3),
(561137, 7, 'D. Chouchoumis', 'Defender', 'https://media.api-sports.io/football/players/561137.png', 31, 3),
(561139, 2, 'A. Tetteh', 'Attacker', 'https://media.api-sports.io/football/players/561139.png', 24, 7),
(561145, 2, 'P. Pantelidis', 'Attacker', 'https://media.api-sports.io/football/players/561145.png', 23, 23),
(561146, 10, 'K. Goumas', 'Midfielder', 'https://media.api-sports.io/football/players/561146.png', 20, 85),
(561149, 8, 'G. Papadopoulos', 'Defender', 'https://media.api-sports.io/football/players/561149.png', 23, 16),
(561151, 4, 'D. Kaloskamis', 'Midfielder', 'https://media.api-sports.io/football/players/561151.png', 20, 17),
(561152, 8, 'P. Tsantilas', 'Attacker', 'https://media.api-sports.io/football/players/561152.png', 21, 19),
(561154, 6, 'O. Alagbe', 'Midfielder', 'https://media.api-sports.io/football/players/561154.png', 25, 69),
(561156, 14, 'E. Tasiouras', 'Defender', 'https://media.api-sports.io/football/players/561156.png', 21, 72),
(561157, 12, 'M. Pardalos', 'Goalkeeper', 'https://media.api-sports.io/football/players/561157.png', 24, 1),
(561159, 8, 'A. Koselev', 'Goalkeeper', 'https://media.api-sports.io/football/players/561159.png', 31, 55),
(561160, 8, 'V. Athanasiou', 'Goalkeeper', 'https://media.api-sports.io/football/players/561160.png', 26, 30),
(561161, 8, 'S. Ampartzidis', 'Midfielder', 'https://media.api-sports.io/football/players/561161.png', 20, 20),
(561162, 13, 'V. Sakalidis', 'Goalkeeper', 'https://media.api-sports.io/football/players/561162.png', 22, 13),
(561163, 9, 'V. Varsamis', 'Midfielder', 'https://media.api-sports.io/football/players/561163.png', 17, 73),
(561165, 7, 'G. Konstantakopoulos', 'Midfielder', 'https://media.api-sports.io/football/players/561165.png', 20, 60),
(561168, 8, 'V. Paliouras', 'Attacker', 'https://media.api-sports.io/football/players/561168.png', 16, 29),
(561175, 5, 'S. Papasarafianos', 'Midfielder', 'https://media.api-sports.io/football/players/561175.png', 16, 29),
(561176, 7, 'L. Maidana', 'Defender', 'https://media.api-sports.io/football/players/561176.png', 23, 24),
(561180, 2, 'L. Stamellos', 'Goalkeeper', 'https://media.api-sports.io/football/players/561180.png', 17, 41),
(565601, 2, 'I. G. Bokos', 'Midfielder', 'https://media.api-sports.io/football/players/565601.png', 18, 39),
(573738, 3, 'N. Botis', 'Goalkeeper', 'https://media.api-sports.io/football/players/573738.png', 21, 31),
(574675, 6, 'D. Grammenos', 'Midfielder', 'https://media.api-sports.io/football/players/574675.png', 22, 99),
(579717, 13, 'E. Pavlis', 'Attacker', 'https://media.api-sports.io/football/players/579717.png', 22, 27),
(579718, 12, 'D. Hoxha', 'Attacker', 'https://media.api-sports.io/football/players/579718.png', 18, 33),
(580193, 7, 'Filippos Roberts', 'Goalkeeper', 'https://media.api-sports.io/football/players/580193.png', 20, 26),
(580867, 1, 'M. Balde', 'Attacker', 'https://media.api-sports.io/football/players/580867.png', 18, 43),
(585102, 8, 'E. Hoxha', 'Midfielder', 'https://media.api-sports.io/football/players/585102.png', 19, 14),
(585105, 8, 'L. Kopanidis', 'Midfielder', 'https://media.api-sports.io/football/players/585105.png', 19, 18),
(592903, 4, 'Marios Balamotis', 'Goalkeeper', 'https://media.api-sports.io/football/players/592903.png', 20, 41),
(592904, 4, 'Zois Karargyris', 'Attacker', 'https://media.api-sports.io/football/players/592904.png', 18, 27),
(594069, 14, 'Nikos Grammatikakis', 'Goalkeeper', 'https://media.api-sports.io/football/players/594069.png', 22, 70),
(594070, 14, 'Diamanti Legisi', 'Attacker', 'https://media.api-sports.io/football/players/594070.png', 19, 39),
(594072, 10, 'Stelios Vallindras', 'Goalkeeper', 'https://media.api-sports.io/football/players/594072.png', 20, 33),
(594073, 7, 'Theodoros Faitakis', 'Defender', 'https://media.api-sports.io/football/players/594073.png', 18, 15),
(594074, 7, 'Konstantinos Lampsias', 'Defender', 'https://media.api-sports.io/football/players/594074.png', 23, 74),
(594075, 7, 'Alexandros Pothas', 'Midfielder', 'https://media.api-sports.io/football/players/594075.png', 18, 16),
(594078, 9, 'Dimitrios Diminikos', 'Midfielder', 'https://media.api-sports.io/football/players/594078.png', 17, 17),
(597940, 1, 'G. Kosidis', 'Defender', 'https://media.api-sports.io/football/players/597940.png', 18, 5),
(599688, 2, 'I. Nempis', 'Attacker', 'https://media.api-sports.io/football/players/599688.png', 16, 50),
(610833, 1, 'B. Dunga', 'Attacker', 'https://media.api-sports.io/football/players/610833.png', 18, 37),
(614179, 11, 'Thanasis Sitmalidis', 'Midfielder', 'https://media.api-sports.io/football/players/614179.png', 17, 71),
(616038, 7, 'Christos Ligdas', 'Attacker', 'https://media.api-sports.io/football/players/616038.png', 19, 18),
(619667, 2, 'C. Geitonas', 'Goalkeeper', 'https://media.api-sports.io/football/players/619667.png', 16, 71),
(620944, 2, 'Angelos Vyntra', 'Defender', 'https://media.api-sports.io/football/players/620944.png', 17, 34),
(625429, 2, 'Sotiris Terzis', 'Attacker', 'https://media.api-sports.io/football/players/625429.png', 18, 47),
(625857, 13, 'Michalis Pourliotopoulos', 'Goalkeeper', 'https://media.api-sports.io/football/players/625857.png', 18, 55),
(626396, 6, 'D. Laskaris', 'Defender', 'https://media.api-sports.io/football/players/626396.png', 20, 74),
(626441, 14, 'K. Lykourinos', 'Defender', 'https://media.api-sports.io/football/players/626441.png', 19, 24),
(626536, 4, 'S. Psyropoulos', 'Defender', 'https://media.api-sports.io/football/players/626536.png', 18, 35),
(626652, 11, 'T. Romano', 'Midfielder', 'https://media.api-sports.io/football/players/626652.png', 19, 30),
(631550, 5, 'Christos Kamtsis', 'Midfielder', 'https://media.api-sports.io/football/players/631550.png', 18, 30),
(631551, 1, 'Vasileios Nikolakoulis', 'Goalkeeper', 'https://media.api-sports.io/football/players/631551.png', 20, 81),
(632408, 13, 'Nikolaos Tolios', 'Attacker', 'https://media.api-sports.io/football/players/632408.png', 19, 47),
(634748, 7, 'Tavares Miguel', 'Attacker', 'https://media.api-sports.io/football/players/634748.png', 26, 77),
(635140, 11, 'Christos Tsalpatouros', 'Midfielder', 'https://media.api-sports.io/football/players/635140.png', 15, 83),
(636166, 14, 'Athanasios Papathanasiou Gerofokoas', 'Goalkeeper', 'https://media.api-sports.io/football/players/636166.png', 19, 82),
(636933, 13, 'Eric Bile Darnel', 'Attacker', 'https://media.api-sports.io/football/players/636933.png', 20, 18),
(637590, 2, 'Iason Skarlatidis', 'Defender', 'https://media.api-sports.io/football/players/637590.png', 19, 62),
(637592, 2, 'Nektarios Kaloskamis', 'Defender', 'https://media.api-sports.io/football/players/637592.png', 19, 37),
(637868, 14, 'V. Koutoukas', 'Goalkeeper', 'https://media.api-sports.io/football/players/637868.png', 20, 26),
(638241, 13, 'Fotis Chavos', 'Defender', 'https://media.api-sports.io/football/players/638241.png', 20, 45),
(638242, 13, 'Giannis Kosmidis', 'Midfielder', 'https://media.api-sports.io/football/players/638242.png', 19, 46),
(638243, 13, 'Zacharias Tsagarakis', 'Midfielder', 'https://media.api-sports.io/football/players/638243.png', 18, 38),
(659459, 11, 'S. Almasidis', 'Midfielder', 'https://media.api-sports.io/football/players/659459.png', 21, 23),
(660734, 6, 'B. Perez', 'Midfielder', 'https://media.api-sports.io/football/players/660734.png', 24, 80);

-- --------------------------------------------------------

--
-- Δομή πίνακα για τον πίνακα `teams`
--

CREATE TABLE `teams` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `city` varchar(100) NOT NULL,
  `logo` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Άδειασμα δεδομένων του πίνακα `teams`
--

INSERT INTO `teams` (`id`, `name`, `city`, `logo`) VALUES
(1, 'PAOK ', 'Thessaloniki', 'https://raw.githubusercontent.com/vaggelisriz/statScopeApp/backend/backend/uploads/logos/paok.png'),
(2, 'Panathinaikos', 'Athens', 'https://raw.githubusercontent.com/vaggelisriz/statScopeApp/backend/backend/uploads/logos/panathinaikos.png'),
(3, 'Olympiacos', 'Piraeus', 'https://raw.githubusercontent.com/vaggelisriz/statScopeApp/backend/backend/uploads/logos/olympiakos.jpg'),
(4, 'AEK', 'Athens', 'https://raw.githubusercontent.com/vaggelisriz/statScopeApp/backend/backend/uploads/logos/aek.jpg'),
(5, 'Aris', 'Thessaloniki', 'https://raw.githubusercontent.com/vaggelisriz/statScopeApp/backend/backend/uploads/logos/aris.jpg'),
(6, 'Asteras Tripolis', 'Tripoli', 'https://raw.githubusercontent.com/vaggelisriz/statScopeApp/backend/backend/uploads/logos/asteras.jpg'),
(7, 'Kifisia', 'Athens', 'https://raw.githubusercontent.com/vaggelisriz/statScopeApp/backend/backend/uploads/logos/kifisia.jpg'),
(8, 'Atromitos', 'Athens', 'https://raw.githubusercontent.com/vaggelisriz/statScopeApp/backend/backend/uploads/logos/atromitos.jpg'),
(9, 'AEL Larisa', 'Larisa', 'https://raw.githubusercontent.com/vaggelisriz/statScopeApp/backend/backend/uploads/logos/larisa.png'),
(10, 'Levadiakos', 'Livadeia', 'https://raw.githubusercontent.com/vaggelisriz/statScopeApp/backend/backend/uploads/logos/levadiakos.png'),
(11, 'OFI', 'Heraklion', 'https://raw.githubusercontent.com/vaggelisriz/statScopeApp/backend/backend/uploads/logos/ofi.png'),
(12, 'Panetolikos', 'Agrinio', 'https://raw.githubusercontent.com/vaggelisriz/statScopeApp/backend/backend/uploads/logos/panetolikos.jpg'),
(13, 'Panserraikos', 'Serres', 'https://raw.githubusercontent.com/vaggelisriz/statScopeApp/backend/backend/uploads/logos/panserraikos.png'),
(14, 'Volos', 'Volos', 'https://raw.githubusercontent.com/vaggelisriz/statScopeApp/backend/backend/uploads/logos/volos.png');

--
-- Ευρετήρια για άχρηστους πίνακες
--

--
-- Ευρετήρια για πίνακα `championships`
--
ALTER TABLE `championships`
  ADD PRIMARY KEY (`id`);

--
-- Ευρετήρια για πίνακα `matches`
--
ALTER TABLE `matches`
  ADD PRIMARY KEY (`id`),
  ADD KEY `home_team_id` (`home_team_id`),
  ADD KEY `away_team_id` (`away_team_id`),
  ADD KEY `championship_id` (`championship_id`);

--
-- Ευρετήρια για πίνακα `match_events`
--
ALTER TABLE `match_events`
  ADD PRIMARY KEY (`id`),
  ADD KEY `match_id` (`match_id`),
  ADD KEY `player_id` (`player_id`);

--
-- Ευρετήρια για πίνακα `players`
--
ALTER TABLE `players`
  ADD PRIMARY KEY (`id`),
  ADD KEY `team_id` (`team_id`);

--
-- Ευρετήρια για πίνακα `teams`
--
ALTER TABLE `teams`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT για άχρηστους πίνακες
--

--
-- AUTO_INCREMENT για πίνακα `championships`
--
ALTER TABLE `championships`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT για πίνακα `matches`
--
ALTER TABLE `matches`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=92;

--
-- AUTO_INCREMENT για πίνακα `match_events`
--
ALTER TABLE `match_events`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT για πίνακα `players`
--
ALTER TABLE `players`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=660735;

--
-- AUTO_INCREMENT για πίνακα `teams`
--
ALTER TABLE `teams`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- Περιορισμοί για άχρηστους πίνακες
--

--
-- Περιορισμοί για πίνακα `matches`
--
ALTER TABLE `matches`
  ADD CONSTRAINT `matches_ibfk_1` FOREIGN KEY (`home_team_id`) REFERENCES `teams` (`id`),
  ADD CONSTRAINT `matches_ibfk_2` FOREIGN KEY (`away_team_id`) REFERENCES `teams` (`id`),
  ADD CONSTRAINT `matches_ibfk_3` FOREIGN KEY (`championship_id`) REFERENCES `championships` (`id`) ON DELETE CASCADE;

--
-- Περιορισμοί για πίνακα `match_events`
--
ALTER TABLE `match_events`
  ADD CONSTRAINT `match_events_ibfk_1` FOREIGN KEY (`match_id`) REFERENCES `matches` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `match_events_ibfk_2` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE;

--
-- Περιορισμοί για πίνακα `players`
--
ALTER TABLE `players`
  ADD CONSTRAINT `players_ibfk_1` FOREIGN KEY (`team_id`) REFERENCES `teams` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
