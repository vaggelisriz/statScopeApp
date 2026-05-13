<?php
// Ορισμός του header ώστε ο client (π.χ. mobile app, JS fetch) να γνωρίζει ότι λαμβάνει JSON
header('Content-Type: application/json; charset=utf-8');

require_once '../config/dbConnect.php';

// Έλεγχος αν έχει σταλεί το id του παίκτη μέσω GET
if (!isset($_GET['id']) || empty($_GET['id'])) {
    echo json_encode([
        "status" => "error",
        "message" => "Δεν δόθηκε ID παίκτη."
    ]);
    exit;
}

$player_id = (int)$_GET['id'];

try {
    // SQL ερώτημα με JOIN για να πάρουμε και το όνομα της ομάδας
    $sql = "SELECT p.id, p.name, p.position, p.photo, p.team_id, t.name as team_name 
            FROM players p 
            LEFT JOIN teams t ON p.team_id = t.id 
            WHERE p.id = ?";
            
    $stmt = $pdo->prepare($sql);
    $stmt->execute([$player_id]);
    $player = $stmt->fetch(PDO::FETCH_ASSOC);

    if ($player) {
        // Επιτυχία: Επιστροφή των δεδομένων
        echo json_encode([
            "status" => "success",
            "data" => $player
        ]);
    } else {
        // Αν δεν βρέθηκε ο παίκτης
        http_response_code(404);
        echo json_encode([
            "status" => "error",
            "message" => "Ο παίκτης δεν βρέθηκε."
        ]);
    }

} catch (PDOException $e) {
    // Διαχείριση σφάλματος βάσης
    http_response_code(500);
    echo json_encode([
        "status" => "error",
        "message" => "Σφάλμα συστήματος: " . $e->getMessage()
    ]);
}