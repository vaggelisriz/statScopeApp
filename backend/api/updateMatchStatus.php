<?php
include '../config/dbConnect.php'; 

header('Content-Type: application/json');

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    // Λήψη δεδομένων από το $_POST (όπως τα στέλνει το Retrofit)
    $match_id = isset($_POST['match_id']) ? $_POST['match_id'] : null;
    $status = isset($_POST['status']) ? $_POST['status'] : null;

    if ($match_id && $status) {
        try {
            // Χρησιμοποιούμε το $pdo που ορίσαμε στο dbConnect.php
            $sql = "UPDATE matches SET status = :status WHERE id = :id";
            $stmt = $pdo->prepare($sql);
            
            // Εκτέλεση με το PDO style
            $result = $stmt->execute([
                ':status' => $status,
                ':id'     => $match_id
            ]);

            if ($result) {
                http_response_code(200);
                echo json_encode(["message" => "Match status updated successfully"]);
            } else {
                http_response_code(500);
                echo json_encode(["error" => "Update failed"]);
            }
        } catch (PDOException $e) {
            http_response_code(500);
            echo json_encode(["error" => "Database error: " . $e->getMessage()]);
        }
    } else {
        http_response_code(400);
        echo json_encode(["error" => "Missing parameters. Received match_id: $match_id, status: $status"]);
    }
} else {
    http_response_code(405);
    echo json_encode(["error" => "Method not allowed"]);
}
?>