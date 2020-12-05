package uz.mq.braillerecognition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import static uz.mq.braillerecognition.BrailleHelper.getLatinFormArray;
import static uz.mq.braillerecognition.BrailleHelper.getNumberFormArray;
import static uz.mq.braillerecognition.HistoryDB.addToHistory;
import static uz.mq.braillerecognition.Utils.getCurrentDate;

public class BrailleKeyboardActivity extends AppCompatActivity {

    ImageView[] points;
    LinearLayout[] btnPoints;
    boolean[] value = new boolean[6];
    LinearLayout btnLett, btnNum;
    TextView tvLett, tvNumm;
    ScrollView mainScroll;
    TextView tvBraille, tvText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_braille_keyboard);
        setActionBar();
        initViews();
    }

    public void onStop () {
        if (!tvBraille.getText().toString().equals("")){
            addToHistory(BrailleKeyboardActivity.this, new HistoryModel(tvBraille.getText().toString(), tvText.getText().toString(), getCurrentDate(), false));
        }
        super.onStop();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews(){
        btnLett = (LinearLayout) findViewById(R.id.btnLett);
        btnNum = (LinearLayout) findViewById(R.id.btnNumm);
        btnNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addChar(tvNumm.getText().toString());
            }
        });
        btnLett.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addChar(tvLett.getText().toString());
            }
        });
        tvNumm = (TextView) findViewById(R.id.tvNumm);
        tvLett = (TextView) findViewById(R.id.tvLett);
        tvBraille = (TextView) findViewById(R.id.tvBraille);
        tvText = (TextView) findViewById(R.id.tvText);
        points = new ImageView[6];
        points[0] = (ImageView) findViewById(R.id.point_1);
        points[1] = (ImageView) findViewById(R.id.point_2);
        points[2] = (ImageView) findViewById(R.id.point_3);
        points[3] = (ImageView) findViewById(R.id.point_4);
        points[4] = (ImageView) findViewById(R.id.point_5);
        points[5] = (ImageView) findViewById(R.id.point_6);
        btnPoints = new LinearLayout[6];
        btnPoints[0] = (LinearLayout) findViewById(R.id.btnPoint_1);
        btnPoints[1] = (LinearLayout) findViewById(R.id.btnPoint_2);
        btnPoints[2] = (LinearLayout) findViewById(R.id.btnPoint_3);
        btnPoints[3] = (LinearLayout) findViewById(R.id.btnPoint_4);
        btnPoints[4] = (LinearLayout) findViewById(R.id.btnPoint_5);
        btnPoints[5] = (LinearLayout) findViewById(R.id.btnPoint_6);

        for (int i=0; i<6; i++){
            final int j = i;
            btnPoints[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    keyboardAction(j);
                }
            });
        }
        mainScroll = (ScrollView) findViewById(R.id.mainScroll);
        mainScroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = mainScroll.getScrollY();
                Log.e("Scroll", scrollY+"");
                if (scrollY >= 10){
                    getSupportActionBar().setElevation(10);
                }else{
                    getSupportActionBar().setElevation(0);
                }
            }
        });
    }

    private void chekChars(){
        String lett = getLatinFormArray(value);
        if (!lett.equals("none")){
            btnLett.setVisibility(View.VISIBLE);
            tvLett.setText(lett);
        }else{
            btnLett.setVisibility(View.GONE);
        }

        String numm = getNumberFormArray(value);
        if (!numm.equals("none")){
            btnNum.setVisibility(View.VISIBLE);
            tvNumm.setText(numm);
        }else{
            btnNum.setVisibility(View.GONE);
        }
    }
    String data = "";
    private void addChar(String ch){
        data += ch;
        tvText.setText(data);
        tvBraille.setText(data);
        clear();
    }

    private void clear(){
        for (int i=0; i<6; i++){
            value[i] = false;
            points[i].setImageResource(R.drawable.ic_icons8_circle_1);
        }
        beep();
        chekChars();
    }

    private void keyboardAction(int j){
        if (value[j]){
            points[j].setImageResource(R.drawable.ic_icons8_circle_1);
            value[j] = false;
        }else{
            points[j].setImageResource(R.drawable.ic_icons8_filled_circle);
            value[j] = true;
        }
        beep();
        chekChars();
    }


    private void beep(){
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(10);
        }
    }

    private void setActionBar(){
        getSupportActionBar().setTitle((Html.fromHtml("<font align=\"center\" color=\""+String.format("#%06x", ContextCompat.getColor(this, R.color.colorText) & 0xffffff)+"\">"+getString(R.string.app_name)+"</font>")));
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_icons8_left);
    }
}