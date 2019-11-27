# RSVLP
## 簡易贓車辨識App

### 操作
```
1. 將 car 資料夾放入 server 中。

2. 匯入 final_Net.m，執行 Web_object_classification_car.m。

3. 執行 RSVLP 專案之 App 檔。
```

### 檔案
```
1. final (Matlab 專案檔)
   * final_Net.mat：辨識 Model。
   * pixresized_img_0.m：修改訓練集圖片大小。
   * TransferLearningVideoJ1_final.m：建構 Model。
   * Web_object_classification_car.m：執行 Model 進行車牌影像辨識。
   * number.txt：辨識結果文字檔(測試用)。
   * 5555-DP.jpg、7339-k.jpg、JSH-777.jpg：測試圖片。
   
2. img：訓練圖片檔，包含 A-Z、0-9、-。

3. RSVLP (Android 專案檔)

4. car (PHP)
   * UploadToServer.php：上傳 App 圖片到 uploads。
   * read.php：讀取辨識結果文字檔。
   * number.txt：辨識結果文字檔。
   * uploads 資料夾 → car.jpg：預備辨識之車牌影像。
```

<img width="70%" height="70%" src="https://github.com/a10423006/RSVLP/blob/master/img/%E5%9C%96%E7%89%87%201.png">