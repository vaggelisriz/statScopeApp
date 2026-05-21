<?php
require_once '../config/dbConnect.php';
header('Content-Type: application/json; charset=utf-8');

$match_id = isset($_GET['match_id']) ? intval($_GET['match_id']) : 0;

if ($match_id === 0) {
    echo json_encode(["error" => "Missing match_id"]);
    exit;
}

try {
    // 1. Βρίσκουμε αυτόματα ποια είναι η home και ποια η away ομάδα για αυτό το ματς
    $matchSql = "SELECT home_team_id, away_team_id FROM matches WHERE id = :match_id";
    $matchStmt = $pdo->prepare($matchSql);
    $matchStmt->execute(['match_id' => $match_id]);
    $match = $matchStmt->fetch(PDO::FETCH_ASSOC);

    if (!$match) {
        echo json_encode([
            "home_shots" => "0", "away_shots" => "0",
            "home_passes" => "0", "away_passes" => "0",
            "home_fouls" => "0", "away_fouls" => "0",
            "home_cards" => "0", "away_cards" => "0",
            "home_assists" => "0", "away_assists" => "0",
            "home_tackles" => "0", "away_tackles" => "0",
            "home_corners" => "0", "away_corners" => "0",
            "home_mistakes" => "0", "away_mistakes" => "0"
        ]);
        exit;
    }

    $home_id = $match['home_team_id'];
    $away_id = $match['away_team_id'];

    // 2. Εκτελούμε το COUNT συνδέοντας τα events με το team_id του παίκτη
    $statsSql = "SELECT 
        SUM(CASE WHEN me.event_type = 'shot' AND p.team_id = :home_id THEN 1 ELSE 0 END) as home_shots,
        SUM(CASE WHEN me.event_type = 'pass' AND p.team_id = :home_id THEN 1 ELSE 0 END) as home_passes,
        SUM(CASE WHEN me.event_type = 'foul_committed' AND p.team_id = :home_id THEN 1 ELSE 0 END) as home_fouls,
        SUM(CASE WHEN me.event_type = 'card_yellow' AND p.team_id = :home_id THEN 1 ELSE 0 END) as home_cards,
        SUM(CASE WHEN me.event_type = 'assist' AND p.team_id = :home_id THEN 1 ELSE 0 END) as home_assists,
        SUM(CASE WHEN me.event_type = 'tackle' AND p.team_id = :home_id THEN 1 ELSE 0 END) as home_tackles,
        SUM(CASE WHEN me.event_type = 'corner_won' AND p.team_id = :home_id THEN 1 ELSE 0 END) as home_corners,
        SUM(CASE WHEN me.event_type = 'mistake' AND p.team_id = :home_id THEN 1 ELSE 0 END) as home_mistakes,
        
        SUM(CASE WHEN me.event_type = 'shot' AND p.team_id = :away_id THEN 1 ELSE 0 END) as away_shots,
        SUM(CASE WHEN me.event_type = 'pass' AND p.team_id = :away_id THEN 1 ELSE 0 END) as away_passes,
        SUM(CASE WHEN me.event_type = 'foul_committed' AND p.team_id = :away_id THEN 1 ELSE 0 END) as away_fouls,
        SUM(CASE WHEN me.event_type = 'card_yellow' AND p.team_id = :away_id THEN 1 ELSE 0 END) as away_cards,
        SUM(CASE WHEN me.event_type = 'assist' AND p.team_id = :away_id THEN 1 ELSE 0 END) as away_assists,
        SUM(CASE WHEN me.event_type = 'tackle' AND p.team_id = :away_id THEN 1 ELSE 0 END) as away_tackles,
        SUM(CASE WHEN me.event_type = 'corner_won' AND p.team_id = :away_id THEN 1 ELSE 0 END) as away_corners,
        SUM(CASE WHEN me.event_type = 'mistake' AND p.team_id = :away_id THEN 1 ELSE 0 END) as away_mistakes
        
     FROM match_events me
     JOIN players p ON me.player_id = p.id
     WHERE me.match_id = :match_id";

    $statsStmt = $pdo->prepare($statsSql);
    $statsStmt->execute([
        'match_id' => $match_id,
        'home_id' => $home_id,
        'away_id' => $away_id
    ]);
    
    $stats = $statsStmt->fetch(PDO::FETCH_ASSOC);

    echo json_encode([
        "home_shots" => strval(intval($stats['home_shots'])),
        "away_shots" => strval(intval($stats['away_shots'])),
        "home_passes" => strval(intval($stats['home_passes'])),
        "away_passes" => strval(intval($stats['away_passes'])),
        "home_fouls" => strval(intval($stats['home_fouls'])),
        "away_fouls" => strval(intval($stats['away_fouls'])),
        "home_cards" => strval(intval($stats['home_cards'])),
        "away_cards" => strval(intval($stats['away_cards'])),
        "home_assists" => strval(intval($stats['home_assists'])),
        "away_assists" => strval(intval($stats['away_assists'])),
        "home_tackles" => strval(intval($stats['home_tackles'])),
        "away_tackles" => strval(intval($stats['away_tackles'])),
        "home_corners" => strval(intval($stats['home_corners'])),
        "away_corners" => strval(intval($stats['away_corners'])),
        "home_mistakes" => strval(intval($stats['home_mistakes'])),
        "away_mistakes" => strval(intval($stats['away_mistakes']))
    ], JSON_UNESCAPED_UNICODE);

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(["error" => $e->getMessage()]);
}
?>