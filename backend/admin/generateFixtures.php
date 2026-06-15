<?php
require_once '../config/dbConnect.php';

// Ξεκινάμε το HTML Buffer για να εφαρμόσουμε το Theme
?>
<!DOCTYPE html>
<html lang="el">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>StatScope - Draw Result</title>
    <link rel="stylesheet" href="styleWebApp.css">
    <style>
        .result-card {
            background: rgba(255, 255, 255, 0.03);
            border: 1px solid rgba(255, 255, 255, 0.1);
            border-radius: 12px;
            padding: 40px 20px;
            text-align: center;
        }
        .status-icon {
            font-size: 50px;
            margin-bottom: 20px;
        }
        .status-success { color: #00E676; }
        .status-error { color: #ff5252; }
        
        .message-title {
            font-size: 20px;
            font-weight: bold;
            margin-bottom: 10px;
            text-transform: uppercase;
        }
        .message-body {
            color: #D3D3D3;
            font-size: 15px;
            margin-bottom: 30px;
            line-height: 1.6;
        }
        .error-box {
            background: rgba(255, 82, 82, 0.1);
            border: 1px solid #ff5252;
            color: #ff5252;
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 25px;
            font-size: 14px;
        }
    </style>
</head>
<body>

<div class="admin-container">
    <div class="logo-section" style="text-align: center; margin-bottom: 40px;">
        <div class="logo-main">STAT<span>SCOPE</span></div>
    </div>

    <div class="result-card">
        <?php
        if ($_SERVER['REQUEST_METHOD'] === 'POST') {
            $champName = $_POST['championship_name'] ?? '';
            $teams = $_POST['selected_teams'] ?? []; 

            // Βασικοί έλεγχοι (Αφαιρέθηκε ο περιορισμός του % 2 != 0, γιατί πλέον το διαχειριζόμαστε με ρεπό αν χρειαστεί)
            if (empty($champName) || count($teams) < 2) {
                echo '<div class="status-icon status-error">⚠</div>';
                echo '<div class="message-title status-error">Error</div>';
                echo '<div class="message-body">';
                if (empty($champName)) echo "Championship name is required.<br>";
                if (count($teams) < 2) echo "A single Championship requires a minimum number of 2 teams.<br>";
                echo '</div>';
            } else {
                try {
                    $pdo->beginTransaction();

                    // 1. Δημιουργία Πρωταθλήματος
                    $stmtChamp = $pdo->prepare("INSERT INTO championships (name) VALUES (?)");
                    $stmtChamp->execute([$champName]);
                    $championshipId = $pdo->lastInsertId();

                    // Αν ο αριθμός των ομάδων είναι περιττός, βάζουμε μια εικονική ομάδα 0 (Ρεπό)
                    if (count($teams) % 2 != 0) {
                        $teams[] = 0;
                    }

                    $numTeams = count($teams);
                    $roundsSingle = $numTeams - 1; // Οι αγωνιστικές του 1ου γύρου
                    $matchesPerRound = $numTeams / 2;

                    shuffle($teams);

                    // Αλγόριθμος Round-Robin για 1ο και 2ο γύρο παράλληλα
                    for ($i = 0; $i < $roundsSingle; $i++) {
                        for ($j = 0; $j < $matchesPerRound; $j++) {
                            $home = $teams[$j];
                            $away = $teams[$numTeams - 1 - $j];

                            // Αν κάποια ομάδα παίζει με το ρεπό (ID 0), δεν γράφουμε τον αγώνα στη βάση
                            if ($home != 0 && $away != 0) {
                                
                                // 🏠 1ος Γύρος: Κανονική έδρα (Αγωνιστικές 1 έως X)
                                $sql1 = "INSERT INTO matches (championship_id, home_team_id, away_team_id, match_round) VALUES (?, ?, ?, ?)";
                                $stmt1 = $pdo->prepare($sql1);
                                $stmt1->execute([$championshipId, $home, $away, $i + 1]);

                                // ✈️ 2ος Γύρος: Αντίστροφη έδρα (Αγωνιστικές X+1 έως 2X)
                                $sql2 = "INSERT INTO matches (championship_id, home_team_id, away_team_id, match_round) VALUES (?, ?, ?, ?)";
                                $stmt2 = $pdo->prepare($sql2);
                                $stmt2->execute([$championshipId, $away, $home, $i + 1 + $roundsSingle]);
                            }
                        }

                        // Κυκλικό rotation των ομάδων (κρατώντας την πρώτη σταθερή)
                        $fixed = array_shift($teams);
                        $last = array_pop($teams);
                        array_unshift($teams, $last);
                        array_unshift($teams, $fixed);
                    }

                    $pdo->commit();

                    // Εμφάνιση Επιτυχίας
                    echo '<div class="status-icon status-success">✓</div>';
                    echo '<div class="message-title status-success">Success!</div>';
                    echo '<div class="message-body">';
                    echo "Championship '<b>" . htmlspecialchars($champName) . "</b>' created.<br>";
                    echo "Generated " . ($roundsSingle * 2) . " matchdays (Home & Away matches included).";
                    echo '</div>';

                } catch (Exception $e) {
                    $pdo->rollBack();
                    echo '<div class="status-icon status-error">✕</div>';
                    echo '<div class="message-title status-error">Database Error</div>';
                    echo '<div class="error-box">' . $e->getMessage() . '</div>';
                }
            }
        } else {
            echo '<div class="message-body">Not acceptable request.</div>';
        }
        ?>

        <a href="createChampionship.php" class="stat-button">New Championhsip</a>
        <a href="admin.php" style="display:block; margin-top:20px; color:#D3D3D3; text-decoration:none; font-size:13px; opacity:0.7;">Back to Dashboard</a>
    </div>

    <div class="footer-text" style="margin-top: 40px;">
        StatScope Tournament Engine • Finalized
    </div>
</div>

</body>
</html>