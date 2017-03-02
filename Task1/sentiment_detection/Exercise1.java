package uk.ac.cam.tp423.MachineLearning.Task1.sentiment_detection;

import uk.ac.cam.cl.mlrwd.exercises.sentiment_detection.DataPreparation1;
import uk.ac.cam.cl.mlrwd.exercises.sentiment_detection.IExercise1;
import uk.ac.cam.cl.mlrwd.exercises.sentiment_detection.Sentiment;
import uk.ac.cam.cl.mlrwd.exercises.sentiment_detection.Tokenizer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by TeeKay on 1/20/2017.
 */
public class Exercise1 implements IExercise1 {


    //    public Map<String, LexiconEntry> lexi =Lexicon.lexicon(Paths.get("E:\\newLexicon.txt"));
    private Map<String, LexiconEntry> lexi = Lexicon.lexicon(Paths.get("E:\\Java\\src\\uk\\ac\\cam\\tp423\\MachineLearning\\Task1\\data\\sentiment_lexicon.txt"));
    private Map<Integer, String> keyNumbers;

    public Exercise1() {
        keyNumbers = new HashMap<>();
        int index = 0;
        for (String word : lexi.keySet()) {
            keyNumbers.put(index++, word);
        }
    }

    @Override
    public Map<Path, Sentiment> simpleClassifier(Set<Path> testSet, Path lexiconFile) throws IOException {


        Map<Path, Sentiment> sentiments = new HashMap<>();
        for (Path path : testSet) {
            List<String> currentReview = Tokenizer.tokenize(path);
            int score = 0;
            for (String word : currentReview) {
                if (lexi.containsKey(word)) {
                    if (lexi.get(word).getFeeling() == Sentiment.NEGATIVE) {
                        score--;
                    } else if (lexi.get(word).getFeeling() == Sentiment.POSITIVE) {
                        score++;
                    }
                }
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
    public double calculateAccuracy(Map<Path, Sentiment> trueSentiments, Map<Path, Sentiment> predictedSentiments) {
        double accurate = 0;
        int checked = 0;
        for (Path path : predictedSentiments.keySet()) {
            if (trueSentiments.containsKey(path)) {
                checked++;
                if (trueSentiments.get(path) == predictedSentiments.get(path)){
                    accurate++;
                }
            }
        }
        return accurate / checked;
    }

    @Override
    public Map<Path, Sentiment> improvedClassifier(Set<Path> testSet, Path lexiconFile) throws IOException {

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
                    else if (lexi.get(word).getType().equals("weaksubj")) strenght = 0.5;
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

    public Map<Path, Sentiment> simpleEasy(Set<Path> testSet) throws IOException {
        return simpleClassifier(testSet, Paths.get("E:\\Java\\src\\uk\\ac\\cam\\tp423\\MachineLearning\\Task1\\data\\sentiment_lexicon.txt"));
    }

    public Map<Path, Sentiment> improvedEasy(Set<Path> testSet) throws IOException {
        return improvedClassifier(testSet, Paths.get("E:\\Java\\src\\uk\\ac\\cam\\tp423\\MachineLearning\\Task1\\data\\sentiment_lexicon.txt"));
    }


    public double randomLexi(Set<Path> testSet, int maxChanges, int maxTries) throws IOException {
        Path dataDirectory = Paths.get("E:\\Java\\src\\uk\\ac\\cam\\tp423\\MachineLearning\\Task1\\data\\sentiment_dataset");
        Path sentimentFile = dataDirectory.resolve("review_sentiment");
        Path reviewsDir = dataDirectory.resolve("reviews");
        Map<Path, Sentiment> dataSet = DataPreparation1.loadSentimentDataset(reviewsDir, sentimentFile);

        double currentAccuracy = calculateAccuracy(dataSet, simpleEasy(dataSet.keySet()));

        Set<Integer> changed = new HashSet<>();
        Set<Integer> neutral = new HashSet<>();
        int tryNumber = 0;
        int nrOfChanges;
        int change = 0;
        int lexiSize = lexi.keySet().size();

        while (tryNumber++ < maxTries) {
            nrOfChanges = (int) (Math.random() * maxChanges);

            for (int i = 0; i < nrOfChanges; i++) {
                do {
                    change = (int) (Math.random() * lexiSize);

                } while (changed.contains(change));
                changed.add(change);
            }

            LexiconEntry holder = null;
            for (Integer i : changed) {
                holder = lexi.get(keyNumbers.get(i));
                if (holder.getFeeling() == null) neutral.add(i);
                holder.changeFeeling();
            }
            double newAccuracy = calculateAccuracy(dataSet, simpleEasy(dataSet.keySet()));
            if (newAccuracy > currentAccuracy) {
                currentAccuracy = newAccuracy;
            } else {

                for (Integer i : changed) {
                    if (neutral.contains(i)) {
                        lexi.get(keyNumbers.get(i)).setFeeling(null);
                    } else {
                        lexi.get(keyNumbers.get(i)).changeFeeling();
                    }
                }
            }
            changed.clear();
            neutral.clear();
            System.out.println("Attempt " + tryNumber + ": " + newAccuracy);
            if (tryNumber % 10 == 0) {
                System.out.println("Highest accuracy: " + currentAccuracy);
            }
        }
        return currentAccuracy;
    }

    public double randomType(Set<Path> testSet, int maxChanges, int maxTries) throws IOException {

        Path dataDirectory = Paths.get("E:\\Java\\src\\uk\\ac\\cam\\tp423\\MachineLearning\\Task1\\data\\sentiment_dataset");
        Path sentimentFile = dataDirectory.resolve("review_sentiment");
        Path reviewsDir = dataDirectory.resolve("reviews");
        Map<Path, Sentiment> dataSet = DataPreparation1.loadSentimentDataset(reviewsDir, sentimentFile);

        double currentAccuracy = calculateAccuracy(dataSet, improvedEasy(dataSet.keySet()));

        Set<Integer> changed = new HashSet<>();

        int tryNumber = 0;
        int nrOfChanges;
        int change = 0;
        int lexiSize = lexi.keySet().size();

        while (tryNumber++ < maxTries) {
            nrOfChanges = (int) (Math.random() * maxChanges);

            for (int i = 0; i < nrOfChanges; i++) {
                do {
                    change = (int) (Math.random() * lexiSize);

                } while (changed.contains(change));
                changed.add(change);
            }

            for (Integer i : changed) {
                lexi.get(keyNumbers.get(i)).changeType();
            }
            double newAccuracy = calculateAccuracy(dataSet, improvedEasy(dataSet.keySet()));
            if (newAccuracy > currentAccuracy) {
                currentAccuracy = newAccuracy;
            } else {
                System.out.println(changed.size());
                for (Integer i : changed) {
                    lexi.get(keyNumbers.get(i)).changeType();
                }
            }
            changed.clear();
            System.out.println("Attempt " + tryNumber + ": " + newAccuracy);

            if (tryNumber % 10 == 0) {
                System.out.println("Highest accuracy: " + currentAccuracy);
            }

        }
        return currentAccuracy;
    }

    public static void main(String[] args) {
        try {
            Exercise1 gPig = new Exercise1();
            Path dataDirectory = Paths.get("E:\\Java\\src\\uk\\ac\\cam\\tp423\\MachineLearning\\Task1\\data\\sentiment_dataset");
            Path sentimentFile = dataDirectory.resolve("review_sentiment");
            Path reviewsDir = dataDirectory.resolve("reviews");

            Map<Path, Sentiment> dataSet = DataPreparation1.loadSentimentDataset(reviewsDir, sentimentFile);


                gPig.randomLexi(dataSet.keySet(), 50, 300);


            Lexicon.writer(gPig.lexi, 1.1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}