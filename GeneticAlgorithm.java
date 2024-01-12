import java.util.*;
import java.util.stream.Collectors;
import java.io.PrintWriter;
import java.io.FileNotFoundException;

public class GeneticAlgorithm {

    private final int MIN_ROUTE_LENGTH = 3;
    private List<Route> population;
    private Graph graph;
    private int populationSize;
    private double mutationRate;
    private String depotName;
    private Random rand = new Random();

    public GeneticAlgorithm(Graph graph, int populationSize, double mutationRate, String depot) {
        this.graph = graph;
        this.populationSize = populationSize;
        this.mutationRate = mutationRate;
        this.depotName = depot;
        this.population = initializePopulation();
    }

    //Checks of a route is valid
    private boolean isValid(Route route) {
        if (route.getTotalTime() > route.getMAX_WORKING_TIME())
            return false;
        if (route.getCities().first != depotName)
            return false;
        List<String> path = route.getPath();
        for (int i = 1; i < path.size(); i++) {
            if (path.get(i).equals(depotName)) {
                return false;
            }
        }
        boolean hasDuplicates = path.stream()
                .collect(Collectors.groupingBy(e -> e, Collectors.counting()))
                .values()
                .stream()
                .anyMatch(count -> count > 1);
        if (hasDuplicates)
            return false;
        return true;
    }

    //Checks if 2 routes overlap
    private boolean doRoutesOverlap(Route route1, Route route2) {
        List<String> path1 = route1.getPath();
        List<String> path2 = route2.getPath();
        for (int i = 1; i < path1.size(); i++) {
            for (int j = 1; j < path2.size(); j++)
                if (path1.get(i).equals(path2.get(j))) {
                    return true; // Found a common city
                }
        }
        return false; // No common cities found
    }

    //Methods for generating random routes
    private Route generateRanRoute() {
        int vertSize = graph.getVertices().length;
        String randCityString = graph.getVertices()[rand.nextInt(vertSize)];
        return new Route(new Pair<String, String>(depotName, randCityString), graph);
    }

    private Route generateRanRouteString(String City) {
        return new Route(new Pair<String, String>(depotName, City), graph);
    }

    private Route getRouteWithfixLenght() {
        Route randomRoute = generateRanRoute();
        while (!isValid(randomRoute) || randomRoute.getRouteLenght() < MIN_ROUTE_LENGTH) {
            randomRoute = generateRanRoute();
        }
        return randomRoute;
    }

    private Route getRouteWithfixLenghtString(String City) {
        Route randomRoute = generateRanRouteString(City);
        while (!isValid(randomRoute) || randomRoute.getRouteLenght() < MIN_ROUTE_LENGTH) {
            randomRoute = generateRanRoute();
        }
        return randomRoute;
    }

    // Generate random routes for the initial population
    private List<Route> initializePopulation() {
        List<Route> initialPopulation = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            initialPopulation.add(getRouteWithfixLenght());
        }
        return initialPopulation;
    }

    // Selection using rouletteWheelSelection
    private Route rouletteWheelSelection() {
        double totalFitness = population.stream().mapToDouble(Route::getFitness).sum();
        double randomValue = rand.nextDouble() * totalFitness;
        double runningSum = 0;

        for (Route route : population) {
            runningSum += route.getFitness();
            if (runningSum >= randomValue) {
                return route;
            }
        }
        return population.get(population.size() - 1); // Fallback
    }

    private List<Route> selection() {
        List<Route> selectedRoutes = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            selectedRoutes.add(rouletteWheelSelection());
        }
        return selectedRoutes;
    }
    //end selection

    //Crossover
    private List<Route> crossover(List<Route> selectedRoutes) {
        List<Route> offspring = new ArrayList<>();
        for (int i = 0; i < selectedRoutes.size() - 1; i += 2) {
            Route parent1 = selectedRoutes.get(i);
            Route parent2 = selectedRoutes.get(i + 1);
            int sizePathP1 = parent1.getPath().size();
            int sizePathP2 = parent2.getPath().size();
            int randIndex1;
            int randIndex2;
            Route child1 = null;
            Route child2 = null;
            boolean check = true;

            // Create two valid children using segments from both parents
            while (check) {
                check = true;
                randIndex1 = rand.nextInt(sizePathP1 - 2) + 1;
                randIndex2 = rand.nextInt(sizePathP2 - 2) + 1;
                child1 = getRouteWithfixLenghtString(parent2.getPath().get(randIndex2));
                if (!isValid(child1))
                    check = false;
                if (graph.validateRoute(child1.getPath()))
                    check = false;
                child2 = getRouteWithfixLenghtString(parent1.getPath().get(randIndex1));
                if (!isValid(child2))
                    check = false;
                if (graph.validateRoute(child2.getPath()))
                    check = false;
                if (!check)
                    continue;
            }

            // Add the children to the offspring list
            offspring.add(child1);
            offspring.add(child2);
        }
        return offspring;
    }

    //Mutations
    private void mutate(List<Route> population) {
        for (Route route : population) {
            if (rand.nextDouble() < mutationRate) {
                List<String> currentPath = route.getPath();
                int replaceIndex = rand.nextInt(currentPath.size() - 2) + 1;
                String newCity;
                do {
                    newCity = graph.getVertices()[rand.nextInt(graph.getVertices().length)];
                } while (currentPath.contains(newCity));
                currentPath.set(replaceIndex, newCity);
                while (!graph.validateRoute(currentPath)) {
                    do {
                        newCity = graph.getVertices()[rand.nextInt(graph.getVertices().length)];
                    } while (currentPath.contains(newCity));
                    currentPath.set(replaceIndex, newCity);
                }
                route = new Route(currentPath, graph); // Update the route with the new path
            }
        }
    }

    //Replacement, with preservation of 30%
    private void replacement(List<Route> offspring) {
        int numberOfElites = (int) (populationSize * .3); // Define how many top individuals to preserve
        // Sort the current population by fitness in descending order
        population.sort((route1, route2) -> Double.compare(route2.getFitness(), route1.getFitness()));
        // Preserve the top 'numberOfElites' from the current population
        List<Route> nextGeneration = new ArrayList<>(population.subList(0, numberOfElites));
        // Fill the rest of the next generation with offspring
        nextGeneration.addAll(offspring.subList(0, populationSize - numberOfElites));
        // Update the population with the next generation
        population = nextGeneration;
        // Shuffle the population to ensure random mixing
        Collections.shuffle(population, rand);
    }

    //Extracts distinct routes from a list of routes
    private List<Route> extractDistinctRoutes() {
        population.sort((route1, route2) -> Double.compare(route2.getFitness(), route1.getFitness()));
        List<Route> distinctRoutes = new ArrayList<>();
        for (Route candidateRoute : population) {
            boolean isDistinct = distinctRoutes.stream()
                    .noneMatch(existingRoute -> doRoutesOverlap(existingRoute, candidateRoute));
            if (isDistinct) {
                distinctRoutes.add(candidateRoute);
            }
        }
        return distinctRoutes;
    }

    //Main Genetic Algorithm method
    public List<Route> findBestRoutes(int numberOfGenerations) {

        List<Route> distinctRoutes = new ArrayList<>();

        int generations = 0;

        while (generations < numberOfGenerations) {
            
            // Selection
            List<Route> selectedRoutes = selection();
            // Crossover
            List<Route> offspring = crossover(selectedRoutes);

            // Mutation
            mutate(offspring);

            // Replacement
            replacement(offspring);

            // Extract distinct routes from the current population
            distinctRoutes = extractDistinctRoutes();

            generations++;
        }

        // Return the distinct routes
        return distinctRoutes;
    }

    //Print results
    public void printBestRoutes(List<Route> bestRoutes, Graph graph, int numRoutes) {
        try (PrintWriter writer = new PrintWriter("output.txt")) {
            // The following loop writes route information to 'output.txt'.
            // Each time this method is called, it overwrites the file.
            for (int i = 0; i < Math.min(numRoutes, bestRoutes.size()); i++) {
                Route route = bestRoutes.get(i);
                writer.println("Route " + (i + 1) + ":");
                writer.println("Cities: " + String.join(" -> ", route.getPath()));
                writer.println("Total Distance: " + route.getTotalDistance());
                writer.println("Total Time: " + route.getTotalTime());
                writer.println("Fitness: " + route.getFitness());
                writer.println();
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error: Unable to write to file 'output.txt'.");
            e.printStackTrace();
        }
    }

}
