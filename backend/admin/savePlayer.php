<?php
require_once '../config/dbConnect.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    // 🔒 SERVERSIDE ΕΛΕΓΧΟΣ: Εξασφαλίζουμε ότι όλα τα κείμενα έχουν συμπληρωθεί
    $name = isset($_POST['player_name']) ? trim($_POST['player_name']) : '';
    $position = isset($_POST['position']) ? trim($_POST['position']) : '';
    $team_id = isset($_POST['team_id']) ? intval($_POST['team_id']) : 0;
    
    if (empty($name) || empty($position) || $team_id === 0 || empty($_FILES["photo"]["name"])) {
        die("Σφάλμα: Όλα τα πεδία είναι υποχρεωτικά (Όνομα, Θέση, Ομάδα και Φωτογραφία).");
    }
    
    // Διαχείριση Φωτογραφίας
    $uploads_dir = '../uploads/players/';
    if (!file_exists($uploads_dir)) {
        mkdir($uploads_dir, 0777, true);
    }

    $file_extension = pathinfo($_FILES["photo"]["name"], PATHINFO_EXTENSION);
    $photo_path = $uploads_dir . time() . "_" . uniqid() . "." . $file_extension;
    
    // 🔒 SERVERSIDE ΕΛΕΓΧΟΣ: Επιβεβαίωση ότι το αρχείο ανέβηκε σωστά στον σέρβερ
    if (move_uploaded_file($_FILES["photo"]["tmp_name"], $photo_path)) {
        try {
            $sql = "INSERT INTO players (name, position, team_id, photo) VALUES (?, ?, ?, ?)";
            $stmt = $pdo->prepare($sql);
            $stmt->execute([$name, $position, $team_id, $photo_path]);

            // ✅ Η ΚΟΜΨΗ ΔΙΟΡΘΩΣΗ: Ανακατεύθυνση πίσω στη φόρμα με status=success
            header("Location: createPlayer.php?status=success");
            exit();
            
        } catch (PDOException $e) {
            die("Σφάλμα στη βάση δεδομένων: " . $e->getMessage());
        }
    } else {
        die("Σφάλμα: Αποτυχία κατά το ανέβασμα της φωτογραφίας του παίκτη.");
    }
} else {
    header("Location: createPlayer.php");
    exit();
}
?>