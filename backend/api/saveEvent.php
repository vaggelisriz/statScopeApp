<?php
require_once '../config/dbConnect.php';

// Ορίζουμε ότι επιστρέφουμε JSON
header('Content-Type: application/json; charset=utf-8');

// Παίρνουμε τα δεδομένα που έστειλε το Android (POST)
$match_id          = $_POST['match_id'] ?? null;
$player_id         = $_POST['player_id'] ?? null;
$event_type        = $_POST['event_type'] ?? null; 
$outcome           = $_POST['outcome'] ?? 'success'; 
$event_minute      = $_POST['event_minute'] ?? 0;
$is_home_selected  = $_POST['is_home_selected'] ?? null; // Λήψη της πληροφορίας για την ομάδα

// Ασφαλής έλεγχος για υποχρεωτικά πεδία
if (is_null($match_id) || is_null($event_type)) {
    echo json_encode(["status" => "error", "success" => false, "message" => "Missing required fields"]);
    exit;
}

try {
    $pdo->beginTransaction();

    // 🛠️ ΕΔΩ ΕΙΝΑΙ Η ΠΡΟΣΘΗΚΗ: Μετατρέπουμε το 'corner_won' της Java στο 'corner' που θέλει το ENUM της βάσης σου
    if ($event_type === 'corner_won' || $event_type === 'corner') {
        $event_type = 'corner';
    }

    // Μετατροπή του player_id σε NULL αν έρθει 0 ή κενό από το Android
    $db_player_id = ($player_id == 0 || is_null($player_id) || $player_id === '') ? null : intval($player_id);

    // 1. Αποθήκευση του συμβάντος στον πίνακα match_events
    $sql = "INSERT INTO match_events (match_id, player_id, event_type, outcome, event_minute) 
            VALUES (?, ?, ?, ?, ?)";
    $stmt = $pdo->prepare($sql);
    $stmt->execute([$match_id, $db_player_id, $event_type, $outcome, $event_minute]);

    // 2. ΕΑΝ ΤΟ EVENT ΕΙΝΑΙ ΓΚΟΛ, ΕΝΗΜΕΡΩΝΟΥΜΕ ΤΟ ΣΚΟΡ ΣΤΟΝ ΠΙΝΑΚΑ matches
    if ($event_type === 'shot' && $outcome === 'goal') {
        
        $queryMatch = "SELECT home_team_id, away_team_id FROM matches WHERE id = ?";
        $stMatch = $pdo->prepare($queryMatch);
        $stMatch->execute([$match_id]);
        $matchData = $stMatch->fetch(PDO::FETCH_ASSOC);

        if ($db_player_id !== null) {
            // Εάν το γκολ σημειώθηκε από πραγματικό παίκτη
            $queryTeam = "SELECT team_id FROM players WHERE id = ?";
            $stTeam = $pdo->prepare($queryTeam);
            $stTeam->execute([$db_player_id]);
            $playerTeamId = $stTeam->fetchColumn();

            if ($playerTeamId == $matchData['home_team_id']) {
                $updateScore = "UPDATE matches SET home_score = home_score + 1 WHERE id = ?";
            } else {
                $updateScore = "UPDATE matches SET away_score = away_score + 1 WHERE id = ?";
            }
        } else {
            // ΔΙΟΡΘΩΣΗ: Εάν είναι ομαδικό γκολ (π.χ. αυτογκόλ από λάθος), βασιζόμαστε στο is_home_selected της Java
            // Αν η Java λέει ότι επιλέχθηκε η Home Team, τότε το γκολ πάει στην Home Team.
            if ($is_home_selected === 'true' || $is_home_selected === '1' || $is_home_selected === true) {
                $updateScore = "UPDATE matches SET home_score = home_score + 1 WHERE id = ?";
            } else {
                $updateScore = "UPDATE matches SET away_score = away_score + 1 WHERE id = ?";
            }
        }
        
        $stUpdate = $pdo->prepare($updateScore);
        $stUpdate->execute([$match_id]);
    }

    $pdo->commit();
    echo json_encode(["status" => "success", "success" => true, "message" => "Event saved successfully"]);

} catch (Exception $e) {
    $pdo->rollBack();
    echo json_encode(["status" => "error", "success" => false, "message" => $e->getMessage()]);
}
?>