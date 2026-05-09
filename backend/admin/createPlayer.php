<?php
require_once '../config/dbConnect.php';

// Φέρνουμε όλες τις ομάδες για να τις βάλουμε στο dropdown
$stmt = $pdo->query("SELECT id, name FROM teams ORDER BY name ASC");
$teams = $stmt->fetchAll();
?>

<!DOCTYPE html>
<html lang="el">
<head>
    <meta charset="UTF-8">
    <title>Δημιουργία Παίκτη - ΕΠΟ</title>
</head>
<body>
    <h2>Προσθήκη Νέου Παίκτη</h2>

    <form action="savePlayer.php" method="POST" enctype="multipart/form-data">
        <div>
            <label>Όνομα Παίκτη:</label><br>
            <input type="text" name="player_name" required>
        </div><br>

        <div>
            <label>Θέση:</label><br>
            <select name="position">
                <option value="Τερματοφύλακας">Τερματοφύλακας</option>
                <option value="Αμυντικός">Αμυντικός</option>
                <option value="Μέσος">Μέσος</option>
                <option value="Επιθετικός">Επιθετικός</option>
            </select>
        </div><br>

        <div>
            <label>Ομάδα:</label><br>
            <select name="team_id" required>
                <option value="">-- Επιλέξτε Ομάδα --</option>
                <?php foreach ($teams as $team): ?>
                    <option value="<?= $team['id'] ?>"><?= htmlspecialchars($team['name']) ?></option>
                <?php endforeach; ?>
            </select>
        </div><br>

        <div>
            <label>Φωτογραφία Παίκτη:</label><br>
            <input type="file" name="photo" accept="image/*">
        </div><br>

        <button type="submit">Αποθήκευση Παίκτη</button>
    </form>
</body>
</html>