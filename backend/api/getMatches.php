<?php
// 1. Σύνδεση με τη βάση (χρησιμοποιώντας το σωστό path)
require_once '../config/dbConnect.php';

// 2. Ενημερώνουμε ότι αυτό το αρχείο παράγει JSON
header('Content-Type: application/json; charset=utf-8');

try {
    // 3. Query που φέρνει τους αγώνες και τα ονόματα των ομάδων
    $sql = "SELECT 
                m.id, 
                m.match_round, 
                t1.name AS home_team, 
                t2.name AS away_team, 
                m.home_score, 
                m.away_score, 
                m.status 
            FROM matches m
            JOIN teams t1 ON m.home_team_id = t1.id
            JOIN teams t2 ON m.away_team_id = t2.id
            ORDER BY m.match_round ASC";

    $stmt = $pdo->query($sql);
    $matches = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // 4. ΕΔΩ ΓΙΝΕΤΑΙ Η ΜΕΤΑΤΡΟΠΗ ΣΕ JSON ΚΑΙ Η ΕΞΑΓΩΓΗ
    echo json_encode($matches, JSON_UNESCAPED_UNICODE);

} catch (PDOException $e) {
    // Αν υπάρχει σφάλμα, το στέλνουμε πάλι σε μορφή JSON
    echo json_encode(["error" => $e->getMessage()]);
}
?>