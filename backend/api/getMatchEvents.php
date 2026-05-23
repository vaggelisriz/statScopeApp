<?php
require_once '../config/dbConnect.php';
header('Content-Type: application/json; charset=utf-8');

$match_id = isset($_GET['match_id']) ? intval($_GET['match_id']) : 0;

if ($match_id === 0) {
    echo json_encode(["error" => "Missing match_id"]);
    exit;
}

try {
    // Query που μαζεύει τα events του αγώνα, τα ονόματα των παικτών και των ομάδων τους
    // ΠΡΟΣΑΡΜΟΣΕ τα ονόματα των πινάκων/στηλών αν διαφέρουν στη δική σου βάση δεδομένων
    $sql = "SELECT 
                me.id,
                p.name AS player_name,
                t.name AS team_name,
                me.event_type,
                me.outcome
            FROM match_events me
            JOIN players p ON me.player_id = p.id
            JOIN teams t ON p.team_id = t.id
            WHERE me.match_id = :match_id
            ORDER BY me.id DESC";

    $stmt = $pdo->prepare($sql);
    $stmt->execute(['match_id' => $match_id]);
    $events = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // Επιστρέφει ΠΑΝΤΑ Array (ακόμα και άδειο αν δεν υπάρχουν stats)
    echo json_encode($events, JSON_UNESCAPED_UNICODE);

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(["error" => $e->getMessage()]);
}
?>