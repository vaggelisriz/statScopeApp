<?php
// addMatchStatistic.php
header('Content-Type: application/json; charset=utf-8');
require_once '../config/dbConnect.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(["error" => "Method not allowed"]);
    exit;
}

// Λήψη των δεδομένων που στέλνει το Android Studio μέσω POST
$match_id   = isset($_POST['match_id'])   ? (int)$_POST['match_id']   : null;
$player_id  = isset($_POST['player_id'])  ? (int)$_POST['player_id']  : null;
$team_id    = isset($_POST['team_id'])    ? (int)$_POST['team_id']    : null;
$event_type = isset($_POST['event_type']) ? $_POST['event_type']      : null;
$outcome    = isset($_POST['outcome'])    ? $_POST['outcome']         : null;

// Έλεγχος ότι ήρθαν όλα τα απαραίτητα στοιχεία
if (!$match_id || !$player_id || !$team_id || !$event_type || !$outcome) {
    http_response_code(400);
    echo json_encode(["error" => "Missing required fields"]);
    exit;
}

try {
    // Εισαγωγή του στατιστικού στη βάση δεδομένων
    $sql = "INSERT INTO match_statistics (match_id, player_id, team_id, event_type, outcome) 
            VALUES (?, ?, ?, ?, ?)";
    $stmt = $pdo->prepare($sql);
    $stmt->execute([$match_id, $player_id, $team_id, $event_type, $outcome]);

    echo json_encode(["status" => "success", "message" => "Statistic recorded!"]);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(["error" => $e->getMessage()]);
}
?>