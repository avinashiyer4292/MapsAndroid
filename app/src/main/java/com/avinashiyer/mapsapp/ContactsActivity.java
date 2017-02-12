package com.avinashiyer.mapsapp;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class ContactsActivity extends AppCompatActivity {
    TextToSpeech t1;
    RelativeLayout relativeLayout;
    private final int SPEECH_RECOGNITION_CODE_A = 1;
    private final int SPEECH_RECOGNITION_CODE_B = 2;
    CardView c1,c2;
    SimpleOnGestureListener sListener;
    GestureDetector gestureDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        c1 = (CardView)findViewById(R.id.cardView1);
        c2 = (CardView)findViewById(R.id.cardView2);
        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                    //systemSpeech("Welcome");
                    systemSpeech("Tap your phone to add your contact or swipe when done.");


                }
            }
        });
        relativeLayout= (RelativeLayout)findViewById(R.id.activity_contacts);
        relativeLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(),"hi",Toast.LENGTH_SHORT).show();
                startSpeechToText(1);
                //systemSpeech("Tap your phone to add your contact or swipe when done.");
                startSpeechToText(2);
            }
        });
        gestureDetector = new GestureDetector(ContactsActivity.this,new SimpleOnGestureListener());
        relativeLayout.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });



    }
    private void startSpeechToText(int code) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak something...");
        try {
            if(code==1)
                startActivityForResult(intent, SPEECH_RECOGNITION_CODE_A);
            else
                startActivityForResult(intent, SPEECH_RECOGNITION_CODE_B);
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
            case SPEECH_RECOGNITION_CODE_A: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String text = result.get(0);
                    //txtOutput.setText(text);
                    //Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();

                    //systemSpeech("Please tap phone again to confirm.");
                    //goToContacts();
                    makeVisible(c1);
                }
                break;

            }
            case SPEECH_RECOGNITION_CODE_B: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String text = result.get(0);
                    //txtOutput.setText(text);
                    //Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();

                    //systemSpeech("Please tap phone again to confirm.");
                    //goToContacts();
                    makeVisible(c2);
                }
                break;
            }
        }
    }
    private void makeVisible(CardView c){
        if(c.getVisibility()==View.INVISIBLE)
            c.setVisibility(View.VISIBLE);
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
    class SimpleOnGestureListener extends GestureDetector.SimpleOnGestureListener
    {

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d("Contacts","onDoubleTap");
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,float velocityY)
        {
            String velocity="onFling: \n" + e1.toString() + "\n" + e2.toString() +"\n"
                    + "velocityX= " + String.valueOf(velocityX) + "\n"
                    + "velocityY= " + String.valueOf(velocityY) + "\n";
            Log.d("Contacts","onFling velocity="+velocity);
            Intent i = new Intent(ContactsActivity.this,MapsActivity.class);
            startActivity(i);
            finish();
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.d("Contacts","onLongPress: \n" + e.toString());
            super.onLongPress(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.d("Contacts","onSingleTapConfirmed: \n" + e.toString());
            return super.onSingleTapConfirmed(e);
        }

        private boolean permissibleYVelocity(float velocityY)
        {
            if ((velocityY < -200) || (velocityY > 200))
            {
                return false;
            }
            else
            {
                return true;
            }

        }
    };




}
