package uk.ac.cam.tp423.MachineLearning.Task5;

import uk.ac.cam.cl.mlrwd.exercises.sentiment_detection.DataPreparation1;
import uk.ac.cam.cl.mlrwd.exercises.sentiment_detection.Sentiment;
import uk.ac.cam.tp423.MachineLearning.Task1.sentiment_detection.Exercise1;
import uk.ac.cam.tp423.MachineLearning.Task2.Exercise2;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by TeeKay on 2/3/2017.
 */
public class Naive_Bayes_CrossClassifier {
    static final Path dataDirectory = Paths.get("E:\\Java\\src\\uk\\ac\\cam\\tp423\\MachineLearning\\Task1\\data\\sentiment_dataset");
    public static void main(String[] args) {
        try {

            Exercise5 implementation5 = new Exercise5();
            Path sentimentFile = dataDirectory.resolve("review_sentiment");
            Map<Path, Sentiment> dataSet = DataPreparation1.loadSentimentDataset(dataDirectory.resolve("reviews"),
                    sentimentFile);
            List<Map<Path, Sentiment>> normalSplit = implementation5.splitCVRandom(dataSet, 53);
            List<Map<Path, Sentiment>> stratifiedSplit = implementation5.splitCVStratifiedRandom(dataSet, 49);

            double[] normalAccuracies = implementation5.crossValidate(normalSplit);
            double[] stratifiedAccuracies = implementation5.crossValidate(stratifiedSplit);
            System.out.println("Normal Accuracy:" + implementation5.cvAccuracy(normalAccuracies));
            System.out.println("Stratified Accuracy:" + implementation5.cvAccuracy(stratifiedAccuracies));
            System.out.println("Normal Variance: " + implementation5.cvVariance(normalAccuracies));
            System.out.println("Stratified Variance: " + implementation5.cvVariance(stratifiedAccuracies));


        }

        catch (IOException e){
            e.printStackTrace();
        }
    }
}
