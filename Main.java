import java.util.List;

public class Main {
    public static void main(String[] args) {
        Graph graph = new Graph();
        GeneticAlgorithm gen = new GeneticAlgorithm(graph, 200, .05, "Baia Mare");
        List<Route> bestRoutes = gen.findBestRoutes(200);
        gen.printBestRoutes(bestRoutes, graph, 10);
    }
}

//Just main function
//Outputs in a file called output.txt