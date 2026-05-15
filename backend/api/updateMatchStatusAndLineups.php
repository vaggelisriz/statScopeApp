<?php
include 'db_config.php'; // Η σύνδεσή σου με τη βάση

$match_id = $_POST['match_id'];
$status = $_POST['status'];
$home_starters = $_POST['home_starters']; // Αυτό είναι array λόγω Retrofit []
$away_starters = $_POST['away_starters'];

// Ξεκινάμε Transaction για να σιγουρευτούμε ότι αν αποτύχει κάτι, δεν θα αλλάξει τίποτα
$conn->begin_transaction();

try {
    // 1. Ενημέρωση του αγώνα σε "live"
    $stmt1 = $conn->prepare("UPDATE matches SET status = ? WHERE id = ?");
    $stmt1->bind_param("si", $status, $match_id);
    $stmt1->execute();

    // 2. Εισαγωγή των βασικών παικτών (Lineups)
    // Υποθέτουμε ότι έχεις έναν πίνακα match_lineups (match_id, player_id, team_type)
    $stmt2 = $conn->prepare("INSERT INTO match_lineups (match_id, player_id) VALUES (?, ?)");

    // Προσθήκη γηπεδούχων
    foreach ($home_starters as $p_id) {
        $stmt2->bind_param("ii", $match_id, $p_id);
        $stmt2->execute();
    }

    // Προσθήκη φιλοξενούμενων
    foreach ($away_starters as $p_id) {
        $stmt2->bind_param("ii", $match_id, $p_id);
        $stmt2->execute();
    }

    $conn->commit();
    echo json_encode(["status" => "success"]);

} catch (Exception $e) {
    $conn->rollback();
    http_response_code(500);
    echo json_encode(["status" => "error", "message" => $e->getMessage()]);
}

$conn->close();
?>