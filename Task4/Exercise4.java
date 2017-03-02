package uk.ac.cam.tp423.MachineLearning.Task4;

import uk.ac.cam.cl.mlrwd.exercises.sentiment_detection.IExercise4;
import uk.ac.cam.cl.mlrwd.exercises.sentiment_detection.Sentiment;
import uk.ac.cam.cl.mlrwd.exercises.sentiment_detection.Tokenizer;
import uk.ac.cam.tp423.MachineLearning.Task1.sentiment_detection.Lexicon;
import uk.ac.cam.tp423.MachineLearning.Task1.sentiment_detection.LexiconEntry;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by TeeKay on 1/30/2017.
 */
public class Exercise4 implements IExercise4 {


    @Override
    public Map<Path, Sentiment> magnitudeClassifier(Set<Path> testSet, Path lexiconFile) throws IOException {
        Map<String, LexiconEntry> lexi = Lexicon.lexicon(lexiconFile);
        Map<Path, Sentiment> sentiments = new HashMap<>();
        for (Path path : testSet) {
            List<String> currentReview = Tokenizer.tokenize(path);
            int score = 0;
            int polarity = 1;
            double strenght = 1;
            for (String word : currentReview) {
                if (word.startsWith("not-")) {
                    polarity *= -1;
                    word = word.split("-")[1];
                }
                if (lexi.containsKey(word)) {

                    if (lexi.get(word).getType().equals("strongsubj")) strenght = 2;
                    else if (lexi.get(word).getType().equals("weaksubj")) strenght = 1;
                    else strenght = 1;

                    if (lexi.get(word).getFeeling() == Sentiment.NEGATIVE) {
                        score += (-1) * polarity * strenght;
                    } else if (lexi.get(word).getFeeling() == Sentiment.POSITIVE) {
                        score += polarity * strenght;
                    }
                }
                if (word.equals("not")) {
                    polarity = -1;
                } else polarity = 1;
            }

            if (score < 0) {
                sentiments.put(path, Sentiment.NEGATIVE);
            } else {
                sentiments.put(path, Sentiment.POSITIVE);
            }
        }
        return sentiments;
    }

    @Override
    public double signTest(Map<Path, Sentiment> actualSentiments, Map<Path, Sentiment> classificationA, Map<Path, Sentiment> classificationB) {
        int plus = 0;
        int minus = 0;
        int NULL = 0;
        for (Path path : actualSentiments.keySet()){
            Sentiment A = classificationA.get(path);
            Sentiment B = classificationB.get(path);
            if (A == B) NULL++;
            else {
                Sentiment actual = actualSentiments.get(path);
                if (actual == A) plus++;
                else if (actual == B) minus++;
                else NULL++;
            }
        }

        int n = 2 *((NULL+1) / 2)  + plus + minus;
        int k = ((NULL+1) / 2)  + Math.min(plus, minus);
        double sum = 0;
        System.out.println(plus + " " + minus + " " + NULL);
        Combinatorial combinationGenerator = new Combinatorial(n);
        for (int i = 0; i <= k; i++){
             sum += (combinationGenerator.choose(i)).multiply(new BigDecimal(Math.pow(0.5, i))).multiply(new BigDecimal(Math.pow(0.5, n - i))).doubleValue();
        }
        return sum * 2;
    }


}
