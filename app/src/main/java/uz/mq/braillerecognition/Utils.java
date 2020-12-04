package uz.mq.braillerecognition;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static String getCurrentDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return formatter.format(date);
    }
}
