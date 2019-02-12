package bosch.cpm.com.bosch.constant;

import android.content.res.Resources;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import bosch.cpm.com.bosch.R;

public class SpeekingClock extends AppCompatActivity implements TextToSpeech.OnInitListener ,View.OnClickListener{
    private TextToSpeech tts;
    Button clickme;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speeking_clock);
        clickme=findViewById(R.id.clickme);
        tts = new TextToSpeech(this, this);
        clickme.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            String[] s=   Resources.getSystem().getAssets().getLocales();
            int result = tts.setLanguage(Locale.getDefault());
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                clickme.setEnabled(true);
                speakOut();
            }
        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.clickme){
            speakOut();
        }
    }

    private void speakOut() {
        Locale[] locale=Locale.getAvailableLocales();

        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");

        String date = df.format(Calendar.getInstance().getTime());
        tts.speak(date, TextToSpeech.QUEUE_FLUSH, null);
    }

}
