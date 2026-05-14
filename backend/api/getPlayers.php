<?php
// getPlayers.php
header('Content-Type: application/json; charset=utf-8');
require_once '../config/dbConnect.php';

// Check if team_id is provided in the URL
if (!isset($_GET['team_id']) || empty($_GET['team_id'])) {
    echo json_encode([]); 
    exit;
}

$team_id = (int)$_GET['team_id'];

try {
    // We MUST include 'photo' in the SELECT statement
    $sql = "SELECT name, position, photo FROM players WHERE team_id = ?";
    $stmt = $pdo->prepare($sql);
    $stmt->execute([$team_id]);
    $players = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // Return the list of players as JSON
    echo json_encode($players, JSON_UNESCAPED_UNICODE);

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(["error" => $e->getMessage()]);
}
?>