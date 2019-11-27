<!--測試用按鈕-->
<html>
    <body>
        <form action="UploadToServer.php" method="post" enctype="multipart/form-data" >
            圖片：<input type="file" name="uploaded_file"> <input type="submit" name="Button" value="上傳圖片">
        </form>
    </body>
</html>

<?php
   $file_path = "uploads/";
   $file_path = $file_path.basename( $_FILES['uploaded_file']['name']);

   if(move_uploaded_file($_FILES['uploaded_file']['tmp_name'], $file_path)) {
        chmod($file_path, 0777); //修改權限始可讀寫存
        rename($file_path, "uploads/car.jpg"); //統一檔名
       echo "success";
   } else{
       echo "fail";
   }
?>