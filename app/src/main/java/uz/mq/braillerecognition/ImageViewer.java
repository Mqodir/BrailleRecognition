package uz.mq.braillerecognition;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageViewer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        getSupportActionBar().hide();
        Glide.with(ImageViewer.this).load(getIntent().getStringExtra("url")).placeholder(R.drawable.test).into(((ImageView) findViewById(R.id.iv_image_activity)));
    }
}