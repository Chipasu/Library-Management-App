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

function get_books($filename, $bookids)
{
    if (!file_exists($filename) || !is_readable($filename)) {
        return null;
    }

    $filesize = filesize($filename);
    $lines    = [];
    $n        = 0;

    $handle = fopen($filename, 'r');

    if ($handle) {
        while ($row = fgetcsv($handle)) {
            if (in_array($row[0], $bookids)) {
                array_push($lines, $row);
            }
        }

        fclose($handle);
    }//end if

    return $lines;

}//end get_books()

$conn = mysqli_connect('localhost', 'r1234', 'myPass', 'mccProj');
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

$strec0  = mysqli_query($conn, "SELECT * FROM booking WHERE client_id='$client_id'");
$bookids = [];
while ($row = mysqli_fetch_assoc($strec0)) {
    array_push($bookids, $row['bookid']);
}

$lines     = get_books('booklist.csv', $bookids);
$jsonArray = [];
foreach ($lines as $row) {
    $jsonObject = [
                   'bookid' => $row[0],
                   'name'   => $row[3],
                   'author' => $row[4],
                   'genre'  => $row[6],
                   'image'  => $row[6].'/'.$row[1],
                  ];
    array_push($jsonArray, $jsonObject);
}

echo json_encode($jsonArray);
