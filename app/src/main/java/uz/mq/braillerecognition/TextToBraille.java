package uz.mq.braillerecognition;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import static uz.mq.braillerecognition.HistoryDB.addToHistory;
import static uz.mq.braillerecognition.Utils.getCurrentDate;

public class TextToBraille extends AppCompatActivity {
    EditText edText;
    TextView tvBraille;
    String data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_to_braille);
        setActionBar();
        initViews();
    }

    private void initViews(){
        edText = (EditText) findViewById(R.id.edText);
        tvBraille= (TextView) findViewById(R.id.tvBraille);
        edText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tvBraille.setText(edText.getText());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void onStop () {
        if (tvBraille.getText().toString().equals("")){
            addToHistory(TextToBraille.this, new HistoryModel(tvBraille.getText().toString(), edText.getText().toString(), getCurrentDate(), false));
        }
        super.onStop();
    }

    private void setActionBar(){
        getSupportActionBar().setTitle((Html.fromHtml("<font align=\"center\" color=\""+String.format("#%06x", ContextCompat.getColor(this, R.color.colorText) & 0xffffff)+"\">"+getString(R.string.app_name)+"</font>")));
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_icons8_left);
    }
}