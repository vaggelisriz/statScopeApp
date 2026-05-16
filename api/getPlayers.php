<?php
// getPlayers.php — Επιστρέφει όλους τους παίκτες μιας ομάδας
// URL: GET /api/getPlayers.php?team_id=1

header('Content-Type: application/json; charset=utf-8');

// ✅ Επιτρέπουμε CORS για development (emulator → Apache)
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');

// ✅ Χρησιμοποιούμε __DIR__ για αξιόπιστο path (ανεξάρτητο από working directory)
require_once __DIR__ . '/../config/dbConnect.php';

// ─── Validation ──────────────────────────────────────────────────────────────
if (!isset($_GET['team_id']) || !is_numeric($_GET['team_id'])) {
    http_response_code(400);
    echo json_encode([
        "status"  => "error",
        "message" => "Missing or invalid team_id parameter"
    ], JSON_UNESCAPED_UNICODE);
    exit;
}

$team_id = (int) $_GET['team_id'];

if ($team_id <= 0) {
    http_response_code(400);
    echo json_encode([
        "status"  => "error",
        "message" => "team_id must be a positive integer"
    ], JSON_UNESCAPED_UNICODE);
    exit;
}

// ─── Query ───────────────────────────────────────────────────────────────────
try {
    // ✅ Επιστρέφουμε id και team_id μαζί — χρειάζονται στην Android activity
    //    για να ξέρει ποιος παίκτης ανήκει σε ποια ομάδα (home vs away split)
    $sql = "SELECT id, name, position, photo, number, team_id 
            FROM players 
            WHERE team_id = ? 
            ORDER BY position, name";

    $stmt = $pdo->prepare($sql);
    $stmt->execute([$team_id]);
    $players = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // ✅ Πάντα επιστρέφουμε array (ακόμα κι αν είναι κενό) — ποτέ null
    echo json_encode($players, JSON_UNESCAPED_UNICODE);

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        "status"  => "error",
        "message" => "Database error: " . $e->getMessage()
    ], JSON_UNESCAPED_UNICODE);
}
?>