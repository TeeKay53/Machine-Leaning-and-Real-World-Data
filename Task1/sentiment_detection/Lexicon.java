package uk.ac.cam.tp423.MachineLearning.Task1.sentiment_detection;

import uk.ac.cam.cl.mlrwd.exercises.sentiment_detection.Sentiment;

import uk.ac.cam.cl.mlrwd.exercises.sentiment_detection.DataPreparation1;
import uk.ac.cam.cl.mlrwd.exercises.sentiment_detection.IExercise1;
import uk.ac.cam.cl.mlrwd.exercises.sentiment_detection.Sentiment;
import uk.ac.cam.cl.mlrwd.exercises.sentiment_detection.Tokenizer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by TeeKay on 1/20/2017.
 */
public class Lexicon {

    public static Map<String, LexiconEntry> lexicon(Path path){
        try {
            BufferedReader reader = Files.newBufferedReader(path);
            String line = null;
            Map<String, LexiconEntry> wordFeeling = new TreeMap<>();

            int typeIndex = 0;
            int wordIndex = 0;
            int pos1Index = 0;
            int stemmed1Index = 0;
            int polarity = 0;
            int index = 0;
            int neutral = 0;
            while( (line = reader.readLine()) != null){

                String[] lineArray = line.split(" ");
                index = 0;

                while (!lineArray[index].startsWith("type")) index++;
                typeIndex = index;
                while (!lineArray[index].startsWith("word")) index++;
                wordIndex = index;
                while (!lineArray[index].startsWith("pos")) index++;
                pos1Index = index;
                while (!lineArray[index].startsWith("stemm")) index++;
                stemmed1Index = index;
                while (!lineArray[index].startsWith("prio")) index++;
                polarity = index;
//                    LexiconEntry l = new LexiconEntry(lineArray[typeIndex].split("=")[1], lineArray[pos1Index].split("=")[1], lineArray[stemmed1Index].split("=")[1], lineArray[polarity].split("=")[1]);
//                    System.out.println(lineArray[2].split("=")[1]);
//                    l.print();
                    wordFeeling.put(lineArray[wordIndex].split("=")[1], new LexiconEntry(lineArray[typeIndex].split("=")[1], lineArray[pos1Index].split("=")[1], lineArray[stemmed1Index].split("=")[1], lineArray[polarity].split("=")[1]));
//                }
                if (!lineArray[typeIndex].split("=")[1].equals("strongsubj") && !lineArray[typeIndex].split("=")[1].equals("weaksubj")){
                    System.out.println(lineArray[wordIndex].split("=")[1] + " " + lineArray[typeIndex].split("=")[1]);
                }

            }

            return wordFeeling;
        }
        catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    public static void writer(Map<String , LexiconEntry> lex, double versionNumber){
        try {
            BufferedWriter wr = new BufferedWriter(new FileWriter(new File("E:\\Java\\src\\uk\\ac\\cam\\tp423\\MachineLearning\\Task1\\improvedLexicon v" + versionNumber + ".txt")));
            for (String word : lex.keySet()){
                LexiconEntry entry = lex.get(word);
                String feel = "neutral";
                if (entry.getFeeling() == Sentiment.NEGATIVE){
                    feel = "negative";
                }
                else if (entry.getFeeling() == Sentiment.POSITIVE) {
                    feel = "positive";
                }
                wr.write("type=" + entry.getType() + " len=1 word1=" + word + " pos1=" + entry.getPosl() + " stemmed1=" + entry.getStemmed1() + " priorpolarity=" + feel);
                wr.newLine();

            }
            wr.flush();
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }
}
