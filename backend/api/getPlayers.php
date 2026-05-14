<?php
header('Content-Type: application/json; charset=utf-8');
require_once '../config/dbConnect.php';

if (!isset($_GET['team_id']) || empty($_GET['team_id'])) {
    echo json_encode([]); // Επιστρέφει άδεια λίστα αν λείπει το ID
    exit;
}

$team_id = (int)$_GET['team_id'];

try {
    // Επιλέγουμε τα πεδία που χρειάζεται το Player.java
    $sql = "SELECT name, position FROM players WHERE team_id = ?";
    $stmt = $pdo->prepare($sql);
    $stmt->execute([$team_id]);
    $players = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // Στέλνουμε απευθείας τον πίνακα, χωρίς το "status" και το "data"
    echo json_encode($players);

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([]);
}
?>