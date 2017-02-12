package com.avinashiyer.mapsapp;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class ContactsActivity extends AppCompatActivity {
    TextToSpeech t1;
    RelativeLayout relativeLayout;
    private final int SPEECH_RECOGNITION_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                    //systemSpeech("Welcome");
                    systemSpeech("Tap your phone to add your contact!");


                }
            }
        });
        relativeLayout= (RelativeLayout)findViewById(R.id.activity_contacts);
        relativeLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(),"hi",Toast.LENGTH_SHORT).show();
                startSpeechToText();
            }
        });


    }
    private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak something...");
        try {
            startActivityForResult(intent, SPEECH_RECOGNITION_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Speech recognition is not supported in this device.",
                    Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * Callback for speech recognition activity
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SPEECH_RECOGNITION_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String text = result.get(0);
                    //txtOutput.setText(text);
                    //Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
                    systemSpeech("Your destination is: "+text);
                    //systemSpeech("Please tap phone again to confirm.");
                    //goToContacts();

                }
                break;
            }
        }
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text){
        String utteranceId=this.hashCode() + "";
        t1.speak(text, TextToSpeech.QUEUE_ADD, null, utteranceId);
    }
    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text){
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        t1.speak(text, TextToSpeech.QUEUE_ADD, map);
    }

    private void systemSpeech(String text){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            ttsGreater21(text);
//            text="Tap your phone to enter your destination.";
//            ttsGreater21(text);
        } else {
            ttsUnder20(text);
//            text="Tap your phone to enter your destination.";
//            ttsUnder20(text);
        }
    }
}
