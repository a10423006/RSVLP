<?php
    $file = fopen("number.txt", "r");
    echo fgets($file);
    fclose($file);
?>