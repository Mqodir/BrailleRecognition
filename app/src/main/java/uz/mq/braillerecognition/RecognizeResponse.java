package uz.mq.braillerecognition;

public class RecognizeResponse {
    String result;
    String resultPicUrl;
    String error;

    public RecognizeResponse(String result, String resultPicUrl, String error) {
        this.result = result;
        this.resultPicUrl = resultPicUrl;
        this.error = error;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResultPicUrl() {
        return resultPicUrl;
    }

    public void setResultPicUrl(String resultPicUrl) {
        this.resultPicUrl = resultPicUrl;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
