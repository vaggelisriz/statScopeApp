<?php
require_once '../config/dbConnect.php';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $champName = $_POST['championship_name'] ?? '';
    // Παίρνουμε τους κωδικούς των ομάδων που επιλέχθηκαν
    $teams = $_POST['selected_teams'] ?? []; 

    // Βασικοί έλεγχοι
    if (empty($champName)) {
        die("Σφάλμα: Το όνομα είναι υποχρεωτικό.");
    }
    if (count($teams) < 2 || count($teams) % 2 != 0) {
        die("Σφάλμα: Πρέπει να επιλέξετε ζυγό αριθμό ομάδων (τουλάχιστον 2). Επιλέξατε: " . count($teams));
    }

    try {
        $pdo->beginTransaction();

        // 1. Δημιουργία Πρωταθλήματος
        $stmtChamp = $pdo->prepare("INSERT INTO championships (name) VALUES (?)");
        $stmtChamp->execute([$champName]);
        $championshipId = $pdo->lastInsertId();

        $numTeams = count($teams);
        $rounds = $numTeams - 1;
        $matchesPerRound = $numTeams / 2;

        // Ανακάτεμα ομάδων για να μην είναι πάντα η ίδια σειρά στην κλήρωση
        shuffle($teams);

        for ($i = 0; $i < $rounds; $i++) {
            for ($j = 0; $j < $matchesPerRound; $j++) {
                $home = $teams[$j];
                $away = $teams[$numTeams - 1 - $j];

                $sql = "INSERT INTO matches (championship_id, home_team_id, away_team_id, match_round) VALUES (?, ?, ?, ?)";
                $stmt = $pdo->prepare($sql);
                $stmt->execute([$championshipId, $home, $away, $i + 1]);
            }

            // Αλγόριθμος περιστροφής
            $fixed = array_shift($teams);
            $last = array_pop($teams);
            array_unshift($teams, $last);
            array_unshift($teams, $fixed);
        }

        $pdo->commit();
        echo "<h2>Επιτυχία!</h2> Το πρωτάθλημα δημιουργήθηκε με τις ομάδες που επιλέξατε.";
        echo "<br><a href='createChampionship.php'>Επιστροφή</a>";

    } catch (Exception $e) {
        $pdo->rollBack();
        die("Σφάλμα: " . $e->getMessage());
    }
}