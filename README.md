# StatScope ⚽

A full-stack football statistics platform built for real-time match tracking. StatScope consists of two native Android apps and a PHP/MySQL backend, designed for use during live football championships.

---

## Overview

StatScope was built to support live football events by giving **statisticians** a dedicated tool to record match events in real time, while **fans/viewers** can follow the action through their own app — browsing standings, rosters, and live match stats.

---

## Architecture

```
statScopeApp/
├── backend/          # PHP REST API + Admin panel (Apache/XAMPP)
├── database/         # MySQL schema & seed data (statscopedb.sql)
├── statisticianApp/  # Android app for match officials / statisticians
└── userApp/          # Android app for fans / viewers
```

---

## Components

### 🗄️ Backend (PHP + MySQL)

A RESTful API built in PHP using PDO, running on Apache (XAMPP recommended). All endpoints live under `backend/api/` and return JSON.

**Key endpoints:**

| Endpoint | Method | Description |
|---|---|---|
| `getMatches.php` | GET | List all matches (filterable by `?status=live`) |
| `getChampionships.php` | GET | List all championships |
| `getTeams.php` | GET | List all teams |
| `getPlayers.php` | GET | Get players for a team (`?team_id=`) |
| `getMatchLineups.php` | GET | Get starters/bench for a match |
| `getMatchStats.php` | GET | Get match statistics |
| `getStandings.php` | GET | Championship standings |
| `getPlayerDetails.php` | GET | Player profile & season stats |
| `getPlayerMatchHistory.php` | GET | Player match-by-match history |
| `addMatchStatistic.php` | POST | Record a match event (goal, card, etc.) |
| `updateMatchStatusAndLineups.php` | POST | Set lineups & change match status |
| `saveEvent.php` | POST | Log a live event with minute |
| `finishMatch.php` | POST | Mark a match as completed |

There is also an `admin/` panel (PHP web pages) for managing championships, teams, and players.

### 📱 Statistician App (Android — Java)

The app used by the official statistician during a live match.

**Features:**
- Browse scheduled and live matches
- Set starting lineups (starters + bench) before kick-off
- Live match control: record goals, cards, substitutions with timestamps
- Update lineups mid-match
- View real-time statistics during the match
- Finish / close a match

**Key screens:** `LiveMatchesActivity`, `MatchLiveControlActivity`, `LiveMatchActionsActivity`, `LiveStatsActivity`, `UpdateLineupsActivity`

**Networking:** Retrofit 2 + OkHttp

### 📱 User App (Android — Java)

The read-only fan-facing app.

**Features:**
- Browse active championships
- View league standings
- Browse teams and their rosters
- View player profiles and match history
- Follow live matches in real time (live score + event log)
- View match details (lineups, events, stats)

**Key screens:** `FanActivity`, `ChampionshipActivity`, `FanMatchDetailsActivity`, `PlayerDetailsActivity`, `TeamRosterActivity`

**Tabs:** Matches · Standings · Teams

**Networking:** OkHttp (raw JSON parsing)

### 🗃️ Database

MySQL database (`statscopedb`) with the following tables:

- `championships` — competition groups (e.g. SuperLeague, Finals)
- `matches` — fixtures with status (`scheduled` / `live` / `completed`) and score
- `teams` — club info and logos
- `players` — player profiles linked to teams
- `match_lineups` — starters and bench per match
- `match_statistics` / `match_events` — logged events (goals, cards, substitutions)

A full SQL dump with schema and sample data is included at `database/statscopedb.sql`.

---

## Getting Started

### Prerequisites

- [XAMPP](https://www.apachefriends.org/) (Apache + MySQL + PHP 8.x)
- Android Studio (for building the Android apps)
- Android device or emulator on the same local network as the server

### Backend Setup

1. Clone the repository and place the `statScopeApp` folder inside your XAMPP `htdocs` directory.
2. Start Apache and MySQL from the XAMPP Control Panel.
3. Open **phpMyAdmin** and import `database/statscopedb.sql` to create and seed the database.
4. The API is now accessible at `http://<your-local-ip>/statScopeApp/backend/api/`.

### Android Apps Setup

1. Open either `statisticianApp/` or `userApp/` in Android Studio.
2. In `app/src/main/java/com/example/frontend/Config.java`, set your machine's local IP:
   ```java
   public static final String IP = "192.168.x.x"; // your machine's LAN IP
   ```
3. Build and run on a device/emulator connected to the same Wi-Fi network.

> **Note:** Both apps point to the same backend. Make sure your device and the machine running XAMPP are on the same local network.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | PHP 8, PDO, Apache (XAMPP) |
| Database | MySQL / MariaDB |
| Android | Java, Android SDK |
| HTTP (Statistician) | Retrofit 2, OkHttp, Gson |
| HTTP (User) | OkHttp (raw JSON) |
| Image loading | Glide |
| UI | Material Design 3, RecyclerView, ViewPager2 |
| Fonts | Rajdhani (Bold, SemiBold) |

---

## Sample Data

The database dump includes sample Greek football data: teams from the SuperLeague (Olympiakos, PAOK, Panathinaikos, AEK, Aris, etc.) along with championships, fixtures, and standings so the apps are usable right after setup.

---

## License

This project was developed as a university assignment for the course "Mobile Application Development" at the University of Macedonia. It is intended for educational purposes only.
