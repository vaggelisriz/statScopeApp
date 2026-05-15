<!DOCTYPE html>
<html lang="el">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>StatScope - Προσθήκη Ομάδας</title>
    <link rel="stylesheet" href="styleWebApp.css">
    <style>
        /* Διορθώσεις για τη στοίχιση */
        .admin-card {
            background: rgba(255, 255, 255, 0.03);
            border: 1px solid rgba(255, 255, 255, 0.1);
            border-radius: 12px;
            padding: 30px;
            text-align: left; /* Στοίχιση κειμένου αριστερά μέσα στην κάρτα */
        }

        .form-group {
            margin-bottom: 20px; /* Απόσταση μεταξύ των πεδίων */
            display: flex;
            flex-direction: column; /* Αναγκάζει label και input να είναι σε στήλη */
        }

        label {
            color: #00E676; /* Neon Green για τα labels */
            font-size: 12px;
            font-weight: bold;
            letter-spacing: 1px;
            margin-bottom: 8px;
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
            box-sizing: border-box; /* Για να μην ξεφεύγει το πλάτος */
        }

        input[type="text"]:focus {
            outline: none;
            border-color: #00E676;
            background: rgba(0, 230, 118, 0.05);
        }

        /* Custom style για το κουμπί επιλογής αρχείου */
        input[type="file"] {
            color: #D3D3D3;
            font-size: 14px;
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
        <p style="color: #D3D3D3; opacity: 0.6; font-size: 12px; margin-top: 10px;">Create New Team</p>
    </div>

    <?php if (isset($_GET['status']) && $_GET['status'] == 'success'): ?>
        <div class="success-msg">✓ Team created successfully!</div>
    <?php endif; ?>

    <div class="admin-card">
        <form action="saveTeam.php" method="POST" enctype="multipart/form-data">
            
            <div class="form-group">
                <label>Team Name</label>
                <input type="text" name="team_name" placeholder="i.e. PAOK" required>
            </div>

            <div class="form-group">
                <label>Hometown</label>
                <input type="text" name="city" placeholder="i.e. Thessaloniki" required>
            </div>

            <div class="form-group">
                <label>Team Logo (image)</label>
                <input type="file" name="logo" accept="image/*" required>
            </div>

            <button type="submit" class="stat-button" style="margin-top: 10px;">Submit new team</button>
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