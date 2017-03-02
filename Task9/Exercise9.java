package uk.ac.cam.tp423.MachineLearning.Task9;

import uk.ac.cam.cl.mlrwd.exercises.markov_models.*;
import uk.ac.cam.tp423.MachineLearning.Task7.HMMGenerator;
import uk.ac.cam.tp423.MachineLearning.Task8.Viterbi;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by TeeKay on 2/20/2017.
 */
public class Exercise9 implements IExercise9 {
    @Override
    public HiddenMarkovModel<AminoAcid, Feature> estimateHMM(List<HMMDataStore<AminoAcid, Feature>> sequencePairs) throws IOException {
        HMMGenerator<AminoAcid, Feature> generator = new HMMGenerator<>();
        return generator.generate(sequencePairs).getValue();
    }

    @Override
    public Map<Feature, Double> getFinalProbs(List<HMMDataStore<AminoAcid, Feature>> trainingPairs) throws IOException {
        HMMGenerator<AminoAcid, Feature> generator = new HMMGenerator<>();
        return generator.generate(trainingPairs).getKey();
    }

    @Override
    public List<Feature> viterbi(HiddenMarkovModel<AminoAcid, Feature> model, Map<Feature, Double> finalProbs, List<AminoAcid> observedSequence) {
        Viterbi<AminoAcid, Feature> viterbi = new Viterbi<>();
        return viterbi.viterbi(model, finalProbs, observedSequence);
    }

    @Override
    public Map<List<Feature>, List<Feature>> predictAll(HiddenMarkovModel<AminoAcid, Feature> model, Map<Feature, Double> finalProbs, List<HMMDataStore<AminoAcid, Feature>> testSequencePairs) throws IOException {
        Map<List<Feature>, List<Feature>> all = new HashMap<>();
        for (HMMDataStore<AminoAcid, Feature> data: testSequencePairs){
            List<Feature> trueL = data.hiddenSequence;
            List<Feature> predictedL = viterbi(model, finalProbs, data.observedSequence);
            all.put(trueL, predictedL);
        }
        return all;
    }

    @Override
    public double precision(Map<List<Feature>, List<Feature>> true2PredictedMap) {
        double correct = 0;
        double predicted = 0;
        for (Map.Entry<List<Feature>, List<Feature>> entry : true2PredictedMap.entrySet()){
            Iterator<Feature> trueIterator = entry.getKey().iterator();
            Iterator<Feature> predictedIterator = entry.getValue().iterator();
            while (trueIterator.hasNext() && predictedIterator.hasNext()){
                Feature t = trueIterator.next();
                Feature p = predictedIterator.next();
                if (p == Feature.MEMBRANE){
                    predicted++;
                    if (t == Feature.MEMBRANE){
                        correct++;
                    }
                }
            }
        }

        return correct/predicted;
    }

    @Override
    public double recall(Map<List<Feature>, List<Feature>> true2PredictedMap) {
        double correct = 0;
        double predicted = 0;
        for (Map.Entry<List<Feature>, List<Feature>> entry : true2PredictedMap.entrySet()){
            Iterator<Feature> trueIterator = entry.getKey().iterator();
            Iterator<Feature> predictedIterator = entry.getValue().iterator();
            while (trueIterator.hasNext() && predictedIterator.hasNext()){
                Feature t = trueIterator.next();
                Feature p = predictedIterator.next();
                if (t == Feature.MEMBRANE){
                    predicted++;
                    if (p == Feature.MEMBRANE){
                        correct++;
                    }
                }
            }
        }

        return correct/predicted;
    }

    @Override
    public double fOneMeasure(Map<List<Feature>, List<Feature>> true2PredictedMap) {
        double precision= precision(true2PredictedMap);
        double recall = recall(true2PredictedMap);
        return 2*(precision*recall)/(precision + recall);
    }

    public static void main(String[] args) {
        System.out.println();
    }
}


