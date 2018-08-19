<?php
$conn = mysqli_connect('localhost', 'r1234', 'myPass', 'mccProj');


$filename = 'booklist.csv';
$handle   = fopen($filename, 'r');

while ($row = fgetcsv($handle)) {
    $random = rand(0,2);
    $strec1 = mysqli_query($conn, "INSERT INTO books(bookid,available) VALUES('{$row[0]}',$random)");
}