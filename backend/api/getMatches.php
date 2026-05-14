<?php
require_once '../config/dbConnect.php';
header('Content-Type: application/json; charset=utf-8');

try {
    $sql = "SELECT 
                m.id, 
                m.match_round, 
                t1.name AS home_team, 
                t2.name AS away_team, 
                m.home_score, 
                m.away_score, 
                m.status,
                m.home_team_id,
                m.away_team_id
            FROM matches m
            JOIN teams t1 ON m.home_team_id = t1.id
            JOIN teams t2 ON m.away_team_id = t2.id
            ORDER BY m.match_round ASC";

    $stmt = $pdo->query($sql);
    $matches = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode($matches, JSON_UNESCAPED_UNICODE);

} catch (PDOException $e) {
    echo json_encode(["error" => $e->getMessage()]);
}
?>