package uk.ac.cam.tp423.MachineLearning.Task2;

/**
 * Created by TeeKay on 1/23/2017.
 */
import uk.ac.cam.cl.mlrwd.exercises.sentiment_detection.*;
import uk.ac.cam.cl.mlrwd.utils.DataSplit;
import uk.ac.cam.tp423.MachineLearning.Task1.sentiment_detection.Exercise1;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class Exercise2  implements IExercise2 {
    @Override
    public Map<Sentiment, Double> calculateClassProbabilities(Map<Path, Sentiment> trainingSet) throws IOException {

        Map<Sentiment, Double> classProb = new HashMap<>();

        double positiveRev = 0;
        double negRev = 0;
        for (Path path : trainingSet.keySet()) {
            if (trainingSet.get(path) == Sentiment.POSITIVE) positiveRev++;
            else if (trainingSet.get(path) == Sentiment.NEGATIVE) negRev++;
        }
        classProb.put(Sentiment.NEGATIVE, negRev / trainingSet.size());
        classProb.put(Sentiment.POSITIVE, positiveRev / trainingSet.size());

        return classProb;
    }

    @Override
    public Map<String, Map<Sentiment, Double>> calculateUnsmoothedLogProbs(Map<Path, Sentiment> trainingSet) throws IOException {


        Map<String, Map<Sentiment, Double>> prob = new TreeMap<>();
        Map<String, Integer> positiveOccurances = new HashMap<>();
        Map<String, Integer> negativeOccurances = new HashMap<>();

        double positiveWords = 0;
        double negativeWords = 0;

        for (Path path : trainingSet.keySet()) {

            List<String> words = Tokenizer.tokenize(path);
            for (String currentWord : words) {
                if (trainingSet.get(path) == Sentiment.NEGATIVE) {
                    negativeWords++;
                    negativeOccurances.put(currentWord, negativeOccurances.getOrDefault(currentWord, 0) + 1);
                    if (!positiveOccurances.containsKey(currentWord)) positiveOccurances.put(currentWord, 0);
                } else {
                    positiveWords++;
                    positiveOccurances.put(currentWord, positiveOccurances.getOrDefault(currentWord, 0) + 1);
                    if (!negativeOccurances.containsKey(currentWord))negativeOccurances.put(currentWord, 0);
                }
            }
        }
        for (String word : positiveOccurances.keySet()) {

            int posOcc = positiveOccurances.get(word);
            int negOcc = negativeOccurances.get(word);

            Map<Sentiment, Double> value = new HashMap<>();

            value.put(Sentiment.POSITIVE, Math.log(posOcc / positiveWords));
            value.put(Sentiment.NEGATIVE, Math.log(negOcc / negativeWords));
            prob.put(word, value);
        }

        return prob;
    }

    @Override
    public Map<String, Map<Sentiment, Double>> calculateSmoothedLogProbs(Map<Path, Sentiment> trainingSet) throws IOException {


        Map<String, Map<Sentiment, Double>> prob = new HashMap<>();
        Map<String, Integer> positiveOccurances = new HashMap<>();
        Map<String, Integer> negativeOccurances = new HashMap<>();

        double positiveWords = 0;
        double negativeWords = 0;
        int vocabulary = 0;


        for (Path path : trainingSet.keySet()) {

            List<String> words = Tokenizer.tokenize(path);
            for (String currentWord : words) {
                if (!positiveOccurances.containsKey(currentWord) && !negativeOccurances.containsKey(currentWord)) vocabulary++;

                if (trainingSet.get(path) == Sentiment.NEGATIVE) {
                    negativeWords++;
                    negativeOccurances.put(currentWord, negativeOccurances.getOrDefault(currentWord, 0) + 1);
                    if (!positiveOccurances.containsKey(currentWord)) positiveOccurances.put(currentWord, 0);
                } else {
                    positiveWords++;
                    positiveOccurances.put(currentWord, positiveOccurances.getOrDefault(currentWord, 0) + 1);
                    if (!negativeOccurances.containsKey(currentWord)) negativeOccurances.put(currentWord, 0);
                }
            }
        }
        positiveWords += vocabulary;
        negativeWords += vocabulary;

        for (String word : positiveOccurances.keySet()) {

            int posOcc = positiveOccurances.get(word) + 1;
            int negOcc = negativeOccurances.get(word) + 1;

            Map<Sentiment, Double> value = new HashMap<>();

            value.put(Sentiment.POSITIVE, Math.log(posOcc / positiveWords ));
            value.put(Sentiment.NEGATIVE, Math.log(negOcc / negativeWords ));
            prob.put(word, value);
        }

        return prob;
    }

    @Override
    public Map<Path, Sentiment> naiveBayes(Set<Path> testSet, Map<String, Map<Sentiment, Double>> tokenLogProbs, Map<Sentiment, Double> classProbabilities) throws IOException {
        Map<Path, Sentiment> naive = new HashMap<>();

        double posClass = Math.log(classProbabilities.get(Sentiment.POSITIVE));
        double negClass = Math.log(classProbabilities.get(Sentiment.NEGATIVE));

        for (Path path : testSet) {
            List<String> text = Tokenizer.tokenize(path);
            double posSum = 0;
            double negSum = 0;
            for (String word : text) {
                if (tokenLogProbs.containsKey(word)) {
                    posSum += tokenLogProbs.get(word).get(Sentiment.POSITIVE);
                    negSum += tokenLogProbs.get(word).get(Sentiment.NEGATIVE);
                }
            }
            if (negClass + negSum > posClass + posSum) {
                naive.put(path, Sentiment.NEGATIVE);
            } else naive.put(path, Sentiment.POSITIVE);
        }

        return naive;
    }
}