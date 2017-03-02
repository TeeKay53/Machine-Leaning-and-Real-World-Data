package uk.ac.cam.tp423.MachineLearning.Task7;

import uk.ac.cam.cl.mlrwd.exercises.markov_models.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * Created by TeeKay on 2/13/2017.
 */
public class Exercise7 implements IExercise7 {
    @Override
    public HiddenMarkovModel<DiceRoll, DiceType> estimateHMM(Collection<Path> sequenceFiles) throws IOException {
        HMMGenerator<DiceRoll, DiceType> generator = new HMMGenerator<>();
        return generator.generate(HMMDataStore.loadDiceFiles(sequenceFiles)).getValue();
    }
}
