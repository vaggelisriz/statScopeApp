<?php
// addMatchStatistic.php
header('Content-Type: application/json; charset=utf-8');
require_once '../config/dbConnect.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(["status" => "error", "error" => "Method not allowed"]);
    exit;
}

// Λήψη των δεδομένων και έλεγχος με is_null() για να μην απορρίπτεται το ID αν είναι 0
$match_id   = isset($_POST['match_id'])   && $_POST['match_id'] !== ''   ? (int)$_POST['match_id']   : null;
$player_id  = isset($_POST['player_id'])  && $_POST['player_id'] !== ''  ? (int)$_POST['player_id']  : null;
$team_id    = isset($_POST['team_id'])    && $_POST['team_id'] !== ''    ? (int)$_POST['team_id']    : null;
$event_type = isset($_POST['event_type']) && $_POST['event_type'] !== '' ? $_POST['event_type']      : null;
$outcome    = isset($_POST['outcome'])    && $_POST['outcome'] !== ''    ? $_POST['outcome']         : null;

// Έλεγχος αν κάποιο πεδίο είναι όντως null
if (is_null($match_id) || is_null($player_id) || is_null($team_id) || is_null($event_type) || is_null($outcome)) {
    echo json_encode([
        "status" => "error", 
        "error" => "Missing required fields. Received: match_id=$match_id, player_id=$player_id, team_id=$team_id, event_type=$event_type, outcome=$outcome"
    ]);
    exit;
}

try {
    // ✅ ΔΙΟΡΘΩΘΗΚΕ: Ο πίνακας αλλάχτηκε από match_statistics σε match_events
    $sql = "INSERT INTO match_events (match_id, player_id, team_id, event_type, outcome) 
            VALUES (?, ?, ?, ?, ?)";
    $stmt = $pdo->prepare($sql);
    $stmt->execute([$match_id, $player_id, $team_id, $event_type, $outcome]);

    echo json_encode(["status" => "success", "message" => "Statistic recorded successfully!"]);
} catch (PDOException $e) {
    // Επιστρέφουμε το ακριβές SQL σφάλμα δομημένα στο JSON για να το βλέπουμε στο Android αν χρειαστεί
    echo json_encode(["status" => "error", "error" => "Database Error: " . $e->getMessage()]);
}
?>