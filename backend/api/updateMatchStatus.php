<?php
include '../config/dbConnect.php'; 

header('Content-Type: application/json');

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $match_id = $_POST['match_id'];
    $status = $_POST['status'];

    if (!empty($match_id) && !empty($status)) {
        $sql = "UPDATE matches SET status = ? WHERE id = ?";
        $stmt = $conn->prepare($sql);
        
        if ($stmt) {
            $stmt->bind_param("si", $status, $match_id);
            if ($stmt->execute()) {
                http_response_code(200);
                echo json_encode(["message" => "Match status updated successfully"]);
            } else {
                http_response_code(500);
                echo json_encode(["error" => "Failed to update database"]);
            }
        } else {
            http_response_code(500);
            echo json_encode(["error" => "Failed to prepare statement"]);
        }
    } else {
        http_response_code(400);
        echo json_encode(["error" => "Missing parameters"]);
    }
} else {
    http_response_code(405);
    echo json_encode(["error" => "Method not allowed"]);
}
?>