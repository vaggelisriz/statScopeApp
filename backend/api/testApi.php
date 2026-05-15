<?php
// 1. Ρυθμίσεις
$apiKey = "75611104dc4a3f12754a4c4d0e6f929e"; // Αντικατάστησε με το κλειδί σου
$url = "https://v3.football.api-sports.io/players/squads?team=33";

// 2. Εκτέλεση με cURL
$curl = curl_init();

curl_setopt_array($curl, [
    CURLOPT_URL => $url,
    CURLOPT_RETURNTRANSFER => true,
    CURLOPT_HTTPHEADER => [
        "x-apisports-key: " . $apiKey
    ],
]);

$response = curl_exec($curl);
$err = curl_error($curl);
curl_close($curl);

// 3. Εμφάνιση αποτελέσματος
if ($err) {
    echo "Σφάλμα: " . $err;
} else {
    // Μετατρέπουμε το response σε αντικείμενο για να το ξανα-τυπώσουμε "όμορφα"
    $data = json_decode($response);
    
    header('Content-Type: application/json; charset=utf-8');
    echo json_encode($data, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
}
?>