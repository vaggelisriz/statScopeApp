<?php
require_once '../config/dbConnect.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $name = $_POST['player_name'];
    $position = $_POST['position'];
    $team_id = $_POST['team_id'];
    
    // Διαχείριση Φωτογραφίας
    $uploads_dir = '../uploads/players/';
    if (!file_exists($uploads_dir)) {
        mkdir($uploads_dir, 0777, true);
    }

    $photo_path = "";
    if (!empty($_FILES["photo"]["name"])) {
        $file_extension = pathinfo($_FILES["photo"]["name"], PATHINFO_EXTENSION);
        $photo_path = $uploads_dir . time() . "_" . uniqid() . "." . $file_extension;
        move_uploaded_file($_FILES["photo"]["tmp_name"], $photo_path);
    }

    try {
        $sql = "INSERT INTO players (name, position, team_id, photo) VALUES (?, ?, ?, ?)";
        $stmt = $pdo->prepare($sql);
        $stmt->execute([$name, $position, $team_id, $photo_path]);

        echo "Ο παίκτης αποθηκεύτηκε! <a href='createPlayer.php'>Προσθήκη κι άλλου</a>";
    } catch (PDOException $e) {
        die("Σφάλμα: " . $e->getMessage());
    }
}