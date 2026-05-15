<?php
include 'db_config.php';

// Έλεγχος αν υπάρχουν τα απαραίτητα δεδομένα
if (!isset($_POST['match_id']) || !isset($_POST['home_starters']) || !isset($_POST['away_starters'])) {
    http_response_code(400);
    echo json_encode(["status" => "error", "message" => "Missing data"]);
    exit;
}

$match_id = $_POST['match_id'];
$status = $_POST['status'];
$home_starters = $_POST['home_starters']; 
$away_starters = $_POST['away_starters'];

$conn->begin_transaction();

try {
    // 1. Ενημέρωση status
    $stmt1 = $conn->prepare("UPDATE matches SET status = ? WHERE id = ?");
    $stmt1->bind_param("si", $status, $match_id);
    $stmt1->execute();

    // 2. Καλό είναι να καθαρίζουμε τυχόν παλιά lineups για αυτό το match_id 
    // ώστε να μην έχουμε Primary Key errors αν ξαναγίνει το αίτημα
    $clear = $conn->prepare("DELETE FROM match_lineups WHERE match_id = ?");
    $clear->bind_param("i", $match_id);
    $clear->execute();

    // 3. Εισαγωγή Lineups
    $stmt2 = $conn->prepare("INSERT INTO match_lineups (match_id, player_id) VALUES (?, ?)");

    // Home
    foreach ($home_starters as $p_id) {
        $stmt2->bind_param("ii", $match_id, $p_id);
        $stmt2->execute();
    }

    // Away
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