package sorajirocom.android.tess_two;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    static final String DEFAULT_LANGUAGE = "jpn";
    static final int REQUEST_CODE_CAMERA = 1;

    String filepath;
    Bitmap bitmap;
    TessBaseAPI tessBaseAPI;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView)findViewById(R.id.image_view);
        //bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.konnitiwa);

        filepath = getFilesDir() + "/tesseract/";

        tessBaseAPI = new TessBaseAPI();

        checkFile(new File(filepath + "tessdata/"));

        tessBaseAPI.init(filepath, DEFAULT_LANGUAGE);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tessBaseAPI.setImage(bitmap);
                String result = tessBaseAPI.getUTF8Text();
                Intent intent = new Intent(MainActivity.this, NextActivity.class);
                intent.putExtra("result", result);
                startActivity(intent);
            }
        });
    }

    private void checkFile(File file) {
        if (!file.exists() && file.mkdirs()){
            copyFiles();
        }
        if(file.exists()) {
            String datafilepath = filepath+ "/tessdata/jpn.traineddata";
            File datafile = new File(datafilepath);

            if (!datafile.exists()) {
                copyFiles();
            }
        }
    }

    private void copyFiles() {
        try {
            String datapath = filepath + "/tessdata/jpn.traineddata";
            InputStream instream = getAssets().open("tessdata/jpn.traineddata");
            OutputStream outstream = new FileOutputStream(datapath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }

            outstream.flush();
            outstream.close();
            instream.close();

            File file = new File(datapath);
            if (!file.exists()) {
                throw new FileNotFoundException();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void camera(View v){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,REQUEST_CODE_CAMERA);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent intent){
//        super.onActivityResult(requestCode,resultCode,intent);
//        if(requestCode == REQUEST_CODE_CAMERA){
//            bitmap = (Bitmap) intent.getExtras().get("data");
//            imageView.setImageBitmap(bitmap);
//
//
//        }else if(resultCode == RESULT_CANCELED ){
//            Toast.makeText(this,"CANCEL",Toast.LENGTH_SHORT).show();
//        }
//
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CAMERA) {
            // cancelしたケースも含む
            if(data.getExtras() == null){
                Log.d("debug","cancel ?");
                return;
            }
            else{
                bitmap = (Bitmap) data.getExtras().get("data");
                if(bitmap != null){
                    // 画像サイズを計測
                    int bmpWidth = bitmap.getWidth();
                    int bmpHeight = bitmap.getHeight();
                    Log.d("debug",String.format("w= %d",bmpWidth));
                    Log.d("debug",String.format("h= %d",bmpHeight));
                }
            }

            imageView.setImageBitmap(bitmap);
        }
    }
}
