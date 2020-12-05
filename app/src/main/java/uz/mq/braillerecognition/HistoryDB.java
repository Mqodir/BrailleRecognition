package uz.mq.braillerecognition;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class HistoryDB {

    public static void addToHistory(Context ctx, HistoryModel model){
        ArrayList<HistoryModel> historyModels;
        Gson gson = new Gson();
        SharedPreferences sharedPreference = ctx.getSharedPreferences("History", Context.MODE_PRIVATE);
        String cash_str = sharedPreference.getString("History", "empty");
        if (cash_str.equals("empty")){
            historyModels = new ArrayList<>();
            historyModels.add(model);
        }else{
            Type typeOfObjectsList = new TypeToken<ArrayList<HistoryModel>>() {}.getType();
            historyModels = gson.fromJson(cash_str, typeOfObjectsList);
            historyModels.add(model);
        }
        sharedPreference.edit().putString("History", gson.toJson(historyModels)).apply();
    }


    public static void switchFav(Context ctx, int index){
        ArrayList<HistoryModel> historyModels;
        Gson gson = new Gson();
        SharedPreferences sharedPreference = ctx.getSharedPreferences("History", Context.MODE_PRIVATE);
        String cash_str = sharedPreference.getString("History", "empty");
        Type typeOfObjectsList = new TypeToken<ArrayList<HistoryModel>>() {}.getType();
        historyModels = gson.fromJson(cash_str, typeOfObjectsList);
        historyModels.get(index).setFav(!historyModels.get(index).getFav());
        sharedPreference.edit().putString("History", gson.toJson(historyModels)).apply();
    }

    public static void removeFav(Context ctx, String date){
        ArrayList<HistoryModel> historyModels;
        Gson gson = new Gson();
        SharedPreferences sharedPreference = ctx.getSharedPreferences("History", Context.MODE_PRIVATE);
        String cash_str = sharedPreference.getString("History", "empty");
        Type typeOfObjectsList = new TypeToken<ArrayList<HistoryModel>>() {}.getType();
        historyModels = gson.fromJson(cash_str, typeOfObjectsList);
        for (int i=0; i<historyModels.size(); i++){
            if (historyModels.get(i).getDate().equals(date)){
                historyModels.get(i).setFav(false);
                break;
            }
        }
        sharedPreference.edit().putString("History", gson.toJson(historyModels)).apply();
    }

    public static ArrayList<HistoryModel> getHistory(Context ctx){
        ArrayList<HistoryModel> historyModels = new ArrayList<>();
        Gson gson = new Gson();
        SharedPreferences sharedPreference = ctx.getSharedPreferences("History", Context.MODE_PRIVATE);
        String cash_str = sharedPreference.getString("History", "empty");
        if (!cash_str.equals("empty")){
            Type typeOfObjectsList = new TypeToken<ArrayList<HistoryModel>>() {}.getType();
            historyModels = gson.fromJson(cash_str, typeOfObjectsList);
            Collections.reverse(historyModels);
            return historyModels;
        }else{
            return historyModels;
        }
    }

    public static ArrayList<HistoryModel> getFavs(Context ctx){
        ArrayList<HistoryModel> historyModels = new ArrayList<>();
        Gson gson = new Gson();
        SharedPreferences sharedPreference = ctx.getSharedPreferences("History", Context.MODE_PRIVATE);
        String cash_str = sharedPreference.getString("History", "empty");

        if (!cash_str.equals("empty")){
            Type typeOfObjectsList = new TypeToken<ArrayList<HistoryModel>>() {}.getType();
            historyModels = gson.fromJson(cash_str, typeOfObjectsList);
            ArrayList<HistoryModel> favsModels;
            favsModels = new ArrayList<>();
            for (int i=0; i<historyModels.size(); i++){
                if (historyModels.get(i).getFav()){
                    favsModels.add(historyModels.get(i));
                }
            }
            return favsModels;
        }else{
            return historyModels;
        }
    }

}
