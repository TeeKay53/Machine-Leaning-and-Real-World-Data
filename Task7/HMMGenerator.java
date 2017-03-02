package uk.ac.cam.tp423.MachineLearning.Task7;

import uk.ac.cam.cl.mlrwd.exercises.markov_models.HMMDataStore;
import uk.ac.cam.cl.mlrwd.exercises.markov_models.HiddenMarkovModel;

import java.util.*;

/**
 * Created by TeeKay on 2/15/2017.
 */
public class HMMGenerator<T, U> {
    //U -> states
    //T -> emissions

    public Map.Entry<Map<U, Double>, HiddenMarkovModel<T, U>> generate(List<HMMDataStore<T, U>> data){
        Map<U, Map<U, Double>> transitions = new HashMap<U, Map<U, Double>>();
        Map<U, Map<T, Double>> emissions = new HashMap<U, Map<T, Double>>();
        Map<U, Double> stateProbs = new HashMap<U, Double>();
        Map<U, Double> finalProbs = new HashMap<U, Double>();
        int count = 0;

        for (HMMDataStore dataStore : data) {

            Iterator observedIterator = dataStore.observedSequence.iterator();
            Iterator hiddenIterator = dataStore.hiddenSequence.iterator();

            T thisObserved = (T) observedIterator.next();
            U lastHidden = (U) hiddenIterator.next();
            stateProbs.put(lastHidden, stateProbs.getOrDefault(lastHidden, 0d) + 1);
            Map<T, Double> value = new HashMap<T, Double>();
            if (emissions.containsKey(lastHidden)){
                value = emissions.get(lastHidden);
                value.put(thisObserved, value.getOrDefault(thisObserved, 0d) + 1);
            }
            else {
                value.put(thisObserved, 1d);
                emissions.put(lastHidden, value);
            }

            while (observedIterator.hasNext() && hiddenIterator.hasNext()){

                thisObserved = (T) observedIterator.next();
                U thisHidden =(U) hiddenIterator.next();
                count++;
                if (emissions.containsKey(thisHidden)){
                    emissions.get(thisHidden).put(thisObserved, emissions.get(thisHidden).getOrDefault(thisObserved, 0d) + 1);
                }
                else {
                    value = new HashMap<T, Double>();
                    value.put(thisObserved, 1d);
                    emissions.put(thisHidden, value);
                }
                if (transitions.containsKey(lastHidden)){
                    Map<U, Double> s = transitions.get(lastHidden);
                    s.put(thisHidden, s.getOrDefault(thisHidden, 0d) + 1);
                }
                else {
                    Map<U, Double> s = new HashMap<U, Double>();
                    s.put(thisHidden, 1d);
                    transitions.put(lastHidden, s);
                }
                lastHidden = thisHidden;
            }
            finalProbs.put(lastHidden, finalProbs.getOrDefault(lastHidden, 0d) + 1);

        }
        int finalTotal =0;
        int initialTotal = 0;
        int total;

        for (U type : transitions.keySet()){
            total = 0;

            Map<U, Double> trans = transitions.get(type);
            for (U v : trans.keySet()){
                total += trans.get(v);
            }
            for (U v : trans.keySet()){
                trans.put(v, trans.get(v) / total);
            }

            total = 0;
            Map<T, Double> emm = emissions.get(type);
            for (T v : emm.keySet()){
                total += emm.get(v);
            }
            for (T v : emm.keySet()){
                emm.put(v, emm.get(v)/total);
            }
            //sometimes type does not occur in initial
            initialTotal += stateProbs.getOrDefault(type, 0d);
            finalTotal += finalProbs.getOrDefault(type, 0d);
        }

        for (U type : stateProbs.keySet()){
            stateProbs.put(type, stateProbs.get(type) / initialTotal);
        }
        for(U type :finalProbs.keySet()){
            finalProbs.put(type, finalProbs.get(type)/finalTotal);
        }
        Map.Entry<Map<U, Double>, HiddenMarkovModel<T, U>> toRet = new Map.Entry<Map<U, Double>, HiddenMarkovModel<T, U>>() {
            @Override
            public Map<U, Double> getKey() {
                return finalProbs;
            }

            @Override
            public HiddenMarkovModel<T, U> getValue() {
                return new HiddenMarkovModel<T, U>(transitions, emissions, stateProbs);
            }

            @Override
            public HiddenMarkovModel<T, U> setValue(HiddenMarkovModel<T, U> value) {
                return null;
            }
        };
        return toRet;
    }


}
