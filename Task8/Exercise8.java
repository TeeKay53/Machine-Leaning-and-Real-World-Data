package uk.ac.cam.tp423.MachineLearning.Task8;

import sun.awt.image.ImageWatched;
import uk.ac.cam.cl.mlrwd.exercises.markov_models.*;
import uk.ac.cam.tp423.MachineLearning.Task7.HMMGenerator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * Created by TeeKay on 2/17/2017.
 */
public class Exercise8 implements IExercise8 {
    @Override
    public Map<DiceType, Double> getFinalProbs(List<Path> trainingFiles) throws IOException {
        HMMGenerator<DiceRoll, DiceType> generator = new HMMGenerator<>();
        return generator.generate(HMMDataStore.loadDiceFiles(trainingFiles)).getKey();
    }

    @Override
    public List<DiceType> viterbi(HiddenMarkovModel<DiceRoll, DiceType> model, Map<DiceType, Double> finalProbs, List<DiceRoll> observedSequence) {
        Viterbi<DiceRoll, DiceType> viterbi = new Viterbi<>();
        return viterbi.viterbi(model, finalProbs, observedSequence);
    }

    @Override
    public Map<List<DiceType>, List<DiceType>> predictAll(HiddenMarkovModel<DiceRoll, DiceType> model, Map<DiceType, Double> finalProbs, List<Path> testFiles) throws IOException {
        Map<List<DiceType>, List<DiceType>> all = new HashMap<>();
        for (Path path : testFiles){
            BufferedReader reader = new BufferedReader(new FileReader(path.toFile()));
            char[] observed = reader.readLine().toCharArray();
            char[] hidden = reader.readLine().toCharArray();
            LinkedList<DiceRoll> obL = new LinkedList<>();
            LinkedList<DiceType> hiL = new LinkedList<>();
            for (char c : observed){
                obL.add(DiceRoll.valueOf(c));
            }
            for (char c : hidden){
                hiL.add(DiceType.valueOf(c));
            }
            all.put(hiL, viterbi(model, finalProbs, obL));
        }
        return all;
    }

    @Override
    public double precision(Map<List<DiceType>, List<DiceType>> true2PredictedMap) {
        double correct = 0;
        double predicted = 0;
        for (Map.Entry<List<DiceType>, List<DiceType>> entry : true2PredictedMap.entrySet()){
            Iterator<DiceType> trueIterator = entry.getKey().iterator();
            Iterator<DiceType> predictedIterator = entry.getValue().iterator();
            while (trueIterator.hasNext() && predictedIterator.hasNext()){
                DiceType t = trueIterator.next();
                DiceType p = predictedIterator.next();
                if (p == DiceType.WEIGHTED){
                    predicted++;
                    if (t == DiceType.WEIGHTED){
                        correct++;
                    }
                }
            }
        }

        return correct/predicted;
    }

    @Override
    public double recall(Map<List<DiceType>, List<DiceType>> true2PredictedMap) {
        double correct = 0;
        double trueN = 0;
        for (Map.Entry<List<DiceType>, List<DiceType>> entry : true2PredictedMap.entrySet()){
            Iterator<DiceType> trueIterator = entry.getKey().iterator();
            Iterator<DiceType> predictedIterator = entry.getValue().iterator();
            while (trueIterator.hasNext() && predictedIterator.hasNext()){
                DiceType t = trueIterator.next();
                DiceType p = predictedIterator.next();
                if (t == DiceType.WEIGHTED){
                    trueN++;
                    if (p == DiceType.WEIGHTED){
                        correct++;
                    }
                }
            }
        }

        return correct/trueN;
    }

    @Override
    public double fOneMeasure(Map<List<DiceType>, List<DiceType>> true2PredictedMap) {
       double precision= precision(true2PredictedMap);
       double recall = recall(true2PredictedMap);
       return 2*(precision*recall)/(precision + recall);
    }
}
