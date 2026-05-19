<?php
require_once '../config/dbConnect.php';
$stmt = $pdo->query("SELECT id, name FROM championships ORDER BY id DESC");
$championships = $stmt->fetchAll(PDO::FETCH_ASSOC);

header('Content-Type: application/json; charset=utf-8');
echo json_encode($championships, JSON_UNESCAPED_UNICODE);