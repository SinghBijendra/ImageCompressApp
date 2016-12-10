package com.bijendra.compress.image;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bijendra.compress.image.misc.Compressor;
import com.bijendra.compress.image.misc.FileUtil;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Random;


public class MainActivity extends AppCompatActivity {
    private static final int RC_IMAGE = 1;

    private ImageView mIvActual;
    private ImageView mIvCompressed;
    private TextView mTvActualSize;
    private TextView mTvCompressedSize;

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mIvActual = (ImageView) findViewById(R.id.ivActual);
        mIvCompressed = (ImageView) findViewById(R.id.ivCompressed);
        mTvActualSize = (TextView) findViewById(R.id.tvActualSize);
        mTvCompressedSize = (TextView) findViewById(R.id.tvCompressedSize);
        clearImage();
    }


    public void gotoOperation(View view)
    {
        if(view.getTag().toString().equalsIgnoreCase("0"))
        {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, RC_IMAGE);
        }

    }

    private void setCompressedImage(File compressedImage) {
        mIvCompressed.setImageBitmap(BitmapFactory.decodeFile(compressedImage.getAbsolutePath()));
        mTvCompressedSize.setText(String.format("Size : %s", getReadableFileSize(compressedImage.length())));

        Toast.makeText(this, "Compressed image save in " + compressedImage.getPath(), Toast.LENGTH_LONG).show();
        Log.d("Compressor", "Compressed image save in " + compressedImage.getPath());
    }

    private void clearImage() {
        mIvCompressed.setImageDrawable(null);
         mTvCompressedSize.setText("Size : -");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_IMAGE && resultCode == RESULT_OK) {
            if (data == null) {
                showError("Failed to open picture!");
                return;
            }
            try {
                File actualImage = FileUtil.from(this, data.getData());
                mIvActual.setImageBitmap(BitmapFactory.decodeFile(actualImage.getAbsolutePath()));
                mTvActualSize.setText(String.format("Size : %s", getReadableFileSize(actualImage.length())));

                new  AsynCompressImage().execute(actualImage);
            } catch (IOException e) {
                showError("Failed to read picture data!");
                e.printStackTrace();
            }
        }
    }


    public void showError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    public String getReadableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        Log.d("Image Size", String.valueOf(size)+" <> "+Math.log10(size)+"<>"+Math.log10(1024));
        final String[] sizeUnits = new String[]{"B", "KB", "MB", "GB", "TB"};
        int index = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, index)) + " " + sizeUnits[index];
    }
  class AsynCompressImage extends AsyncTask<File,Void,File>
  {
      @Override
      protected File doInBackground(File... files) {
          File file = Compressor.getDefault(MainActivity.this).compressToFile(files[0]);
          return file;
      }

      @Override
      protected void onPostExecute(File file) {
          clearImage();
          if(file!=null)
            setCompressedImage(file);
      }
  }
}

