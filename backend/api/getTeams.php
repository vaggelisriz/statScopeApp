<?php
require_once '../config/dbConnect.php';
header('Content-Type: application/json; charset=utf-8');

// Διαβάζουμε το championship_id από το URL
$championship_id = isset($_GET['championship_id']) ? intval($_GET['championship_id']) : 0;

try {
    // ΘΩΡΑΚΙΣΗ: Αν είναι 0 ή -1 ή οποιοσδήποτε αρνητικός αριθμός, φέρε ΟΛΕΣ τις ομάδες
    if ($championship_id <= 0) {
        $sql = "SELECT id, name, city, logo FROM teams ORDER BY name ASC";
        $stmt = $pdo->query($sql);
        $teams = $stmt->fetchAll(PDO::FETCH_ASSOC);
    } 
    // Διαφορετικά, φέρε στοχευμένα μόνο τις ομάδες του συγκεκριμένου πρωταθλήματος
    else {
        $sql = "SELECT DISTINCT t.id, t.name, t.city, t.logo 
                FROM teams t
                JOIN matches m ON (t.id = m.home_team_id OR t.id = m.away_team_id)
                WHERE m.championship_id = :championship_id
                ORDER BY t.name ASC";
                
        $stmt = $pdo->prepare($sql);
        $stmt->execute(['championship_id' => $championship_id]);
        $teams = $stmt->fetchAll(PDO::FETCH_ASSOC);
        
        // Αν για κάποιο λόγο το query του πρωταθλήματος επιστρέψει άδεια λίστα, φέρε πάλι όλες τις ομάδες
        if (empty($teams)) {
            $sql = "SELECT id, name, city, logo FROM teams ORDER BY name ASC";
            $stmt = $pdo->query($sql);
            $teams = $stmt->fetchAll(PDO::FETCH_ASSOC);
        }
    }

    echo json_encode($teams, JSON_UNESCAPED_UNICODE);

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(["error" => $e->getMessage()]);
}
?>