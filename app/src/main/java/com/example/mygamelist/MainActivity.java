package com.example.mygamelist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;
import android.widget.Button;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private Uri cameraUri;

    private final ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (cameraUri != null && isExternalStorageReadable()) {
                        imageView.setImageURI(cameraUri);
                    } else {
                        Log.d("debug", "cameraUri == null");
                    }
                }
            });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        imageView = findViewById(R.id.image_view);
        imageView.setImageResource(R.drawable.back);

        Button cameraButton = findViewById(R.id.camera_button);
        // lambda式
        cameraButton.setOnClickListener(v -> {
            if (isExternalStorageWritable()) {
                cameraIntent();
            }
        });
    }


    private void cameraIntent() {
        Context context = getApplicationContext();
        // 保存先のフォルダー
        File cFolder = context.getExternalFilesDir(Environment.DIRECTORY_DCIM);
        Log.d("log", "path: " + cFolder);

        String fileDate = new SimpleDateFormat(
                "ddHHmmss", Locale.JAPAN).format(new Date());
        // ファイル名
        String fileName = String.format("CameraIntent_%s.jpg", fileDate);

        File cameraFile = new File(cFolder, fileName);

        try {
            cameraUri = FileProvider.getUriForFile(
                    MainActivity.this,
                    context.getPackageName() + ".fileprovider",
                    cameraFile);
        } catch (Exception ex) {
            Log.e("error", ex.getMessage(), ex);
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);

        resultLauncher.launch(intent);

        Log.d("debug", "startActivityForResult()");
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state));
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }

}