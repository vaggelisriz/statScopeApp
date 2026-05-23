<?php
require_once '../config/dbConnect.php';

// Ορίζουμε ότι επιστρέφουμε JSON
header('Content-Type: application/json; charset=utf-8');

// Παίρνουμε τα δεδομένα που έστειλε το Android (POST)
$match_id      = $_POST['match_id'] ?? null;
$player_id     = $_POST['player_id'] ?? null;
$event_type    = $_POST['event_type'] ?? null; // shot, pass, foul, card, corner, tackle, mistake
$outcome       = $_POST['outcome'] ?? 'success'; // success, failure, goal, saved, etc.
$event_minute  = $_POST['event_minute'] ?? 0;

// Ασφαλής έλεγχος για null τιμές
if (is_null($match_id) || is_null($player_id) || is_null($event_type)) {
    echo json_encode(["status" => "error", "message" => "Missing required fields"]);
    exit;
}

try {
    $pdo->beginTransaction();

    // 🛠️ ΔΙΟΡΘΩΣΗ ΠΑΓΙΔΑΣ 1: Μετατροπή του event_type για να ταιριάζει με το ENUM της βάσης
    // Αν η Java στείλει "card" και outcome "yellow", το κάνουμε "card_yellow"
    if ($event_type === 'card' || $event_type === 'cards') {
        if ($outcome === 'yellow') {
            $event_type = 'card_yellow';
        } else if ($outcome === 'red') {
            $event_type = 'card_red';
        }
    }
    
    // Αν η Java στείλει σκέτο "foul", το γυρνάμε στο ENUM σου "foul_committed"
    if ($event_type === 'foul') {
        $event_type = 'foul_committed';
    }
    
    // Αν η Java στείλει "corner", το γυρνάμε στο ENUM σου "corner_won"
    if ($event_type === 'corner') {
        $event_type = 'corner_won';
    }

    // 1. Αποθήκευση του συμβάντος στον πίνακα match_events
    $sql = "INSERT INTO match_events (match_id, player_id, event_type, outcome, event_minute) 
            VALUES (?, ?, ?, ?, ?)";
    $stmt = $pdo->prepare($sql);
    $stmt->execute([$match_id, $player_id, $event_type, $outcome, $event_minute]);

    // 2. ΕΑΝ ΤΟ EVENT ΕΙΝΑΙ ΓΚΟΛ, ΕΝΗΜΕΡΩΝΟΥΜΕ ΤΟ ΣΚΟΡ ΣΤΟΝ ΠΙΝΑΚΑ matches
    if ($event_type === 'shot' && $outcome === 'goal') {
        
        // Βρίσκουμε αν ο παίκτης ανήκει στην εντός ή εκτός έδρας ομάδα
        $queryTeam = "SELECT team_id FROM players WHERE id = ?";
        $stTeam = $pdo->prepare($queryTeam);
        $stTeam->execute([$player_id]);
        $playerTeamId = $stTeam->fetchColumn();

        $queryMatch = "SELECT home_team_id, away_team_id FROM matches WHERE id = ?";
        $stMatch = $pdo->prepare($queryMatch);
        $stMatch->execute([$match_id]);
        $matchData = $stMatch->fetch(PDO::FETCH_ASSOC);

        if ($playerTeamId == $matchData['home_team_id']) {
            $updateScore = "UPDATE matches SET home_score = home_score + 1 WHERE id = ?";
        } else {
            $updateScore = "UPDATE matches SET away_score = away_score + 1 WHERE id = ?";
        }
        
        $stUpdate = $pdo->prepare($updateScore);
        $stUpdate->execute([$match_id]);
    }

    $pdo->commit();
    echo json_encode(["status" => "success", "message" => "Event saved successfully"]);

} catch (Exception $e) {
    $pdo->rollBack();
    echo json_encode(["status" => "error", "message" => $e->getMessage()]);
}
?>