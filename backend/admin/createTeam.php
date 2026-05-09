<!DOCTYPE html>
<html lang="el">
<head>
    <meta charset="UTF-8">
    <title>Δημιουργία Ομάδας - ΕΠΟ</title>
</head>
<body>
    <h2>Προσθήκη Νέας Ομάδας</h2>

    <!-- Εμφάνιση μηνύματος επιτυχίας αν υπάρχει στην URL -->
    <?php if (isset($_GET['status']) && $_GET['status'] == 'success'): ?>
        <p style="color: green;">Η ομάδα δημιουργήθηκε με επιτυχία!</p>
    <?php endif; ?>

    <form action="saveTeam.php" method="POST" enctype="multipart/form-data">
        <div>
            <label>Όνομα Ομάδας:</label><br>
            <input type="text" name="team_name" required>
        </div><br>

        <div>
            <label>Πόλη:</label><br>
            <input type="text" name="city" required>
        </div><br>

        <div>
            <label>Σήμα Ομάδας (Logo):</label><br>
            <input type="file" name="logo" accept="image/*" required>
        </div><br>

        <button type="submit">Αποθήκευση Ομάδας</button>
    </form>
</body>
</html>