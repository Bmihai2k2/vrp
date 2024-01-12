import java.util.*;

public class Graph {

    private int[][] adjacencyMatrix;
    private String[] vertices;
    private List<String> currentPath;
    private Random rand;

    //We prefered to use an predifined graph
    //From testing and optimisation, it has a success rate of about 80%, depends on the depot city

    public Graph() {
        this.vertices = new String[] {"Alba Iulia", "Arad", "Bacau", "Baia Mare", "Bistrita", "Botosani", "Brasov",
                "Cluj-Napoca", "Deva", "Iasi", "Miercurea Ciuc", "Oradea", "Piatra Neamt", "Satu Mare", "Sibiu",
                "Suceava", "Targu Mures", "Zalau"}; // 18 nodes
        this.adjacencyMatrix = new int[vertices.length][vertices.length];

        // Initialize the adjacency matrix with zeros (no edges)
        for (int i = 0; i < vertices.length; i++) {
            for (int j = 0; j < vertices.length; j++) {
                adjacencyMatrix[i][j] = 0;
            }
        }

        // Adding edges with distances - 31 edges
        addEdge("Satu Mare", "Baia Mare", 68);
        addEdge("Satu Mare", "Zalau", 90);
        addEdge("Satu Mare", "Oradea", 133);
        addEdge("Baia Mare", "Suceava", 338);
        addEdge("Baia Mare", "Bistrita", 147);
        addEdge("Baia Mare", "Zalau", 86);
        addEdge("Baia Mare", "Cluj-Napoca", 148);
        addEdge("Suceava", "Botosani", 42);
        addEdge("Suceava", "Iasi", 144);
        addEdge("Suceava", "Piatra Neamt", 100);
        addEdge("Suceava", "Miercurea Ciuc", 235);
        addEdge("Suceava", "Targu Mures", 283);
        addEdge("Suceava", "Bistrita", 192);
        addEdge("Oradea", "Zalau", 115);
        addEdge("Oradea", "Cluj-Napoca", 152);
        addEdge("Oradea", "Alba Iulia", 245);
        addEdge("Oradea", "Deva", 191);
        addEdge("Oradea", "Arad", 118);
        addEdge("Zalau", "Cluj-Napoca", 84);
        addEdge("Cluj-Napoca", "Bistrita", 110);
        addEdge("Cluj-Napoca", "Targu Mures", 111);
        addEdge("Cluj-Napoca", "Alba Iulia", 98);
        addEdge("Bistrita", "Targu Mures", 91);
        addEdge("Targu Mures", "Miercurea Ciuc", 139);
        addEdge("Targu Mures", "Brasov", 170);
        addEdge("Targu Mures", "Sibiu", 115);
        addEdge("Targu Mures", "Alba Iulia", 120);
        addEdge("Miercurea Ciuc", "Piatra Neamt", 138);
        addEdge("Miercurea Ciuc", "Bacau", 139);
        addEdge("Brasov", "Sibiu", 147);
        addEdge("Piatra Neamt", "Bacau", 139);

        this.currentPath = new ArrayList<>();

    }

    //Setters and getters
    public List<String> getCurrentPath() {
        return new ArrayList<>(currentPath); // Return a copy of the current path
    }

    public void setCurrentPath(List<String> path) {
        this.currentPath = path;
    }

    public Pair<String, String> getPair(String depot, String anotherCity) {

        if (depot != anotherCity)
            return new Pair<String, String>(depot, anotherCity);
        return null;

    }

    public int[][] getAdjacencyMatrix(){
        return adjacencyMatrix;
    }

    public String[] getVertices() {
        // Convert the array to a list and return
        return vertices;
    }
    //end Setters and getters

    //Adding edge method
    public void addEdge(String vertex1, String vertex2, int distance) {
        int index1 = Arrays.asList(vertices).indexOf(vertex1);
        int index2 = Arrays.asList(vertices).indexOf(vertex2);

        if (index1 != -1 && index2 != -1) {
            adjacencyMatrix[index1][index2] = distance;
            adjacencyMatrix[index2][index1] = distance; // For undirected graph
        }
    }

    //This is for generation a random path between 2 cities
    public Pair<Integer, Integer> getDistance(String startCity, String endCity) {
        int startIndex = Arrays.asList(vertices).indexOf(startCity);
        int endIndex = Arrays.asList(vertices).indexOf(endCity);

        // Clear the current path for a new calculation
        currentPath.clear();

        // Check if both cities are valid
        if (startIndex == -1 || endIndex == -1) {
            return new Pair<>(-1, -1); // Invalid cities
        }

        rand = new Random();
        int totalDistance = 0;
        int current = startIndex;

        // Add the start city to the path
        currentPath.add(vertices[current]);

        while (current != endIndex) {
            // Find adjacent cities
            List<Integer> adjacentCities = new ArrayList<>();
            for (int i = 0; i < vertices.length; i++) {
                if (adjacencyMatrix[current][i] > 0 && i != current) {
                    adjacentCities.add(i);
                }
            }

            if (adjacentCities.isEmpty()) {
                // No path exists from the current city, return -1
                return new Pair<>(-1, -1);
            }

            // Pick a random adjacent city
            int nextIndex = adjacentCities.get(rand.nextInt(adjacentCities.size()));
            totalDistance += adjacencyMatrix[current][nextIndex];
            current = nextIndex;

            // Add the next city to the path
            currentPath.add(vertices[current]);
        }

        setCurrentPath(currentPath);

        return new Pair<>(totalDistance, currentPath.size() - 1);
    }

    //Checking validation of a path
    public boolean validateRoute(List<String> path) {
        if (path == null || path.size() < 2) {
            return false; // A valid path should have at least two cities
        }
        for (int i = 0; i < path.size() - 1; i++) {
            String city1 = path.get(i);
            String city2 = path.get(i + 1);

            int index1 = Arrays.asList(vertices).indexOf(city1);
            int index2 = Arrays.asList(vertices).indexOf(city2);

            // Check if both cities are valid and if there is a direct path between them
            if (index1 == -1 || index2 == -1 || adjacencyMatrix[index1][index2] == 0) {
                return false; // No direct path between these cities
            }
        }
        return true; // The route is valid and exists in the graph
    }
    
}
