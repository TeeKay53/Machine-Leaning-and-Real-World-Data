package uk.ac.cam.tp423.MachineLearning.Task8;

import uk.ac.cam.cl.mlrwd.exercises.markov_models.HiddenMarkovModel;

import java.util.*;

/**
 * Created by TeeKay on 2/17/2017.
 */
public class Viterbi<T, U> {
    //T = emission
    //U = state

    public List<U> viterbi(HiddenMarkovModel<T, U> model,Map<U, Double> finalProbs ,List<T> observedSequence) {
        Map<U, Map<U, Double>> transitions = model.getTransitionMatrix();
        Map<U, Map<T, Double>> emissions = model.getEmissionMatrix();
        Map<U, Double> initialProbs = model.getInitialProbs();

        LinkedList<Map<U, U>> psi = new LinkedList<>();
        LinkedList<Map<U, Double>> probs = new LinkedList<>();

        Map<U, Double> iniProbs = new HashMap<U, Double>();

        Iterator observer = observedSequence.iterator();
        T observation =(T) observer.next();
        for (U type : emissions.keySet()){
            iniProbs.put(type, Math.log(initialProbs.getOrDefault(type, 0d)) + Math.log(emissions.get(type).getOrDefault(observation, 0d)));
        }
        probs.add(iniProbs);
        Map<U, Double> lastProbs = iniProbs;

        while (observer.hasNext()){
            Map<U, U> currentPsi = new HashMap<U, U>();
            Map<U, Double> currentProbs = new HashMap<U, Double>();

            observation = (T) observer.next();
            for (U now : lastProbs.keySet()){
                U biggestType = null;
                double biggestProb = Double.NEGATIVE_INFINITY;
                for (U previous : lastProbs.keySet()){
                    double prob = lastProbs.get(previous)+ Math.log(transitions.get(previous).getOrDefault(now, 0d)) + Math.log(emissions.get(now).getOrDefault(observation, 0d));
                    if (prob > biggestProb){
                        biggestProb = prob;
                        biggestType = previous;
                    }
                }
                currentProbs.put(now, biggestProb);
                currentPsi.put(now, biggestType);
            }
            psi.addFirst(currentPsi);
            probs.add(currentProbs);
            lastProbs = currentProbs;
        }
        //check which one has the biggest prob of ending
        U endState = null;
        double biggestProb = Double.NEGATIVE_INFINITY;
        for (U state : lastProbs.keySet()){
            double prob = lastProbs.get(state) + Math.log(finalProbs.getOrDefault(state, 0d));
            if (prob > biggestProb){
                biggestProb = prob;
                endState = state;
            }
        }
        LinkedList<U> predictions = new LinkedList<U>();
        predictions.add(endState);
        for (Map<U, U> next : psi){
            endState = next.get(endState);
            predictions.addFirst(endState);
        }

        return predictions;
    }
}
