<?php
// getPlayerDetails.php
header('Content-Type: application/json; charset=utf-8');
require_once '../config/dbConnect.php';

if (!isset($_GET['player_id']) || empty($_GET['player_id'])) {
    echo json_encode(["age" => 0]); 
    exit;
}

$player_id = (int)$_GET['player_id'];

try {
    // Παίρνουμε μόνο την ηλικία για αυτόν τον παίκτη
    $sql = "SELECT age FROM players WHERE id = ?";
    $stmt = $pdo->prepare($sql);
    $stmt->execute([$player_id]);
    $player = $stmt->fetch(PDO::FETCH_ASSOC);

    if ($player) {
        echo json_encode($player);
    } else {
        echo json_encode(["age" => 0]);
    }

} catch (PDOException $e) {
    echo json_encode(["age" => 0, "error" => $e->getMessage()]);
}
?>