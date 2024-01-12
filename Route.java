import java.util.Arrays;
import java.util.List;

public class Route {

    private final double MAX_WORKING_TIME = 8.0 * 60.0; // 8 hours in minutes

    private Graph graph;
    private Pair<String, String> cities;
    private int totalDistance;
    private int routeLenght;
    private double totalTime;
    private List<String> path;

    public Route(Pair<String, String> cities, Graph graph) {
        this.graph = graph;
        Pair<Integer, Integer> aux = this.graph.getDistance(cities.first, cities.second);
        while (calculateTotalTime(aux.first) > MAX_WORKING_TIME && aux.second > graph.getVertices().length / 3) {
            aux = this.graph.getDistance(cities.first, cities.second);
        }
        this.totalDistance = aux.first;
        this.routeLenght = aux.second;
        this.cities = cities;
        this.totalTime = calculateTotalTime(aux.first);
        this.path = this.graph.getCurrentPath();
    }

    public Route(List<String> newPath, Graph graph) {
        this.graph = graph;
        this.path = newPath;
        this.cities = new Pair<String, String>(newPath.get(0), newPath.get(newPath.size() - 1));
        calculateTotalDistanceAndLength();
        this.totalTime = calculateTotalTime(this.totalDistance);
    }

    // Setters and getters
    public double getMAX_WORKING_TIME() {
        return MAX_WORKING_TIME;
    }

    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> Path) {
        this.path = Path;
    }

    public int getRouteLenght() {
        return routeLenght;
    }

    public int getTotalDistance() {
        return totalDistance;
    }

    public Pair<String, String> getCities() {
        return cities;
    }

    public void setCities(String City1, String City2) {
        cities = new Pair<String, String>(City1, City2);
    }

    public double getTotalTime() {
        return totalTime;
    }

    private double calculateTotalTime(int dist) {
        if (dist == -1) {
            return -1;
        }
        return (double) (((double) dist / 80 * 60) + (double) (routeLenght * 20));
    }

    //Fitness
    public double getFitness() {
        return 1.0 / totalTime;
    }

    //To calculate total distance and length of it, if 2nd constructor is used (with path input)
    private void calculateTotalDistanceAndLength() {
        this.totalDistance = 0;
        this.routeLenght = 0;

        for (int i = 0; i < path.size() - 1; i++) {
            String city1 = path.get(i);
            String city2 = path.get(i + 1);

            int index1 = Arrays.asList(graph.getVertices()).indexOf(city1);
            int index2 = Arrays.asList(graph.getVertices()).indexOf(city2);

            if (index1 == -1 || index2 == -1 || graph.getAdjacencyMatrix()[index1][index2] == 0) {
                throw new IllegalArgumentException("Invalid path: No direct path between " + city1 + " and " + city2);
            }

            this.totalDistance += graph.getAdjacencyMatrix()[index1][index2];
            this.routeLenght++;
        }
    }

}