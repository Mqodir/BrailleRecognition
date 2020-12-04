package uz.mq.braillerecognition;

public class HistoryModel {
    String braille, text, date;
    Boolean isFav;

    public HistoryModel(String braille, String text, String date, Boolean isFav) {
        this.braille = braille;
        this.text = text;
        this.date = date;
        this.isFav = isFav;
    }

    public void setBraille(String braille) {
        this.braille = braille;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setFav(Boolean fav) {
        isFav = fav;
    }

    public String getBraille() {
        return braille;
    }

    public String getText() {
        return text;
    }

    public String getDate() {
        return date;
    }

    public Boolean getFav() {
        return isFav;
    }
}
