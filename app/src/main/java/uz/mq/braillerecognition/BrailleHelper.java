package uz.mq.braillerecognition;
import uz.mq.braillerecognition.BrailleChars;

import static uz.mq.braillerecognition.BrailleChars.latin;
import static uz.mq.braillerecognition.BrailleChars.latinB;
import static uz.mq.braillerecognition.BrailleChars.numbers;
import static uz.mq.braillerecognition.BrailleChars.numbersB;

public class BrailleHelper {

    public static String getLatinFormArray(boolean[] arr){
        String arr_str = getStrFomArray(arr);
        if (indexOf(arr_str, latinB) >= 0){
            return String.valueOf(latin[indexOf(arr_str, latinB)]);
        }else{
            return "none";
        }
    }

    public static String getNumberFormArray(boolean[] arr){
        String arr_str = getStrFomArray(arr);
        if (indexOf(arr_str, numbersB) >= 0){
            return String.valueOf(numbers[indexOf(arr_str, numbersB)]);
        }else{
            return "none";
        }
    }

    public static String getStrFomArray(boolean[] arr){
        String arr_str = "";
        for (int i=0; i<arr.length; i++){
            arr_str += String.valueOf(arr[i] ? 1 : 0);
        }
        return arr_str;
    }

    public static int indexOf(String val, String[] arr){
        for (int i=0; i<arr.length; i++){
            if (arr[i].equals(val)){
                return i;
            }
        }
        return -1;
    }
}
