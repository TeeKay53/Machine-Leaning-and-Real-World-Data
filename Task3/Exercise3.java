package uk.ac.cam.tp423.MachineLearning.Task3;

import uk.ac.cam.cl.mlrwd.exercises.sentiment_detection.DataPreparation1;
import uk.ac.cam.cl.mlrwd.exercises.sentiment_detection.Tokenizer;
import uk.ac.cam.cl.mlrwd.utils.BestFit;
import uk.ac.cam.tp423.Algorithms.MapUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * Created by TeeKay on 1/27/2017.
 */
public class Exercise3 {
    private Map<String, Double> occurrences;

    private List<BestFit.Point> pointData;
    private BestFit.Line fitLine;

    public Exercise3(List<Path> textInput) throws IOException{
        occurrences = wordOccurrences(textInput);
    }

    private Map<String, Double> wordOccurrences(List<Path> textInput) throws IOException{
        try {
            Map<String, Double> occurrences = new HashMap<>();

            for (Path path : textInput) {
                List<String> text = Tokenizer.tokenize(path);
                for (String word : text){
                    occurrences.put(word, occurrences.getOrDefault(word, 0d) + 1);

                }
            }
            return occurrences;
        }
        catch (IOException e){
            throw  new IOException("Can't find path", e);
        }
        }

    public Map<String, Double> ziphLaw(double k, double alpha) {

        Map<String, Double> rankedWords = MapUtil.sortByValue(occurrences);
        Iterator it = occurrences.entrySet().iterator();
        int rank = 0;
        while (it.hasNext()) {

            Map.Entry<String, Double> entry = (Map.Entry<String, Double>) it.next();

            entry.setValue(k / Math.pow(++rank, alpha));
        }
        return rankedWords;
    }



    public List<BestFit.Point> getLogData(){
        if (pointData == null) {
            pointData = generateLogData(occurrences);
        }
        return pointData;
    }

    public List<BestFit.Point> generateLogData(Map<String, Double> wordOccurences){
        Map<String, Double> source = MapUtil.sortByValue(occurrences);
        List<BestFit.Point> data = new LinkedList<>();
        double rank = 0;
        Iterator it = source.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Double> entry = (Map.Entry) it.next();
            rank++;
            String word = entry.getKey();
            data.add(new BestFit.Point(Math.log(rank), Math.log(entry.getValue())));
            if (word.equals("satisfying") || word.equals("exploiting") ||word.equals("genuine") ||word.equals("dimensional") ||word.equals("potential") ||
                    word.equals("lacking") ||word.equals("fun") ||word.equals("interesting") ||word.equals("unfortunatelly") ||word.equals("exciting")){
                System.out.println(word + ": " + rank + "--->" + wordOccurences.get(word));
            }
            if (rank > 10000) break;
        }
        return data;
    }

    public BestFit.Line getFittedLine(){
        if (fitLine == null ) {
            fitLine = generateFittedLine(getLogData());
        }
        return fitLine;
    }

    public BestFit.Line generateFittedLine(List<BestFit.Point> data){

        Map<BestFit.Point, Double> function = new HashMap<>();
        for (BestFit.Point point : data) {
            function.put(point, Math.exp(point.y));
        }
        return BestFit.leastSquares(function);
    }

    public Map<String, Double> getOccurrences(){return occurrences;}

   public double exprectedFrequency(int rank){
        getLogData();
        getFittedLine();
        return Math.exp(((Math.log(rank) - fitLine.yIntercept) / fitLine.gradient));
   }


   public void constants(){
        System.out.println("K: " + Math.exp(getFittedLine().yIntercept));
        System.out.println("Alpha: " + fitLine.gradient);
    }

    public static List<BestFit.Point> tokensAndTypes(List<Path> textInput)throws IOException{

            List<BestFit.Point> tokensType = new LinkedList<>();
            HashSet<String> types = new HashSet<>();
            long tokens = 0;
            long currentPower  = 1;
            for (Path path : textInput) {
                List<String> text = Tokenizer.tokenize(path);
                for (String word : text){
                    tokens++;
                    if (!types.contains(word)) {
                        types.add(word);
                        if (tokens > 8000000) System.out.println(word);
                    }
                    if (tokens == currentPower){
                        tokensType.add(new BestFit.Point( Math.log(tokens),Math.log(types.size())));
                        currentPower *= 2;
                    }

                }
            }
            tokensType.add(new BestFit.Point( Math.log(tokens),Math.log(types.size())));
            return tokensType;


    }
}
