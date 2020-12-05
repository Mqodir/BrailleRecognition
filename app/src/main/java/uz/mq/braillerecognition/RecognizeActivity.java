package uz.mq.braillerecognition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;

import static uz.mq.braillerecognition.HistoryDB.addToHistory;
import static uz.mq.braillerecognition.Utils.getCurrentDate;

public class RecognizeActivity extends AppCompatActivity {

    BrailleRecognizer recognizer;
    File file;
    ScrollView mainScroll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognize);
        setActionBar();
        File sd = Environment.getExternalStorageDirectory();
        file = new File(sd, getIntent().getStringExtra("file"));
        recognizer = new BrailleRecognizer(RecognizeActivity.this);
        initViews();
    }

    private void initViews(){
        ((ImageView) findViewById(R.id.imagePreview)).setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
        ((Button) findViewById(R.id.btnKiril)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((LinearLayout) findViewById(R.id.absParent)).setVisibility(View.GONE);
                ((ProgressBar) findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
                startRecognize("RU");
            }
        });
        ((Button) findViewById(R.id.btnLatin)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((LinearLayout) findViewById(R.id.absParent)).setVisibility(View.GONE);
                ((ProgressBar) findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
                startRecognize("EN");
            }
        });
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
    private void showLoading(boolean isShow){
        if (isShow){
            ((LinearLayout) findViewById(R.id.loading)).setVisibility(View.VISIBLE);
            ((ProgressBar) findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
        }else{
            ((LinearLayout) findViewById(R.id.loading)).setVisibility(View.GONE);
            ((ProgressBar) findViewById(R.id.progressBar)).setVisibility(View.GONE);
        }
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
    private void downloadImage(String url){
        DownloadManager downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        Long reference = downloadManager.enqueue(request);
    }

    private void startRecognize(final String lang){
        showLoading(true);
        ((TextView) findViewById(R.id.tvLoading)).setText(getResources().getString(R.string.pleaseWaint));
        new Thread(new Runnable() {
            @Override
            public void run() {
                recognizer.initRecognizer();
                final RecognizeResponse recognizeResponse = recognizer.recognize(file, lang);
                if (recognizeResponse.error.equals("")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (recognizeResponse.getResult().equals("")){
                                ((ProgressBar) findViewById(R.id.progressBar)).setVisibility(View.GONE);
                                ((TextView) findViewById(R.id.tvLoading)).setText(getResources().getString(R.string.nodata));
                            }else{
                                addToHistory(RecognizeActivity.this, new HistoryModel(recognizeResponse.getResult(), recognizeResponse.getResult(), getCurrentDate(), false));
                                ((ImageView) findViewById(R.id.ivRecognized)).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        startActivity(new Intent(RecognizeActivity.this, ImageViewer.class).putExtra("url", recognizeResponse.getResultPicUrl()));
                                    }
                                });
                                ((Button) findViewById(R.id.btnSave)).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        downloadImage(recognizeResponse.getResultPicUrl());
                                    }
                                });
                                Glide.with(RecognizeActivity.this).load(recognizeResponse.getResultPicUrl()).placeholder(R.drawable.test).into(((ImageView) findViewById(R.id.ivRecognized)));
                                ((TextView) findViewById(R.id.tvText)).setText(recognizeResponse.getResult());
                                showLoading(false);
                            }
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((ProgressBar) findViewById(R.id.progressBar)).setVisibility(View.GONE);
                            ((TextView) findViewById(R.id.tvLoading)).setText(getResources().getString(R.string.error));
                        }
                    });
                }
            }
        }).start();
    }
    private void setActionBar(){
        getSupportActionBar().setTitle((Html.fromHtml("<font align=\"center\" color=\""+String.format("#%06x", ContextCompat.getColor(this, R.color.colorText) & 0xffffff)+"\">"+getString(R.string.app_name)+"</font>")));
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_icons8_left);
    }
}