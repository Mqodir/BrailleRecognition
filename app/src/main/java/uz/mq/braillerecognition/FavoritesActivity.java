package uz.mq.braillerecognition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import static uz.mq.braillerecognition.HistoryDB.getFavs;
import static uz.mq.braillerecognition.HistoryDB.getHistory;

public class FavoritesActivity extends AppCompatActivity {

    RecyclerView favList;
    NestedScrollView mainScroll;
    HistoryAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        setActionBar();
        initViews();
    }

    private void initViews(){

        favList = (RecyclerView) findViewById(R.id.favList);
        favList.setLayoutManager(new LinearLayoutManager(this));
        mainScroll = (NestedScrollView) findViewById(R.id.favScroll);
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


        if (getFavs(FavoritesActivity.this).size() > 0){
            ((LinearLayout) findViewById(R.id.empty)).setVisibility(View.GONE);
            adapter = new HistoryAdapter(FavoritesActivity.this, getFavs(FavoritesActivity.this), true);
            favList.setAdapter(adapter);
        }else{
            ((LinearLayout) findViewById(R.id.empty)).setVisibility(View.VISIBLE);
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

    private void setActionBar(){
        getSupportActionBar().setTitle((Html.fromHtml("<font align=\"center\" color=\""+String.format("#%06x", ContextCompat.getColor(this, R.color.colorText) & 0xffffff)+"\">"+getString(R.string.favorite)+"</font>")));
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_icons8_left);
    }
}