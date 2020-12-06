package uz.mq.braillerecognition;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.util.List;

import com.google.android.material.tabs.TabLayout;
import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import static android.content.ContentValues.TAG;

public class BrailleRecognizer {
    Context ctx;
    OkHttpClient client;
    CookieManager cookieManager;
    private static String LOG = "BrailleRecognizer";
    public static String SERVER_HOST = "http://angelina-reader.ru/";
    String token = "";
    public BrailleRecognizer(Context ctx) {
        this.ctx = ctx;
        client = new OkHttpClient();
        cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        client.setCookieHandler(cookieManager);
    }

    public CookieManager getCookieManager() {
        return cookieManager;
    }

    public void initRecognizer(){

        String token = getToken();
        RequestBody formBody = new FormEncodingBuilder()
                .add("csrf_token", token)
                .add("e_mail", "m.qodir777@gmail.com")
                .add("submit", "Войти")
                .build();

        final Request request = new Request.Builder().url(SERVER_HOST+"login")
                .post(formBody).build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()){
                Log.i(LOG, "Success");
                Log.i(LOG, "Response"+response.code());
                Log.e(LOG, response.body().string());
            }else{
                Log.i(LOG, "Response"+response.code());
            }
        }catch (Exception e){
            Log.e(LOG, e.getMessage()+"");
        }
    }

    public String getToken(){
        final Request request = new Request.Builder().url(SERVER_HOST+"login")
                .build();

        try {
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()){
                String responseString = response.body().string();

                Document doc = Jsoup.parse(responseString);
                Element tokenElement = doc.select("input[name=csrf_token]").first();
                String token = tokenElement.attr("value");
                Log.i(LOG, "Token::"+token);
                this.token = token;
                return token;
            }else{
                Log.e(LOG, "getToken::response.isSuccessful=false");
                return "getToken::response.isSuccessful=false";
            }
        }catch (Exception e){
            Log.e(LOG, "Error:"+ e.getMessage());
            return "Error";
        }
    }

    public RecognizeResponse recognize(File file, String lang){

        final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpeg");
        RequestBody formBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart("csrf_token", token)
                .addFormDataPart("lang", lang)
                .addFormDataPart("agree", "y")
                .addFormDataPart("find_orientation", "y")
                .addFormDataPart("file","profile.jpg", RequestBody.create(MEDIA_TYPE_PNG, file))
                .build();

        final Request request = new Request.Builder().url(SERVER_HOST+"index")
                .post(formBody).build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()){

                String responseString = response.body().string();

                Document doc = Jsoup.parse(responseString);
                Element textElement = doc.select("textarea[name=text]").first();
                String text = textElement.html();
                Element picElement = doc.select("img[alt='Recognized image']").first();
                String pic = SERVER_HOST.substring(0, SERVER_HOST.length()-1) + picElement.attr("src");
                Log.i(LOG, "Recognized text:"+token);
                return new RecognizeResponse(text, pic, "");
            }else{
                Log.i(LOG, "Response"+response.code());
                Log.e(LOG, request.body().toString());
                return new RecognizeResponse("", "", "is Succes False");
            }
        }catch (Exception e){
            Log.e(LOG, e.getMessage()+"");
            return new RecognizeResponse("", "", e.getMessage()+"");
        }
    }
}
