<?php
// getPlayers.php
header('Content-Type: application/json; charset=utf-8');
require_once '../config/dbConnect.php';

// Έλεγχος αν δόθηκε το team_id στο URL
if (!isset($_GET['team_id']) || empty($_GET['team_id'])) {
    echo json_encode([]); 
    exit;
}

$team_id = (int)$_GET['team_id'];

try {
    $sql = "SELECT id, name, position, photo FROM players WHERE team_id = ?";
    $stmt = $pdo->prepare($sql);
    $stmt->execute([$team_id]);
    $players = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // Επιστροφή της λίστας ως JSON
    echo json_encode($players, JSON_UNESCAPED_UNICODE);

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(["error" => $e->getMessage()]);
}
?>