<?php
header('Content-Type: text/html; charset=utf-8');


// Example usage
$method = $_SERVER['REQUEST_METHOD'];
// $request = explode('/', trim($_SERVER['PATH_INFO'], '/'));
if ($method === 'POST') {
    $input = json_decode(file_get_contents('php://input'), true);
    if ($input === null) {
        echo json_encode(['error' => 'Invalid json']);
        exit;
    }
} else {
    echo json_encode(['error' => 'Invalid post']);
    exit;
}

$conn = mysqli_connect('localhost', 'r1234', 'myPass', 'mccProj');
// $clientDetails = $server->getResourceController()->getToken();
// $client_id     = $clientDetails['client_id'];
if (!isset($input['access_token'])) {
    echo json_encode(['error' => 'Invalid token']);
    exit;
}

$strec1 = mysqli_query($conn, "SELECT * FROM oauth_access_tokens WHERE access_token='{$input['access_token']}'");
if (mysqli_num_rows($strec1) == 0) {
    echo json_encode(['error' => 'Invalid token']);
    exit;
}

$strow1    = mysqli_fetch_assoc($strec1);
$client_id = $strow1['client_id'];
$strec0    = mysqli_query($conn, "SELECT * FROM booking WHERE client_id='$client_id'");
if (mysqli_num_rows($strec0) >= 4) {
    echo json_encode(['error' => 'Booking Limit reached']);
    exit;
}

$bookid    = $input['bookid'];
$strec2    = mysqli_query($conn, "SELECT * FROM books WHERE bookid='$bookid'");
$strow2    = mysqli_fetch_assoc($strec2);
$available = $strow2['available'];
if (intval($available) > 0) {
    mysqli_query($conn, "UPDATE books SET available=available-1 WHERE bookid='$bookid'");
    mysqli_query($conn, "INSERT INTO booking(bookid,client_id) VALUES('$bookid','$client_id')");
    echo json_encode(['success' => 'true']);
}
