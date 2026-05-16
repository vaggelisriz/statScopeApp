<?php
require_once '../config/dbConnect.php';
header('Content-Type: application/json; charset=utf-8');

try {
    // Added championship name join and select
    $sql = "SELECT 
                m.id, 
                m.match_round, 
                t1.name AS home_team, 
                t1.logo AS home_logo, 
                t2.name AS away_team, 
                t2.logo AS away_logo, 
                m.home_score, 
                m.away_score, 
                m.status,
                m.home_team_id,
                m.away_team_id,
                c.name AS championship_name 
            FROM matches m
            JOIN teams t1 ON m.home_team_id = t1.id
            JOIN teams t2 ON m.away_team_id = t2.id
            JOIN championships c ON m.championship_id = c.id
            ORDER BY m.match_round ASC";

    $stmt = $pdo->query($sql);
    $matches = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // Return data in JSON
    echo json_encode($matches, JSON_UNESCAPED_UNICODE);

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(["error" => $e->getMessage()]);
}
?>