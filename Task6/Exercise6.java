package uk.ac.cam.tp423.MachineLearning.Task6;

import uk.ac.cam.cl.mlrwd.exercises.sentiment_detection.IExercise6;
import uk.ac.cam.cl.mlrwd.exercises.sentiment_detection.NuancedSentiment;
import uk.ac.cam.cl.mlrwd.exercises.sentiment_detection.Sentiment;
import uk.ac.cam.cl.mlrwd.exercises.sentiment_detection.Tokenizer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * Created by TeeKay on 2/8/2017.
 */
public class Exercise6 implements IExercise6{


    @Override
    public Map<NuancedSentiment, Double> calculateClassProbabilities(Map<Path, NuancedSentiment> trainingSet) throws IOException {
        Map<NuancedSentiment, Double> classProbs = new HashMap<>();

        int posCount = 0;
        int negCount = 0;
        int neutralCount = 0;

        for (Path path : trainingSet.keySet()){
            NuancedSentiment currentSent = trainingSet.get(path);
            if (currentSent == NuancedSentiment.POSITIVE){
                posCount++;
            }
            else if (currentSent == NuancedSentiment.NEGATIVE){
                negCount++;
            }
            else if (currentSent == NuancedSentiment.NEUTRAL){
                neutralCount++;
            }
        }
        double total = trainingSet.size();
        classProbs.put(NuancedSentiment.POSITIVE, posCount/total);
        classProbs.put(NuancedSentiment.NEGATIVE, negCount/total);
        classProbs.put(NuancedSentiment.NEUTRAL, neutralCount/total);

        return classProbs;
    }

    @Override
    public Map<String, Map<NuancedSentiment, Double>> calculateNuancedLogProbs(Map<Path, NuancedSentiment> trainingSet) throws IOException {
        Map<String, Map<NuancedSentiment, Double>> prob = new HashMap<>();

        Map<String, Integer> positiveOccurrences = new HashMap<>();
        Map<String, Integer> negativeOccurrences = new HashMap<>();
        Map<String, Integer> neutralOccurrences = new HashMap<>();

        Set<String> words = new HashSet<>();

        int posCount = 0;
        int negCount = 0;
        int neutralCount = 0;


        for (Path path: trainingSet.keySet()){
            List<String> review = Tokenizer.tokenize(path);
            NuancedSentiment currentSent = trainingSet.get(path);
            for (String word : review){
                words.add(word);

                if (currentSent == NuancedSentiment.POSITIVE){
                    posCount++;
                    positiveOccurrences.put(word, positiveOccurrences.getOrDefault(word, 0) + 1);
                }
                else if (currentSent == NuancedSentiment.NEGATIVE){
                    negCount++;
                    negativeOccurrences.put(word, negativeOccurrences.getOrDefault(word, 0) + 1);
                }
                else if (currentSent == NuancedSentiment.NEUTRAL){
                    neutralCount++;
                    neutralOccurrences.put(word, neutralOccurrences.getOrDefault(word, 0) + 1);
                }
            }
        }
        double vocabulary = words.size();
        posCount += vocabulary;
        negCount += vocabulary;
        neutralCount += vocabulary;

        for (String word : words){
            double negOccurrences = negativeOccurrences.getOrDefault(word, 0) + 1;
            double posOccurences = positiveOccurrences.getOrDefault(word, 0) + 1;
            double neuOccurrences = neutralOccurrences.getOrDefault(word, 0) + 1;

            Map<NuancedSentiment, Double> value= new HashMap<>();
            value.put(NuancedSentiment.POSITIVE, Math.log(posOccurences/posCount));
            value.put(NuancedSentiment.NEGATIVE, Math.log(negOccurrences/negCount));
            value.put(NuancedSentiment.NEUTRAL, Math.log(neuOccurrences/ neutralCount));

            prob.put(word, value);

        }
        return prob;
    }

    @Override
    public Map<Path, NuancedSentiment> nuancedClassifier(Set<Path> testSet, Map<String, Map<NuancedSentiment, Double>> tokenLogProbs, Map<NuancedSentiment, Double> classProbabilities) throws IOException {
        Map<Path, NuancedSentiment> naive = new HashMap<>();

        double posClass = Math.log(classProbabilities.get(NuancedSentiment.POSITIVE));
        double negClass = Math.log(classProbabilities.get(NuancedSentiment.NEGATIVE));
        double neuClass = Math.log(classProbabilities.get(NuancedSentiment.NEUTRAL));

        for (Path path : testSet) {
            List<String> text = Tokenizer.tokenize(path);
            double posSum = 0;
            double negSum = 0;
            double neuSum = 0;
            for (String word : text) {
                if (tokenLogProbs.containsKey(word)) {
                    posSum += tokenLogProbs.get(word).get(NuancedSentiment.POSITIVE);
                    negSum += tokenLogProbs.get(word).get(NuancedSentiment.NEGATIVE);
                    neuSum += tokenLogProbs.get(word).get(NuancedSentiment.NEUTRAL);
                }
            }
            double finalNegScore = negClass + negSum;
            double finalPosScore = posClass + posSum;
            double finalNeuScore = neuClass + neuSum;
            double max = Math.max(finalNegScore, Math.max(finalPosScore, finalNeuScore));
            if (finalPosScore == max){
                naive.put(path, NuancedSentiment.POSITIVE);
            }
            else if (finalNegScore == max){
                naive.put(path, NuancedSentiment.NEGATIVE);
            }
            else {
                naive.put(path, NuancedSentiment.NEUTRAL);
            }
        }

        return naive;

    }

    @Override
    public double nuancedAccuracy(Map<Path, NuancedSentiment> trueSentiments, Map<Path, NuancedSentiment> predictedSentiments) {
        double valid = 0;
        double agree = 0;
        for (Path path : predictedSentiments.keySet()){
            if (trueSentiments.containsKey(path)){
                valid++;
                if (trueSentiments.get(path) == predictedSentiments.get(path)){
                    agree++;
                }
            }
        }
        return agree/valid;
    }

    @Override
    public Map<Integer, Map<Sentiment, Integer>> agreementTable(Collection<Map<Integer, Sentiment>> predictedSentiments) {
        Map<Integer, Map<Sentiment, Integer>> table = new TreeMap<>();

        for(Map<Integer, Sentiment> map : predictedSentiments){
            for (int i = 1; i < map.size() + 1; i++){
                Sentiment current = map.get(i);
                Map<Sentiment, Integer> value = table.getOrDefault(i, new HashMap<>());
                if (current == Sentiment.POSITIVE){
                    value.put(Sentiment.POSITIVE, value.getOrDefault(Sentiment.POSITIVE, 0) + 1);
                }
                else if (current == Sentiment.NEGATIVE){
                    value.put(Sentiment.NEGATIVE, value.getOrDefault(Sentiment.NEGATIVE, 0) + 1);
                }
                if (!table.containsKey(i)){
                    table.put(i,value);
                }
            }
        }
        System.out.println(table);
        return table;
    }

    @Override
    public double kappa(Map<Integer, Map<Sentiment, Integer>> agreementTable) {
        double Pe = 0;
        double Pa = 0;
        int N = agreementTable.size();
        int[][] Nij = new int[N][2];
        double[] Ni = new double[N];

        int arrayPlaceCount = 0;
        for (Integer number : agreementTable.keySet()){
            Map<Sentiment, Integer> map = agreementTable.get(number);
            Nij[arrayPlaceCount][1] =map.getOrDefault(Sentiment.POSITIVE, 0);
            Nij[arrayPlaceCount][0] = map.getOrDefault(Sentiment.NEGATIVE, 0);
            Ni[arrayPlaceCount] = Nij[arrayPlaceCount][1] + Nij[arrayPlaceCount][0];

            arrayPlaceCount++;
        }
        //It starts at 0 instead of 1, but it works because the array also starts indexing from 0.
        for (int i = 0; i < N; i++){
            Pe += Nij[i][0] / Ni[i];
            Pa += (Nij[i][0]*(Nij[i][0] - 1) + Nij[i][1]*(Nij[i][1] - 1)) / (Ni[i]*(Ni[i] - 1));
        }

        Pa /= N;
        Pe = Math.pow(Pe / N, 2);
        //need this to do the same thing, but with class 1.
        double posSum = 0;
        for (int i = 0; i < N; i++){
            posSum += Nij[i][1] / Ni[i];
        }
        Pe += Math.pow(posSum/N, 2);
        return (Pa - Pe)/ (1 - Pe);
    }
}
