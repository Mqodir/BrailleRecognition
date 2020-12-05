package uz.mq.braillerecognition;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.yalantis.ucrop.UCrop;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Time;

import static uz.mq.braillerecognition.HistoryDB.getHistory;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;

    AppBarLayout appBarLayout;
    DrawerLayout drawer;
    RecyclerView historyList;
    ViewOutlineProvider outlineProvider;
    NestedScrollView mainScroll;
    HistoryAdapter adapter;
    LinearLayout bottomActionBar;
    boolean isBottomBarShow = true;
    private String tState = "b->t";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setActionBar();
        setTitle("");
        setActionBarShadow(false);
        initViews();
    }

    private void initViews(){
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        bottomActionBar = (LinearLayout) findViewById(R.id.bottomActionBar);
        ((LinearLayout) findViewById(R.id.btnChange)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tState.equals("b->t")){
                    switchTranslation("t->b");
                }else{
                    switchTranslation("b->t");
                }
            }
        });
        ((LinearLayout) findViewById(R.id.btnCamera)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(1);
                }
                else {
                    startCameraIntent();
                }
            }
        });

        ((LinearLayout) findViewById(R.id.btnImport)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(2);
                }
                else {
                    startGalleryIntent();
                }
            }
        });
        ((LinearLayout) findViewById(R.id.btnKeyboard)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tState.equals("b->t")){
                    startActivity(new Intent(MainActivity.this, BrailleKeyboardActivity.class));
                }else{
                    startActivity(new Intent(MainActivity.this, TextToBraille.class));
                }
            }
        });

        historyList = (RecyclerView) findViewById(R.id.mainList);
        historyList.setLayoutManager(new LinearLayoutManager(this));
        mainScroll = (NestedScrollView) findViewById(R.id.mainScroll);
        mainScroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = mainScroll.getScrollY();
                Log.e("Scroll", scrollY+"");
                if (scrollY >= 10){
                    setActionBarShadow(true);
                    if (isBottomBarShow){
                        showBottomBar(false);
                    }
                }else{
                    setActionBarShadow(false);
                    if (!isBottomBarShow){
                        showBottomBar(true);
                    }
                }
            }
        });

        adapter = new HistoryAdapter(MainActivity.this, getHistory(MainActivity.this), false);
        historyList.setAdapter(adapter);
        if (getHistory(MainActivity.this).size() > 0){
            hideIntro();
        }
    }

    @Override
    protected void onStart() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(500);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new HistoryAdapter(MainActivity.this, getHistory(MainActivity.this), false);
                        historyList.setAdapter(adapter);
                    }
                });
            }
        }).start();
        super.onStart();
    }

    private void showBottomBar(boolean show){
        if (show){
            bottomActionBar.animate().scaleY(1f).scaleX(1f).translationY(0).setDuration(300);
            isBottomBarShow = true;
        }else{
            bottomActionBar.animate().scaleY(0.6f).scaleX(0.6f).translationY(300).setDuration(300);
            isBottomBarShow = false;
        }
    }

    private void requestPermissions(int code){
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                },
                code);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED)
            {
                startCameraIntent();
            }
        }
        if (requestCode == 2)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED)
            {
                startGalleryIntent();
            }
        }
    }

    String filenamecamera;
    Uri imageUricamera;
    private void startCameraIntent(){
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        filenamecamera = Environment.getExternalStorageDirectory().getPath() + "/camera.jpg";
        imageUricamera = Uri.fromFile(new File(filenamecamera));

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
                imageUricamera);
        startActivityForResult (cameraIntent, 10);
    }

    private void startGalleryIntent(){
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , 20);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (requestCode == 10 || requestCode == 20){
            if(resultCode == RESULT_OK){
                Uri selectedImageUri;
                if (requestCode == 10){
                    selectedImageUri = imageUricamera;
                }else{
                    selectedImageUri = imageReturnedIntent.getData();
                }
                if (null != selectedImageUri) {
                    try {
                        String filename = "trash.jpg";
                        File sd = new File(Environment.getExternalStorageDirectory() + File.separator + "BrailleRecognition");

                        if (!sd.exists()) {
                            sd.mkdirs();
                        }

                        File dest = new File(sd, filename);
                        Log.e("Save file name", dest.getAbsolutePath());
                        UCrop.of(selectedImageUri, Uri.parse(dest.getAbsolutePath()))
                                .start(MainActivity.this);

                    } catch (Exception e) {
                        Log.e("GetBitmapFromUri", "Error" + e.getMessage());
                    }
                }else{
                    Log.e("BitMap", "Null");
                }
            }
        }

        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(imageReturnedIntent);
            Log.e("GeneratenFile", resultUri.toString());
            try {
                Bitmap scaledBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),resultUri);

                String filename = "BrailleRecognition/upload.jpg";

                File sd = Environment.getExternalStorageDirectory();
                File dest = new File(sd, filename);

                Log.e("FileName", dest.getPath());
                try {
                    FileOutputStream out = new FileOutputStream(dest);
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();
                    //**********************************
                    //* Upload to server AND recognize *
                    //**********************************
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }catch (Exception e){
                Log.e("GeneratingBitmap", e.getMessage());
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(imageReturnedIntent);
            Log.e("UCropError", cropError.getMessage());
        }
    }

    private void hideIntro(){
        ((ConstraintLayout) findViewById(R.id.mainPage)).setBackgroundColor(getResources().getColor(R.color.colorWhite));
        ((ImageView) findViewById(R.id.welcome_illustration)).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.tvWelcome)).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.instruction)).setVisibility(View.GONE);
    }

    private void switchTranslation(final String newState){
        bottomActionBar.animate()
                .scaleX(0.0f)
                .scaleY(0.4f)
                .setDuration(400);
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(400);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (newState.equals("t->b")){
                            ((TextView) findViewById(R.id.tvLeft)).setText(getResources().getString(R.string.text));
                            ((ImageView) findViewById(R.id.ivLeft)).setImageResource(R.drawable.ic_icons8_z);
                            ((TextView) findViewById(R.id.tvRight)).setText(getResources().getString(R.string.braille));
                            ((ImageView) findViewById(R.id.ivRight)).setImageResource(R.drawable.braille_z_black);
                            ((ImageView) findViewById(R.id.ivKeyboard)).setImageResource(R.drawable.ic_icons8_keyboard);
                        }else{
                            ((TextView) findViewById(R.id.tvLeft)).setText(getResources().getString(R.string.braille));
                            ((ImageView) findViewById(R.id.ivLeft)).setImageResource(R.drawable.braille_z_black);
                            ((TextView) findViewById(R.id.tvRight)).setText(getResources().getString(R.string.text));
                            ((ImageView) findViewById(R.id.ivRight)).setImageResource(R.drawable.ic_icons8_z);
                            ((ImageView) findViewById(R.id.ivKeyboard)).setImageResource(R.drawable.ic_icons8_braille);
                        }
                        bottomActionBar.animate()
                                .scaleX(1.0f)
                                .scaleY(1.0f)
                                .setDuration(400);
                        tState = newState;
                    }
                });

            }
        }).start();
    }

    private void setActionBarShadow(Boolean shadow){
        if (shadow){
            appBarLayout.setOutlineProvider(outlineProvider);
        }else {
            appBarLayout.setOutlineProvider(null);
        }
    }

    private void setActionBar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        outlineProvider = appBarLayout.getOutlineProvider();
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_modern_menu);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_favorite:
                startActivity(new Intent(MainActivity.this, FavoritesActivity.class));
                break;
        }
        drawer.close();
        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                drawer.openDrawer(Gravity.LEFT);
                break;
            case R.id.action_info:
                Log.e("Size", getHistory(MainActivity.this).size()+"");
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}