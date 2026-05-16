<!DOCTYPE html>
<html lang="el">
<head>
    <meta charset="UTF-8">
    <title>Test Save Event API</title>
</head>
<body>
    <h2>Δοκιμή Καταγραφής Συμβάντος (R2)</h2>
    <!-- Προσοχή στο action: Βεβαιώσου ότι η διαδρομή προς το save_event.php είναι σωστή -->
    <form action="../api/saveEvent.php" method="POST">
        
        <label>ID Αγώνα (match_id):</label><br>
        <input type="number" name="match_id" required><br><br>

        <label>ID Παίκτη (player_id):</label><br>
        <input type="number" name="player_id" required><br><br>

        <label>Τύπος Συμβάντος (event_type):</label><br>
        <select name="event_type">
            <option value="shot">Shot (Γκολ/Σουτ)</option>
            <option value="foul_won">Foul Won (Υπέρ)</option>
            <option value="card_yellow">Card Yellow (Κατά)</option>
            <option value="card_won_yellow">Card Won Yellow (Υπέρ)</option>
        </select><br><br>

        <label>Αποτέλεσμα (outcome):</label><br>
        <select name="outcome">
            <option value="goal">Goal</option>
            <option value="success">Success</option>
            <option value="off_target">Off Target</option>
        </select><br><br>

        <label>Λεπτό (event_minute):</label><br>
        <input type="number" name="event_minute" value="10"><br><br>

        <button type="submit">Αποστολή Event</button>
    </form>
</body>
</html>