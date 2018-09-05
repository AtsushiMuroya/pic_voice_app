package sorajirocom.android.tess_two;

import android.content.Intent;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

public class NextActivity extends AppCompatActivity implements View.OnClickListener, TextToSpeech.OnInitListener {

    private TextToSpeech tts;
    private static final String TAG = "TestTTS";
    String result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        Intent intent = getIntent();
         result = intent.getStringExtra("result");
         TextView textView = (TextView)findViewById(R.id.textView);
         textView.setText(result);

        // TTS インスタンス生成
        tts = new TextToSpeech(this, this);

        Button ttsButton = findViewById(R.id.button);
        ttsButton.setOnClickListener(this);
    }
    @Override
    public void onInit(int status) {
        // TTS初期化
        if (TextToSpeech.SUCCESS == status) {
            Log.d(TAG, "initialized");
        } else {
            Log.e(TAG, "failed to initialize");
        }
    }
    @Override
    public void onClick(View v) {
        speechText();
    }
    private void shutDown(){
        if (null != tts) {
            // to release the resource of TextToSpeech
            tts.shutdown();
        }
    }
    private void speechText() {
        //EditText editor = findViewById(R.id.editText);
        //editor.selectAll();
        // EditTextからテキストを取得
        //String string = editor.getText().toString();

        if (0 < result.length()) {
            if (tts.isSpeaking()) {
                tts.stop();
                return;
            }
            setSpeechRate(1.0f);
            setSpeechPitch(1.0f);

            if (Build.VERSION.SDK_INT >= 21){
                // SDK 21 以上
                tts.speak(result, TextToSpeech.QUEUE_FLUSH, null, "messageID");
            }
            else{
                // tts.speak(text, TextToSpeech.QUEUE_FLUSH, null) に
                // KEY_PARAM_UTTERANCE_ID を HasMap で設定
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"messageID");
                tts.speak(result, TextToSpeech.QUEUE_FLUSH, map);
            }

            setTtsListener();
        }
    }
    // 読み上げのスピード
    private void setSpeechRate(float rate){
        if (null != tts) {
            tts.setSpeechRate(rate);
        }
    }

    // 読み上げのピッチ
    private void setSpeechPitch(float pitch){
        if (null != tts) {
            tts.setPitch(pitch);
        }
    }

    // 読み上げの始まりと終わりを取得
    private void setTtsListener(){
        // android version more than 15th
        if (Build.VERSION.SDK_INT >= 15){
            int listenerResult =
                    tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onDone(String utteranceId) {
                            Log.d(TAG,"progress on Done " + utteranceId);
                        }

                        @Override
                        public void onError(String utteranceId) {
                            Log.d(TAG,"progress on Error " + utteranceId);
                        }

                        @Override
                        public void onStart(String utteranceId) {
                            Log.d(TAG,"progress on Start " + utteranceId);
                        }
                    });

            if (listenerResult != TextToSpeech.SUCCESS) {
                Log.e(TAG, "failed to add utterance progress listener");
            }
        }
        else {
            Log.e(TAG, "Build VERSION is less than API 15");
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        shutDown();
    }
}
