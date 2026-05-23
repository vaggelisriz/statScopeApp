<?php
// finishMatch.php
require_once '../config/dbConnect.php';
header('Content-Type: application/json; charset=utf-8');

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(["status" => "error", "message" => "Method not allowed"]);
    exit;
}

$match_id = isset($_POST['match_id']) ? intval($_POST['match_id']) : null;

if (!$match_id) {
    echo json_encode(["status" => "error", "message" => "Missing match_id"]);
    exit;
}

try {
    // Ενημερώνουμε την κατάσταση του αγώνα σε 'completed'
    $sql = "UPDATE matches SET status = 'completed' WHERE id = ?";
    $stmt = $pdo->prepare($sql);
    $stmt->execute([$match_id]);

    if ($stmt->rowCount() > 0) {
        echo json_encode([
            "status" => "success", 
            "message" => "Match successfully finished! Standings are now calculated dynamically."
        ]);
    } else {
        echo json_encode([
            "status" => "error", 
            "message" => "Match not found or already completed."
        ]);
    }

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(["status" => "error", "message" => "Database Error: " . $e->getMessage()]);
}
?>