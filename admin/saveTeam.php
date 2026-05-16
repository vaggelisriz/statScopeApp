<?php
// Σύνδεση με τη βάση χρησιμοποιώντας το δικό σου αρχείο
require_once '../config/dbConnect.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $name = $_POST['team_name'];
    $city = $_POST['city'];
    
    // 1. Διαχείριση Ανεβάσματος Εικόνας
    $uploads_dir = '../uploads/logos/';
    
    // Δημιουργία φακέλου αν δεν υπάρχει
    if (!file_exists($uploads_dir)) {
        mkdir($uploads_dir, 0777, true);
    }

    // Φτιάχνουμε ένα μοναδικό όνομα για την εικόνα για να μην συμπίπτουν
    $file_extension = pathinfo($_FILES["logo"]["name"], PATHINFO_EXTENSION);
    $new_file_name = time() . "_" . uniqid() . "." . $file_extension;
    $target_file = $uploads_dir . $new_file_name;

    if (move_uploaded_file($_FILES["logo"]["tmp_name"], $target_file)) {
        
        // 2. Εισαγωγή στη Βάση statScopeDB
        try {
            $sql = "INSERT INTO teams (name, city, logo) VALUES (?, ?, ?)";
            $stmt = $pdo->prepare($sql);
            $stmt->execute([$name, $city, $target_file]);

            // 3. Ανακατεύθυνση πίσω στη φόρμα με μήνυμα επιτυχίας
            header("Location: createTeam.php?status=success");
            exit();

        } catch (PDOException $e) {
            die("Σφάλμα στη βάση: " . $e->getMessage());
        }

    } else {
        echo "Σφάλμα κατά το ανέβασμα του αρχείου.";
    }
} else {
    // Αν κάποιος προσπαθήσει να μπει στο αρχείο χωρίς POST, τον διώχνουμε
    header("Location: createTeam.php");
    exit();
}