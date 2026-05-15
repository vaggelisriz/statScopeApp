<?php require_once '../config/dbConnect.php'; ?>
<!DOCTYPE html>
<html lang="el">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>StatScope - Δημιουργία Πρωταθλήματος</title>
    <link rel="stylesheet" href="styleWebApp.css">
    <style>
        .admin-card {
            background: rgba(255, 255, 255, 0.03);
            border: 1px solid rgba(255, 255, 255, 0.1);
            border-radius: 12px;
            padding: 30px;
            text-align: left;
        }

        .form-group {
            margin-bottom: 25px;
            display: flex;
            flex-direction: column;
        }

        label {
            color: #00E676;
            font-size: 12px;
            font-weight: bold;
            letter-spacing: 1px;
            margin-bottom: 10px;
            text-transform: uppercase;
        }

        input[type="text"] {
            width: 100%;
            background: rgba(255, 255, 255, 0.05);
            border: 1px solid rgba(255, 255, 255, 0.2);
            border-radius: 6px;
            padding: 14px;
            color: white;
            font-size: 16px;
            box-sizing: border-box;
        }

        input[type="text"]:focus {
            outline: none;
            border-color: #00E676;
            background: rgba(0, 230, 118, 0.05);
        }

        /* Styling για τη λίστα των ομάδων */
        .team-list {
            background: rgba(0, 0, 0, 0.2);
            border: 1px solid rgba(255, 255, 255, 0.1);
            border-radius: 8px;
            padding: 15px;
            max-height: 250px;
            overflow-y: auto;
            margin-top: 5px;
        }

        /* Custom Scrollbar για την ομάδα */
        .team-list::-webkit-scrollbar {
            width: 6px;
        }
        .team-list::-webkit-scrollbar-thumb {
            background: #00E676;
            border-radius: 10px;
        }
        .team-list::-webkit-scrollbar-track {
            background: rgba(255, 255, 255, 0.05);
        }

        .team-item {
            display: flex;
            align-items: center;
            padding: 10px 5px;
            border-bottom: 1px solid rgba(255, 255, 255, 0.05);
            color: #D3D3D3;
            font-size: 15px;
            cursor: pointer;
            transition: 0.2s;
        }

        .team-item:last-child { border-bottom: none; }
        
        .team-item:hover {
            background: rgba(0, 230, 118, 0.05);
            color: white;
        }

        .team-item input[type="checkbox"] {
            margin-right: 15px;
            accent-color: #00E676; /* Το χρώμα του checkbox στα μοντέρνα browsers */
            width: 18px;
            height: 18px;
            cursor: pointer;
        }

        .footer-info {
            text-align: center;
            margin-top: 30px;
        }
        
        .back-link {
            color: #D3D3D3;
            text-decoration: none;
            font-size: 14px;
            opacity: 0.7;
            transition: 0.3s;
        }
        .back-link:hover { color: #00E676; opacity: 1; }
    </style>
</head>
<body>

<div class="admin-container">
    <div class="logo-section" style="text-align: center; margin-bottom: 40px;">
        <div class="logo-main" style="font-size: 32px; letter-spacing: 5px;">STAT<span>SCOPE</span></div>
        <p style="color: #D3D3D3; opacity: 0.6; font-size: 11px; margin-top: 10px; text-transform: uppercase;">Championship Initialization</p>
    </div>

    <div class="admin-card">
        <form action="generateFixtures.php" method="POST">
            
            <div class="form-group">
                <label>Championship Name</label>
                <input type="text" name="championship_name" placeholder="i.e. Super League 2024" required>
            </div>

            <div class="form-group">
                <label>Contestants (Select an even number of teams)</label>
                <div class="team-list">
                    <?php
                    // Φέρνουμε όλες τις διαθέσιμες ομάδες από τη βάση
                    $stmt = $pdo->query("SELECT id, name FROM teams ORDER BY name ASC");
                    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
                        echo "<label class='team-item'>";
                        echo "<input type='checkbox' name='selected_teams[]' value='{$row['id']}'>";
                        echo htmlspecialchars($row['name']);
                        echo "</label>";
                    }
                    ?>
                </div>
            </div>

            <button type="submit" class="stat-button" style="background-color: #ffffff;">Create Championship</button>
        </form>
    </div>

    <div class="footer-info">
        <a href="admin.php" class="back-link">← Back to Dashboard</a>
    </div>

    <div class="footer-text" style="margin-top: 40px;">
        StatScope Tournament Engine • v1.2
    </div>
</div>

</body>
</html>