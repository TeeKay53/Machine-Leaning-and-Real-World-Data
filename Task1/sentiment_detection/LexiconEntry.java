package uk.ac.cam.tp423.MachineLearning.Task1.sentiment_detection;

import uk.ac.cam.cl.mlrwd.exercises.sentiment_detection.Sentiment;

import java.nio.file.Paths;

/**
 * Created by TeeKay on 1/20/2017.
 */
public class LexiconEntry {
    private String type;
    private String posl;
    private String stemmed1;
    private Sentiment feeling = null;

    public LexiconEntry(String newType, String newPosl, String newStemmed1, String newFeeling){
        type = newType;

        posl = newPosl;
        stemmed1 = newStemmed1;

        if (newFeeling.equals("negative")){
            feeling = Sentiment.NEGATIVE;
        }
        else if (newFeeling.equals("positive")){
            feeling = Sentiment.POSITIVE;
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPosl() {
        return posl;
    }

    public void setPosl(String posl) {
        this.posl = posl;
    }

    public String getStemmed1() {
        return stemmed1;
    }

    public void setStemmed1(String stemmed1) {
        this.stemmed1 = stemmed1;
    }

    public Sentiment getFeeling() {
        return feeling;
    }

    public void setFeeling(Sentiment feeling) {
        this.feeling = feeling;
    }

    public void changeFeeling(){
        if (feeling == Sentiment.NEGATIVE){
            feeling = Sentiment.POSITIVE;
        }
        else if (feeling == Sentiment.POSITIVE) feeling = Sentiment.NEGATIVE;
        else {
            double coinToss = Math.random();
            if (coinToss < 0.5){
                feeling = Sentiment.POSITIVE;
            }
            else feeling = Sentiment.NEGATIVE;
        }
    }

    public void changeType(){
        if (getType().equals("strongsubj")) type = "weaksubj";
        else if (getType().equals("weaksubj")) type = "strongsubj";
    }

    public void print(){
        String feel = (feeling == Sentiment.NEGATIVE) ? "negative" : "positive";
        System.out.println(type + " " + posl + " " + stemmed1 + " " + feeling);
    }
    public String toString(){
        return type + " " + stemmed1 + " " + feeling;
    }
}
