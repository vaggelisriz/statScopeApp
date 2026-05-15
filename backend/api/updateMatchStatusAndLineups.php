<?php
include '../config/dbConnect.php'; 

// Το Retrofit στέλνει τα δεδομένα στο $_POST
$match_id = $_POST['match_id'] ?? null;
$status = $_POST['status'] ?? 'live';
$home_starters = $_POST['home_starters'] ?? [];
$away_starters = $_POST['away_starters'] ?? [];

if (!$match_id) {
    http_response_code(400);
    echo json_encode(["status" => "error", "message" => "No match_id provided"]);
    exit;
}

try {
   
    $pdo->beginTransaction();

    // 1. Ενημέρωση Status
    $stmt = $pdo->prepare("UPDATE matches SET status = ? WHERE id = ?");
    $stmt->execute([$status, $match_id]);

    // 2. Καθαρισμός παλιών 
    $del = $pdo->prepare("DELETE FROM match_lineups WHERE match_id = ?");
    $del->execute([$match_id]);

    // 3. Εισαγωγή νέων
    $ins = $pdo->prepare("INSERT INTO match_lineups (match_id, player_id) VALUES (?, ?)");

    foreach ($home_starters as $id) {
        $ins->execute([$match_id, $id]);
    }
    foreach ($away_starters as $id) {
        $ins->execute([$match_id, $id]);
    }

    $pdo->commit();
    echo json_encode(["status" => "success"]);

} catch (Exception $e) {
    if ($pdo->inTransaction()) $pdo->rollBack();
    http_response_code(500);
    echo json_encode(["status" => "error", "message" => $e->getMessage()]);
}