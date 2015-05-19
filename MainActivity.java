package com.example.jay.lecture1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class MainActivity extends ActionBarActivity {
    private String mCurrentPhotoPath;
    private File selfie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * takeSelfie ??????? ??????? ??????????? ??????? ?? ??????,
     * ??????? ???????????? ??????? ?????????? ??????????? ?? ??????????? ?????? ??????????.
     * @param view
     */
    public void takeSelfie(View view){
        Toast.makeText(this, "Take Selfie", Toast.LENGTH_LONG).show();

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
                selfie = photoFile;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            //
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, 1);
            }
        }
    }

    /**
     * shareSelfie ??????? ??????? ???? ??????? ?? ??????,
     * ??????? ??????? ????????? Native Share ??????? ?? ???????? ?????? ?????????????.
     * @param view
     */
    public void shareSelfie(View view){
        Toast.makeText(this, "Share Selfie", Toast.LENGTH_SHORT).show();

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:" + selfie.getAbsolutePath()));
        shareIntent.setType("image/jpeg");
        startActivity(Intent.createChooser(shareIntent, null));
    }

    /**
     * ?? ???? ??????? ?????? ???????? ?????????? ?????????? ?????? ??????? ????????????,
     * ????????? ?? ?????????? ???????.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap imageBitmap = BitmapFactory.decodeFile(selfie.getAbsolutePath(), options);

            imageBitmap = addWaterMark(imageBitmap);
            savebitmap(imageBitmap);

            ImageView mImageView = (ImageView) findViewById(R.id.selfie);
            mImageView.setImageBitmap(imageBitmap);
        }
    }

    /**
     * ??????? ??????? ??????.
     * @param photo
     * @return
     */
    private File savebitmap(Bitmap photo) {
        OutputStream outStream = null;

        try {
            outStream = new FileOutputStream(selfie);
            photo.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("file", "" + selfie);
        return selfie;
    }

    /**
     * ??????? GeoLab-? ????? ?????
     * @param src
     * @return
     */
    private Bitmap addWaterMark(Bitmap src) {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);

        Bitmap waterMark = BitmapFactory.decodeResource(getResources(), R.drawable.geolab_watermark_2);
        canvas.drawBitmap(waterMark, 5, 5, null);

        return result;
    }

    private File createImageFile() throws IOException {
        String imageFileName = "Selfie";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
