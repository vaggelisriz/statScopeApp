<?php
// getMatchLineups.php — Επιστρέφει την 11άδα για ένα ματς
// URL: GET /api/getMatchLineups.php?match_id=1

header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');

require_once __DIR__ . '/../config/dbConnect.php';

// ─── Validation ──────────────────────────────────────────────────────────────
if (!isset($_GET['match_id']) || !is_numeric($_GET['match_id'])) {
    http_response_code(400);
    echo json_encode(["status" => "error", "message" => "Missing or invalid match_id"]);
    exit;
}

$match_id = (int) $_GET['match_id'];

try {
    // ✅ JOIN με players για να επιστρέψουμε ΟΛΑ τα στοιχεία παίκτη
    //    Συμπεριλαμβάνουμε team_id ώστε η Android app να κάνει home/away split
    $query = "SELECT p.id, p.name, p.position, p.photo, p.number, p.team_id
              FROM match_lineups ml
              JOIN players p ON ml.player_id = p.id
              WHERE ml.match_id = ?
              ORDER BY p.team_id, p.position, p.name";

    $stmt = $pdo->prepare($query);
    $stmt->execute([$match_id]);
    $all_players = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // ✅ Επιστρέφουμε πάντα status + players (ακόμα κι αν είναι κενό array)
    //    Η Android Activity ελέγχει αν players.isEmpty() για να αποφασίσει
    //    αν θα φορτώσει default lineup από getPlayers.php
    echo json_encode([
        "status"  => "success",
        "players" => $all_players   // [] αν νέο ματς χωρίς εγγραφές
    ], JSON_UNESCAPED_UNICODE);

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        "status"  => "error",
        "message" => "Database error: " . $e->getMessage()
    ]);
}
?>