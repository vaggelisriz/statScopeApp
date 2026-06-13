<?php
// deleteMatchStatistic.php
require_once '../config/dbConnect.php';

// Ορίζουμε ότι επιστρέφουμε JSON
header('Content-Type: application/json; charset=utf-8');

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(["status" => "error", "success" => false, "message" => "Method not allowed"]);
    exit;
}

// Λήψη των δεδομένων από το Android (POST)
$match_id   = $_POST['match_id'] ?? null;
$player_id  = $_POST['player_id'] ?? null;
$event_type = $_POST['event_type'] ?? null;
$outcome    = $_POST['outcome'] ?? 'success';

// Έλεγχος υποχρεωτικών πεδίων
if (is_null($match_id) || is_null($event_type)) {
    echo json_encode(["status" => "error", "success" => false, "message" => "Missing required fields"]);
    exit;
}

try {
    $pdo->beginTransaction();

    // 1. ΔΙΟΡΘΩΣΗ ENUM: Μετατροπή του 'corner_won' σε 'corner' για να ταιριάζει με τη βάση
    if ($event_type === 'corner_won' || $event_type === 'corner') {
        $event_type = 'corner';
    }

    // 2. ΔΙΑΧΕΙΡΙΣΗ ΟΜΑΔΙΚΩΝ ΣΤΑΤΙΣΤΙΚΩΝ (player_id = 0 ή null)
    // Αν το player_id είναι 0, κενό ή null, σημαίνει ομαδικό συμβάν (άρα στη βάση είναι NULL)
    if ($player_id == 0 || is_null($player_id) || $player_id === '') {
        // SQL query για ομαδικά στατιστικά (χρήση IS NULL)
        $sql = "DELETE FROM match_events 
                WHERE match_id = ? 
                  AND player_id IS NULL 
                  AND event_type = ? 
                  AND outcome = ? 
                ORDER BY id DESC LIMIT 1";
        
        $stmt = $pdo->prepare($sql);
        $stmt->execute([$match_id, $event_type, $outcome]);
    } else {
        // SQL query για συγκεκριμένο παίκτη
        $sql = "DELETE FROM match_events 
                WHERE match_id = ? 
                  AND player_id = ? 
                  AND event_type = ? 
                  AND outcome = ? 
                ORDER BY id DESC LIMIT 1";
        
        $stmt = $pdo->prepare($sql);
        $stmt->execute([$match_id, intval($player_id), $event_type, $outcome]);
    }

    // Έλεγχος αν όντως διαγράφηκε κάποια γραμμή
    if ($stmt->rowCount() > 0) {
        $pdo->commit();
        echo json_encode(["status" => "success", "success" => true, "message" => "Event deleted successfully"]);
    } else {
        // Αν δεν βρέθηκε το event, κάνουμε rollback για ασφάλεια
        $pdo->rollBack();
        echo json_encode(["status" => "error", "success" => false, "message" => "Match event not found in database"]);
    }

} catch (Exception $e) {
    if ($pdo->inTransaction()) {
        $pdo->rollBack();
    }
    echo json_encode(["status" => "error", "success" => false, "message" => $e->getMessage()]);
}
?>