package sorajirocom.android.tess_two;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    static final String DEFAULT_LANGUAGE = "jpn";

    String filepath;
    Bitmap bitmap;
    TessBaseAPI tessBaseAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.konnitiwa);

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
}
