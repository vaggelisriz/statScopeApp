<?php
// getStandings.php
require_once '../config/dbConnect.php'; 
header('Content-Type: application/json; charset=utf-8');

$championship_id = isset($_GET['championship_id']) ? intval($_GET['championship_id']) : null;

if (!$championship_id) {
    echo json_encode(["status" => "error", "message" => "Missing championship_id"]);
    exit;
}

try {
    $sql = "SELECT 
                t.id AS team_id,
                t.name AS team_name,
                t.logo AS team_logo,
                t.city AS team_city,
                SUM(CASE WHEN m.status = 'completed' THEN 1 ELSE 0 END) AS matches_played,
                SUM(CASE 
                    WHEN m.status = 'completed' AND ((m.home_team_id = t.id AND m.home_score > m.away_score) OR 
                         (m.away_team_id = t.id AND m.away_score > m.home_score)) THEN 1 
                    ELSE 0 
                END) AS wins,
                SUM(CASE 
                    WHEN m.status = 'completed' AND m.home_score = m.away_score THEN 1 
                    ELSE 0 
                END) AS draws,
                SUM(CASE 
                    WHEN m.status = 'completed' AND ((m.home_team_id = t.id AND m.home_score < m.away_score) OR 
                         (m.away_team_id = t.id AND m.away_score < m.home_score)) THEN 1 
                    ELSE 0 
                END) AS losses,
                SUM(CASE WHEN m.status = 'completed' THEN (CASE WHEN m.home_team_id = t.id THEN m.home_score ELSE m.away_score END) ELSE 0 END) AS goals_scored,
                SUM(CASE WHEN m.status = 'completed' THEN (CASE WHEN m.home_team_id = t.id THEN m.away_score ELSE m.home_score END) ELSE 0 END) AS goals_conceded,
                SUM(CASE 
                    WHEN m.status = 'completed' AND ((m.home_team_id = t.id AND m.home_score > m.away_score) OR (m.away_team_id = t.id AND m.away_score > m.home_score)) THEN 3
                    WHEN m.status = 'completed' AND m.home_score = m.away_score THEN 1
                    ELSE 0 
                END) AS points
            FROM (
                SELECT id, name, logo, city FROM teams WHERE id IN (
                    SELECT home_team_id FROM matches WHERE championship_id = ?
                    UNION
                    SELECT away_team_id FROM matches WHERE championship_id = ?
                )
            ) t
            LEFT JOIN matches m ON (t.id = m.home_team_id OR t.id = m.away_team_id) 
                                AND m.championship_id = ?
                                AND m.status = 'completed'
            GROUP BY t.id
            ORDER BY 
                points DESC, 
                (SUM(CASE WHEN m.status = 'completed' THEN (CASE WHEN m.home_team_id = t.id THEN m.home_score ELSE m.away_score END) ELSE 0 END) - 
                 SUM(CASE WHEN m.status = 'completed' THEN (CASE WHEN m.home_team_id = t.id THEN m.away_score ELSE m.home_score END) ELSE 0 END)) DESC, 
                team_name ASC";

    $stmt = $pdo->prepare($sql);
    $stmt->execute([$championship_id, $championship_id, $championship_id]);
    $standings = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // Επιστροφή των αποτελεσμάτων σε καθαρό JSON Array
    echo json_encode($standings, JSON_UNESCAPED_UNICODE | JSON_NUMERIC_CHECK);

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(["status" => "error", "message" => "Database Error: " . $e->getMessage()]);
}
?>