<?php
require_once '../config/dbConnect.php';

// Φέρνουμε όλες τις ομάδες για το dropdown
$stmt = $pdo->query("SELECT id, name FROM teams ORDER BY name ASC");
$teams = $stmt->fetchAll();
?>

<!DOCTYPE html>
<html lang="el">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>StatScope - Προσθήκη Παίκτη</title>
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
            margin-bottom: 20px;
            display: flex;
            flex-direction: column;
        }

        label {
            color: #00E676;
            font-size: 12px;
            font-weight: bold;
            letter-spacing: 1px;
            margin-bottom: 8px;
            text-transform: uppercase;
        }

        input[type="text"], select {
            width: 100%;
            background: rgba(255, 255, 255, 0.05);
            border: 1px solid rgba(255, 255, 255, 0.2);
            border-radius: 6px;
            padding: 14px;
            color: white;
            font-size: 16px;
            box-sizing: border-box;
            appearance: none; /* Αφαιρεί το default βελάκι στα dropdowns για custom look */
        }

        /* Επαναφορά βέλους για τα select με custom icon (προαιρετικό) */
        select {
            width: 100%;
            background: rgba(255, 255, 255, 0.05);
            border: 1px solid rgba(255, 255, 255, 0.2);
            border-radius: 6px;
            padding: 14px;
            color: white; /* Χρώμα κειμένου όταν είναι κλειστό */
            font-size: 16px;
            appearance: none;
            cursor: pointer;
        }

        /* Η ΚΡΙΣΙΜΗ ΔΙΟΡΘΩΣΗ: Styling για τις επιλογές (Options) */
        select option {
            background-color: #01051a; /* Το σκούρο μπλε της εφαρμογής σας */
            color: white;              /* Λευκά γράμματα για να φαίνονται πάντα */
            padding: 10px;
        }

        /* Focus state */
        select:focus {
            outline: none;
            border-color: #00E676;
            background-color: rgba(0, 230, 118, 0.05);
        }

        input:focus{
            outline: none;
            border-color: #00E676;
            background: rgba(0, 230, 118, 0.05);
        }

        input[type="file"] {
            color: #D3D3D3;
            font-size: 14px;
            padding: 10px 0;
        }

        .success-msg {
            background: rgba(0, 230, 118, 0.15);
            color: #00E676;
            padding: 12px;
            border-radius: 6px;
            margin-bottom: 20px;
            text-align: center;
            border: 1px solid #00E676;
        }
    </style>
</head>
<body>

<div class="admin-container">
    <div class="logo-section" style="text-align: center; margin-bottom: 40px;">
        <div class="logo-main" style="font-size: 32px; letter-spacing: 5px;">STAT<span>SCOPE</span></div>
        <p style="color: #D3D3D3; opacity: 0.6; font-size: 12px; margin-top: 10px;">Create New Player</p>
    </div>

    <?php if (isset($_GET['status']) && $_GET['status'] == 'success'): ?>
        <div class="success-msg">✓ Player added successfully!</div>
    <?php endif; ?>

    <div class="admin-card">
        <form action="savePlayer.php" method="POST" enctype="multipart/form-data">
            
            <div class="form-group">
                <label>Name</label>
                <input type="text" name="player_name" placeholder="i.e. Giannis Konstantelias" required>
            </div>

            <div class="form-group">
                <label>Position</label>
                <select name="position" required>
                    <option value="" disabled selected>-- Select Position --</option>
                    <option value="Τερματοφύλακας">Goalkeeper</option>
                    <option value="Αμυντικός">Defender</option>
                    <option value="Μέσος">Midfielder</option>
                    <option value="Επιθετικός">Forward</option>
                </select>
            </div>

            <div class="form-group">
                <label>Team</label>
                <select name="team_id" required>
                    <option value="" disabled selected>-- Select Team --</option>
                    <?php foreach ($teams as $team): ?>
                        <option value="<?= $team['id'] ?>"><?= htmlspecialchars($team['name']) ?></option>
                    <?php endforeach; ?>
                </select>
            </div>

            <div class="form-group">
                <label>Player Photo</label>
                <input type="file" name="photo" accept="image/*" required>
            </div>

            <button type="submit" class="stat-button" style="margin-top: 10px;">Submit new player</button>
        </form>
    </div>

    <div style="text-align: center; margin-top: 30px;">
        <a href="admin.php" style="color: #D3D3D3; text-decoration: none; font-size: 14px; opacity: 0.7;">
            ← Back to Dashboard
        </a>
    </div>
</div>

</body>
</html>