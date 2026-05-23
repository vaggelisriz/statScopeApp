<?php
// Σωστό path για να βρει το αρχείο σύνδεσης
require_once __DIR__ . '/../config/dbConnect.php'; 

header('Content-Type: application/json');

ini_set('display_errors', 1);
error_reporting(E_ALL);

try {
    $match_id = $_POST['match_id'] ?? null;
    $status = $_POST['status'] ?? 'live';
    
    // 🛠️ ΔΙΟΡΘΩΣΗ: Η PHP ελέγχει και για τα δύο πιθανά Keys (home_starters ή home_ids)
    // ανάλογα με το ποια Activity έκανε την κλήση!
    $home = $_POST['home_starters'] ?? $_POST['home_ids'] ?? [];
    $away = $_POST['away_starters'] ?? $_POST['away_ids'] ?? [];

    if (!$match_id) {
        throw new Exception("Missing match_id");
    }

    $pdo->beginTransaction();

    // 1. Update Matches
    $stmt1 = $pdo->prepare("UPDATE matches SET status = ? WHERE id = ?");
    $stmt1->execute([$status, $match_id]);

    // 2. Delete Old Lineups
    $stmt2 = $pdo->prepare("DELETE FROM match_lineups WHERE match_id = ?");
    $stmt2->execute([$match_id]);

    // 3. Insert New Lineups
    $stmt3 = $pdo->prepare("INSERT INTO match_lineups (match_id, player_id) VALUES (?, ?)");

    // Διασφάλιση ότι αν ήρθε μόνο ένα ID (όχι array), το μετατρέπουμε σε array για να μην σκάσει το foreach
    if (!is_array($home)) { $home = [$home]; }
    if (!is_array($away)) { $away = [$away]; }

    foreach ($home as $p_id) {
        if (!empty($p_id)) {
            $stmt3->execute([$match_id, (int)$p_id]);
        }
    }
    foreach ($away as $p_id) {
        if (!empty($p_id)) {
            $stmt3->execute([$match_id, (int)$p_id]);
        }
    }

    $pdo->commit();
    echo json_encode(["status" => "success"]);

} catch (Exception $e) {
    if (isset($pdo) && $pdo->inTransaction()) {
        $pdo->rollBack();
    }
    http_response_code(500);
    echo json_encode(["status" => "error", "message" => "PHP Error: " . $e->getMessage()]);
}
?>