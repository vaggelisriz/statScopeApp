<?php
require_once '../config/dbConnect.php';
header('Content-Type: application/json; charset=utf-8');

// Διαβάζουμε το player_id από την GET κλήση του OkHttp
$player_id = isset($_GET['player_id']) ? intval($_GET['player_id']) : 0;

if ($player_id === 0) {
    echo json_encode(["error" => "Missing player_id"]);
    exit;
}

try {
    // SQL Query που υπολογίζει τα SUM/COUNT από τον match_events πίνακα 
    $sql = "SELECT 
        SUM(CASE WHEN event_type = 'shot' AND outcome = 'goal' THEN 1 ELSE 0 END) as total_goals,
        SUM(CASE WHEN event_type = 'assist' THEN 1 ELSE 0 END) as total_assists,
        SUM(CASE WHEN event_type = 'card_yellow' THEN 1 ELSE 0 END) as total_yellow_cards,
        SUM(CASE WHEN event_type = 'card_red' THEN 1 ELSE 0 END) as total_red_cards,
        (SELECT COUNT(DISTINCT match_id) FROM match_events WHERE player_id = :player_id) as appearances
    FROM match_events 
    WHERE player_id = :player_id";

    $stmt = $pdo->prepare($sql);
    $stmt->execute(['player_id' => $player_id]);
    $stats = $stmt->fetch(PDO::FETCH_ASSOC);

    // Αν ο παίκτης δεν έχει ακόμα κανένα event, γεμίζουμε με μηδενικά
    if (!$stats || $stats['appearances'] == null) {
        $stats = [
            "total_goals" => 0,
            "total_assists" => 0,
            "total_yellow_cards" => 0,
            "total_red_cards" => 0,
            "appearances" => 0
        ];
    } else {
        // Μετατροπή των null τιμών της SUM σε 0, αν δεν έχει π.χ. γκολ
        $stats['total_goals'] = $stats['total_goals'] ? intval($stats['total_goals']) : 0;
        $stats['total_assists'] = $stats['total_assists'] ? intval($stats['total_assists']) : 0;
        $stats['total_yellow_cards'] = $stats['total_yellow_cards'] ? intval($stats['total_yellow_cards']) : 0;
        $stats['total_red_cards'] = $stats['total_red_cards'] ? intval($stats['total_red_cards']) : 0;
        $stats['appearances'] = intval($stats['appearances']);
    }

    echo json_encode($stats, JSON_UNESCAPED_UNICODE);

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(["error" => $e->getMessage()]);
}
?>