<?php
require_once '../config/dbConnect.php';
header('Content-Type: application/json; charset=utf-8');

$match_id = isset($_GET['match_id']) ? intval($_GET['match_id']) : 0;

if ($match_id === 0) {
    echo json_encode(["error" => "Missing match_id"]);
    exit;
}

try {
    // Συνδέουμε την ομάδα (teams) μέσω του πίνακα των παικτών (players)
    $sql = "SELECT 
                me.id,
                p.name AS player_name,
                t.name AS team_name,
                me.event_type,
                me.outcome,
                me.event_minute
            FROM match_events me
            LEFT JOIN players p ON me.player_id = p.id
            LEFT JOIN teams t ON p.team_id = t.id
            WHERE me.match_id = :match_id
            ORDER BY me.id DESC";

    $stmt = $pdo->prepare($sql);
    $stmt->execute(['match_id' => $match_id]);
    $events = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode($events, JSON_UNESCAPED_UNICODE);

} catch (PDOException $e) {
    http_response_code(200); 
    echo json_encode([["player_name" => "ERROR", "team_name" => "SYSTEM", "event_type" => "SQL_ERROR", "outcome" => "FAIL", "event_minute" => 0, "message" => $e->getMessage()]], JSON_UNESCAPED_UNICODE);
}
?>