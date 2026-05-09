-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Εξυπηρετητής: 127.0.0.1
-- Χρόνος δημιουργίας: 09 Μάη 2026 στις 15:30:56
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
  `photo` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Άδειασμα δεδομένων του πίνακα `players`
--

INSERT INTO `players` (`id`, `team_id`, `name`, `position`, `photo`) VALUES
(1, 1, 'Giannis Konstantelias', 'Μέσος', '../uploads/players/1778327050_69ff1e0ab309d.jpg');

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
(1, 'PAOK FC', 'Thessaloniki', '../uploads/logos/1778326561_69ff1c21ab2aa.png'),
(2, 'Panathinaikos FC', 'Athens', '../uploads/logos/1778327177_69ff1e89b7e48.png'),
(3, 'Olympiacos FC', 'Piraeus', '../uploads/logos/1778327216_69ff1eb0e2269.png'),
(4, 'AEK Athens', 'Athens', '../uploads/logos/1778327330_69ff1f22d8c09.jpg');

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
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT για πίνακα `teams`
--
ALTER TABLE `teams`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

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
