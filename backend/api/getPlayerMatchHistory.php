<?php
require_once '../config/dbConnect.php';
header('Content-Type: application/json; charset=utf-8');

$player_id = isset($_GET['player_id']) ? intval($_GET['player_id']) : 0;

if ($player_id === 0) {
    echo json_encode(["error" => "Missing player_id"]);
    exit;
}

try {
    // Query που φέρνει τα στατιστικά ανά αγώνα κάνοντας JOIN με τα matches και τις ομάδες
    $sql = "SELECT 
                m.id AS match_id,
                t1.name AS home_team,
                t2.name AS away_team,
                SUM(CASE WHEN me.event_type = 'shot' AND me.outcome = 'goal' THEN 1 ELSE 0 END) AS match_goals,
                SUM(CASE WHEN me.event_type = 'assist' THEN 1 ELSE 0 END) AS match_assists,
                SUM(CASE WHEN me.event_type = 'card_yellow' THEN 1 ELSE 0 END) AS match_yellow_cards,
                SUM(CASE WHEN me.event_type = 'card_red' THEN 1 ELSE 0 END) AS match_red_cards
            FROM match_events me
            JOIN matches m ON me.match_id = m.id
            JOIN teams t1 ON m.home_team_id = t1.id
            JOIN teams t2 ON m.away_team_id = t2.id
            WHERE me.player_id = :player_id
            GROUP BY m.id
            ORDER BY m.id DESC";

    $stmt = $pdo->prepare($sql);
    $stmt->execute(['player_id' => $player_id]);
    $history = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode($history, JSON_UNESCAPED_UNICODE);

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(["error" => $e->getMessage()]);
}
?>