package uk.ac.cam.tp423.MachineLearning.Task5;

import uk.ac.cam.cl.mlrwd.exercises.sentiment_detection.IExercise5;
import uk.ac.cam.cl.mlrwd.exercises.sentiment_detection.Sentiment;
import uk.ac.cam.tp423.MachineLearning.Task1.sentiment_detection.Exercise1;
import uk.ac.cam.tp423.MachineLearning.Task2.Exercise2;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * Created by TeeKay on 2/3/2017.
 */
public class Exercise5 implements IExercise5 {
    @Override
    public List<Map<Path, Sentiment>> splitCVRandom(Map<Path, Sentiment> dataSet, int seed) {
        LinkedList<Map<Path,Sentiment>> folds = new LinkedList<>();
        Set<Path> keySet = dataSet.keySet();
        //shuffle only wordks on lists
        LinkedList<Path> keyList = new LinkedList();
        keyList.addAll(keySet);
        Collections.shuffle(keyList, new Random(seed));
        int lenghtOfFold = keyList.size()/10;
        int entryIndex =0;
        for (int foldIndex = 0; foldIndex < 10; foldIndex++){
            //create a map for every one, and fill it and put it in
            Map<Path, Sentiment> currentFold = new HashMap<>();
            while (currentFold.size()< lenghtOfFold ){
                currentFold.put(keyList.get(entryIndex),dataSet.get(keyList.get(entryIndex++)) );
            }
            folds.add(currentFold);
        }
        return folds;
    }

    @Override
    public List<Map<Path, Sentiment>> splitCVStratifiedRandom(Map<Path, Sentiment> dataSet, int seed) {
        LinkedList<Map<Path,Sentiment>> folds = new LinkedList<>();
        int lengthOfFold = dataSet.size()/ 10;
        HashSet<Path> wentThrough = new HashSet<>();
        //same problem as previous
        Set<Path> keySet = dataSet.keySet();
        LinkedList<Path> keyList = new LinkedList<>();
        keyList.addAll(keySet);
        Collections.shuffle(keyList, new Random(seed));
        Map<Path, Sentiment> posReviews = new HashMap<>();
        Map<Path, Sentiment> negReviews = new HashMap<>();
        for (Path path : keyList){
            Sentiment currentSent = dataSet.get(path);
            if (currentSent== Sentiment.POSITIVE){
                posReviews.put(path, currentSent);
            }
            else if (currentSent == Sentiment.NEGATIVE){
                negReviews.put(path, currentSent);
            }
        }

        Iterator posIterator = posReviews.entrySet().iterator();
        Iterator negIteraor = negReviews.entrySet().iterator();

        for (int i = 0; i < 10; i++){
            Map<Path, Sentiment> currentFold = new HashMap<>();
            for (int posIndex =0 ; posIndex < lengthOfFold/2; posIndex++){
                Map.Entry<Path, Sentiment> entry = (Map.Entry) posIterator.next();
                currentFold.put(entry.getKey(), entry.getValue());
            }
            for (int negIndex =0 ; negIndex < lengthOfFold/2; negIndex++){
                Map.Entry<Path, Sentiment> entry = (Map.Entry) negIteraor.next();
                currentFold.put(entry.getKey(), entry.getValue());
            }
            folds.push(currentFold);
        }

        return folds;
    }

    @Override
    public double[] crossValidate(List<Map<Path, Sentiment>> folds) throws IOException {
        double[] accuracies = new double[folds.size()];
        Exercise1 implementation1 = new Exercise1();
        Exercise2 implementation2 = new Exercise2();
        int arrayIndex = 0;
        for (int i = 0; i < folds.size(); i++) {
            Map<Path, Sentiment> training = new HashMap<>();
            for (int q = 0; q < folds.size(); q++) {
                if (i != q) {
                    training.putAll(folds.get(q));
                }
            }
            Map<String, Map<Sentiment, Double>> logProbs = implementation2.calculateSmoothedLogProbs(training);
            Map<Sentiment, Double> classProbs = implementation2.calculateClassProbabilities(training);

            Map<Path, Sentiment> nbPredictions = implementation2.naiveBayes(folds.get(i).keySet(), logProbs, classProbs);
            double thisAccuracy = implementation1.calculateAccuracy(folds.get(i), nbPredictions);
            accuracies[arrayIndex++] = thisAccuracy;
        }
        return accuracies;
    }

    @Override
    public double cvAccuracy(double[] scores) {
        double average = 0;
        for (double i : scores){
            average += i;
        }
        return average/scores.length;
    }

    @Override
    public double cvVariance(double[] scores) {
        double variance = 0;
        double average = cvAccuracy(scores);
        for (double i : scores){
            variance += Math.pow(average - i, 2);
        }
        return variance/scores.length;
    }
}
