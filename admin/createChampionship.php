<?php require_once '../config/dbConnect.php'; ?>
<!DOCTYPE html>
<html lang="el">
<head>
    <meta charset="UTF-8">
    <title>Δημιουργία Πρωταθλήματος</title>
    <style>
        body { font-family: sans-serif; margin: 40px; background: #f4f4f4; }
        .container { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); max-width: 500px; }
        .team-list { margin: 15px 0; max-height: 200px; overflow-y: auto; border: 1px solid #ddd; padding: 10px; }
        .team-item { margin-bottom: 5px; }
        button { background: #007bff; color: white; padding: 10px; border: none; cursor: pointer; width: 100%; }
    </style>
</head>
<body>

<div class="container">
    <h2>Νέο Πρωτάθλημα</h2>
    <form action="generateFixtures.php" method="POST">
        <label><b>Όνομα Πρωταθλήματος:</b></label><br>
        <input type="text" name="championship_name" placeholder="π.χ. Summer Cup" required style="width:100%; padding:8px; margin:10px 0;">

        <label><b>Επιλέξτε Ομάδες:</b></label>
        <div class="team-list">
            <?php
            // Φέρνουμε όλες τις διαθέσιμες ομάδες
            $stmt = $pdo->query("SELECT id, name FROM teams ORDER BY name ASC");
            while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
                echo "<div class='team-item'>";
                echo "<input type='checkbox' name='selected_teams[]' value='{$row['id']}'> {$row['name']}";
                echo "</div>";
            }
            ?>
        </div>

        <button type="submit">Δημιουργία & Κλήρωση</button>
    </form>
</div>

</body>
</html>