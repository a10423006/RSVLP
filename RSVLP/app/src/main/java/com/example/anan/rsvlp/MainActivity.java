package com.example.anan.rsvlp;

import java.io.FileNotFoundException;
import java.net.URL;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class MainActivity extends AppCompatActivity implements OnClickListener {
    private ImageButton uploadButton, btnselectpic, btncamera, btnopendata;
    private ImageView imageview;
    private EditText editText;
    private String imagepath = null;
    private FileUpload mFileUpload;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(android.os.Build.VERSION.SDK_INT > 9) { //強制網路執行，避免掛點
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //固定螢幕直向、不隨手機旋轉
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        //註冊
        uploadButton = (ImageButton) findViewById(R.id.upload_button);
        btncamera = (ImageButton) findViewById(R.id.selectpic_button2);
        btnselectpic = (ImageButton)findViewById(R.id.selectpic_button);
        imageview = (ImageView)findViewById(R.id.imageView_pic);
        btnopendata = (ImageButton) findViewById(R.id.opendata_button);
        editText = (EditText)findViewById(R.id.editText);

        //監聽事件
        btnselectpic.setOnClickListener(this);
        uploadButton.setOnClickListener(this);
        btnopendata.setOnClickListener(this);
        btncamera.setOnClickListener(this);

        imagepath = Integer.toString(R.drawable.up);
    }

    @Override
    public void onClick(View arg0) {
        if(arg0 == btnselectpic) { //相簿選擇
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 1);

        }else if(arg0 == btncamera){ //相機
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivityForResult(intent, 2);

        } else if (arg0 == uploadButton) { //上傳辨識
            Toast.makeText(MainActivity.this, "uploading started.....", Toast.LENGTH_SHORT).show();

            //辨識
            new Thread(new Runnable() {
                public void run() {
                    mFileUpload = new FileUpload();
                    mFileUpload.setOnFileUploadListener(new FileUpload.OnFileUploadListener() {
                        @Override
                        public void onFileUploadSuccess(final String msg) { //成功
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this ,msg, Toast.LENGTH_SHORT).show();
                                    getResult();
                                }
                            });
                        }

                        @Override
                        public void onFileUploadFail(final String msg) { //失敗
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this ,msg, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                    mFileUpload.doFileUpload(imagepath);
                }
            }).start();

        }else if(arg0 == btnopendata){ //車牌查詢
            if(!editText.getText().equals("")) {
                new Thread(new Runnable() {
                    public void run() {
                        getSearchResult();
                    }
                }).start();
            }else{
                Toast.makeText(MainActivity.this, "格式錯誤！", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) { //相簿
            Uri selectedImageUri = data.getData();
            ContentResolver cr = this.getContentResolver();
            try {
                //讀取照片，型態為Bitmap
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(selectedImageUri));
                imageview.setImageBitmap(bitmap);
                imagepath = getImageAbsolutePath(this,selectedImageUri);
                Toast.makeText(MainActivity.this,"Uploading file path:" +imagepath, Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                Log.i("FileNotFoundException", "Exception : "  + e.getMessage(), e);
            }

        }else if (requestCode == 2 && resultCode == RESULT_OK) { //相機
            Bundle extrax = data.getExtras();
            Bitmap bmp = (Bitmap) extrax.get("data");
            imageview.setImageBitmap(bmp);
            imagepath = getImageAbsolutePath(this, data.getData());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //回傳辨識完畢的車牌
    public void getResult(){
        try {
            String upLoadUri = "http://172.20.10.2/car/read.php";
            URL num_url = new URL(upLoadUri);
            Document doc =  Jsoup.parse(num_url, 3000); //讀取
            editText.setText(doc.text());
        } catch (Exception e) {
            Log.i("Exception", "Exception : "  + e.getMessage(), e);
        }
    }

    //回傳查詢結果
    public void getSearchResult(){
        try {
            String upLoadUri = "https://od.moi.gov.tw/adm/veh/query_veh?vehNumber=" + editText.getText();
            Uri num_url = Uri.parse(upLoadUri);
            Intent it = new Intent(Intent.ACTION_VIEW, num_url); //前往瀏覽器
            this.startActivity(it);
        } catch (Exception e) {
            Log.i("Exception", "Exception : "  + e.getMessage(), e);
        }
    }

    //內建圖片位置抓取
    public static String getImageAbsolutePath(Activity context, Uri imageUri) {
        if (context == null || imageUri == null)
            return null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, imageUri)) {
            if (isExternalStorageDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(imageUri)) {
                String id = DocumentsContract.getDocumentId(imageUri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[] { split[1] };
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } // MediaStore (and general)
        else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(imageUri))
                return imageUri.getLastPathSegment();
            return getDataColumn(context, imageUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
            return imageUri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = { column };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
