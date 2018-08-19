<?php

// https://www.leaseweb.com/labs/2015/10/creating-a-simple-rest-api-in-php/
// get the HTTP method, path and body of the request
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

if (isset($input['username']) && isset($input['password']) && isset($input['email'])) {
    if (mb_strlen($input['username']) <= 3 || mb_strlen($input['username']) > 20) {
        echo json_encode(['error' => 'Invalid username']);
        exit;
    }

    if (mb_strlen($input['password']) <= 6 && mb_strlen($input['password']) > 200) {
        echo json_encode(['error' => 'Invalid password']);
        exit;
    }

    if (filter_var($input['email'], FILTER_VALIDATE_EMAIL) === false) {
        echo json_encode(['error' => 'Invalid email']);
        exit;
    }
} else {
    echo json_encode(['error' => 'Invalid params']);
    exit;
}//end if

// connect to the mysql database
$conn = mysqli_connect('localhost', 'r1234', 'myPass', 'mccProj');
mysqli_set_charset($conn, 'utf8');

$prep = mysqli_prepare($conn, 'INSERT INTO oauth_clients (client_id, client_secret, redirect_uri) VALUES (?,?, "https://kaustubhk.com/")');
mysqli_stmt_bind_param($prep, 'ss', $input['username'], $input['password']);
mysqli_execute($prep);
mysqli_close($conn);
echo json_encode(['success' => true]);
exit;
