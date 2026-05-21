<?php
require_once '../config/dbConnect.php';
header('Content-Type: application/json; charset=utf-8');

$team_id = isset($_GET['team_id']) ? intval($_GET['team_id']) : 0;

if ($team_id === 0) {
    echo json_encode(["error" => "Missing team_id"]);
    exit;
}

try {
    // 1. Μεταδεδομένα Ομάδας
    $teamSql = "SELECT id, name, city, logo FROM teams WHERE id = :team_id";
    $teamStmt = $pdo->prepare($teamSql);
    $teamStmt->execute(['team_id' => $team_id]);
    $teamInfo = $teamStmt->fetch(PDO::FETCH_ASSOC);

    if (!$teamInfo) {
        echo json_encode(["error" => "Team not found"]);
        exit;
    }

    // 2. Πρωταθλήματα στα οποία έχει παίξει έστω και ένα ματς η ομάδα
    $championshipsSql = "SELECT DISTINCT c.id, c.name 
                         FROM championships c
                         JOIN matches m ON c.id = m.championship_id
                         WHERE m.home_team_id = :team_id OR m.away_team_id = :team_id";
    $chStmt = $pdo->prepare($championshipsSql);
    $chStmt->execute(['team_id' => $team_id]);
    $championships = $chStmt->fetchAll(PDO::FETCH_ASSOC);

    // 3. Live Υπολογισμός Στατιστικών Βαθμολογίας (Νίκες, Ήττες, Ισοπαλίες)
    $statsSql = "SELECT 
                    COUNT(*) as total_matches,
                    SUM(CASE WHEN (home_team_id = :team_id AND home_score > away_score) OR (away_team_id = :team_id AND away_score > home_score) THEN 1 ELSE 0 END) as wins,
                    SUM(CASE WHEN (home_team_id = :team_id AND home_score < away_score) OR (away_team_id = :team_id AND away_score < home_score) THEN 1 ELSE 0 END) as losses,
                    SUM(CASE WHEN (home_team_id = :team_id OR away_team_id = :team_id) AND home_score = away_score AND status = 'completed' THEN 1 ELSE 0 END) as draws
                 FROM matches 
                 WHERE (home_team_id = :team_id OR away_team_id = :team_id) AND status = 'completed'";
    $statsStmt = $pdo->prepare($statsSql);
    $statsStmt->execute(['team_id' => $team_id]);
    $standingsStats = $statsStmt->fetch(PDO::FETCH_ASSOC);

    // Υπολογισμός πόντων (3 η νίκη, 1 η ισοπαλία)
    $points = ($standingsStats['wins'] * 3) + ($standingsStats['draws'] * 1);

    // 4. Λήψη Ρόστερ (Παικτών)
    $playersSql = "SELECT id, name, position, number, photo, age FROM players WHERE team_id = :team_id ORDER BY name ASC";
    $playersStmt = $pdo->prepare($playersSql);
    $playersStmt->execute(['team_id' => $team_id]);
    $players = $playersStmt->fetchAll(PDO::FETCH_ASSOC);

    // Σύνθεση τελικού JSON
    echo json_encode([
        "team_metadata" => $teamInfo,
        "championships" => $championships,
        "stats" => [
            "wins" => intval($standingsStats['wins']),
            "draws" => intval($standingsStats['draws']),
            "losses" => intval($standingsStats['losses']),
            "points" => $points
        ],
        "roster" => $players
    ], JSON_UNESCAPED_UNICODE);

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(["error" => $e->getMessage()]);
}
?>