<?php
header('Content-Type: text/html; charset=utf-8');

function random_lines($filename, $numlines)
{
    if (!file_exists($filename) || !is_readable($filename)) {
        return null;
    }

    $filesize = filesize($filename);
    $lines    = [];
    $n        = 0;

    $handle = fopen($filename, 'r');

    if ($handle) {
        while ($n < $numlines) {
            fseek($handle, rand(0, $filesize));

            $started = false;
            $gotline = false;
            $line    = '';

            while (!$gotline) {
                if (false === ($char = fgetc($handle))) {
                    break;
                } else if ($char == "\n" || $char == "\r") {
                    break;
                }
            }

            $n++;
            array_push($lines, fgetcsv($handle));
        }//end while

        fclose($handle);
    }//end if

    return $lines;

}//end random_lines()


// Example usage
$lines     = random_lines('booklist.csv', 100);
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
