package uk.ac.cam.tp423.MachineLearning.Task10;

import uk.ac.cam.cl.mlrwd.exercises.social_networks.IExercise10;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.file.Path;
import java.util.*;

/**
 * Created by TeeKay on 2/27/2017.
 */
public class Exercise10 implements IExercise10 {
    @Override
    public Map<Integer, Set<Integer>> loadGraph(Path graphFile) throws IOException {
        Map<Integer, Set<Integer>> graph = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(graphFile.toFile()));
        String line = reader.readLine();
        while (line != null){
            String[] edge = line.split(" ");
            int first = Integer.parseInt(edge[0]);
            int second = Integer.parseInt(edge[1]);

            if (graph.keySet().contains(first)){
                graph.get(first).add(second);
            }
            else {
                Set<Integer> toPut = new HashSet<>();
                toPut.add(second);
                graph.put(first, toPut);
            }

            //Make it undirected

            first = second;
            second = Integer.parseInt(edge[0]);

            if (graph.keySet().contains(first)){
                graph.get(first).add(second);
            }
            else {
                Set<Integer> toPut = new HashSet<>();
                toPut.add(second);
                graph.put(first, toPut);
            }
            line = reader.readLine();


        }
        reader.close();
        return graph;
    }

    @Override
    public Map<Integer, Integer> getConnectivities(Map<Integer, Set<Integer>> graph) {
        Map<Integer, Integer> connectivities = new HashMap<>();
        for (Map.Entry<Integer, Set<Integer>> entry : graph.entrySet()){
            connectivities.put(entry.getKey(), entry.getValue().size());
        }

        return connectivities;
    }

    @Override
    public int getDiameter(Map<Integer, Set<Integer>> graph) {
        int maxDistance = 0;
        for (Integer i : graph.keySet()){
            int currentDistance = getLocalMax(graph, i);
            maxDistance = Math.max(maxDistance, currentDistance);
        }
        return maxDistance;
    }

    private int getLocalMax(Map<Integer, Set<Integer>> graph, Integer startNode){

        Set<Integer> seen = new HashSet<>();
        //current exploring keeps track of the nodes for which we are currently visiting the neighbours, which we are storing in wilExplore
        LinkedList<Integer> currentExploring = new LinkedList<>();
        LinkedList<Integer> willExplore = new LinkedList<>();

        seen.add(startNode);
        willExplore.add(startNode);
        //need this because you need value 0 if the while loop stops after 1 iteration
        int distance = -1;

        while (!willExplore.isEmpty()){
            distance++;
            currentExploring = willExplore;
            willExplore = new LinkedList<>();

            while (!currentExploring.isEmpty()){
                Integer current = currentExploring.pop();

                for (Integer neighbour : graph.get(current)){
                    if (seen.contains(neighbour)) continue;
                    willExplore.add(neighbour);
                    seen.add(neighbour);
                }
            }
        }

        return distance ;

    }
}
