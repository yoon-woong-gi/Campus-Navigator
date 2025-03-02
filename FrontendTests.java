import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;

public class FrontendTests {

    @Test
    public void roleTest1() {
        Backend_Placeholder backend = new Backend_Placeholder(new Graph_Placeholder());
        Frontend frontend = new Frontend(backend);
        String prompt = frontend.generateShortestPathPromptHTML();
        Assertions.assertTrue(prompt.contains("id='start'"));
        Assertions.assertTrue(prompt.contains("id='end'"));
        Assertions.assertTrue(prompt.contains("Find Shortest Path"));
        
        String response = frontend.generateShortestPathResponseHTML("Union South", "Computer Sciences and Statistics");
        Assertions.assertTrue(response.contains("Start Location: Union South"));
        Assertions.assertTrue(response.contains("End Location: Computer Sciences and Statistics"));
        Assertions.assertTrue(response.contains("<ol>"));
        Assertions.assertTrue(response.contains("<li>"));
    }

    @Test
    public void roleTest2() {
        Backend_Placeholder backend = new Backend_Placeholder(new Graph_Placeholder());
        Frontend frontend = new Frontend(backend);

        String prompt = frontend.generateReachableFromWithinPromptHTML();
        Assertions.assertTrue(prompt.contains("id='from'"));
        Assertions.assertTrue(prompt.contains("id='time'"));
        Assertions.assertTrue(prompt.contains("Find Reachable Nodes"));
    }

    @Test
    public void roleTest3() {
        Backend_Placeholder backend = new Backend_Placeholder(new Graph_Placeholder());
        Frontend frontend = new Frontend(backend);

        String response = frontend.generateReachableFromWithinResponseHTML("Union South", 300.0);
        Assertions.assertTrue(response.contains("Start Location: Union South"));
        Assertions.assertTrue(response.contains("Travel time allowed: 300.0"));
        Assertions.assertTrue(response.contains("<ul>"));
        Assertions.assertTrue(response.contains("<li>"));
    }


    /**
     * Tests the integration between Frontend and Backend components for finding shortest paths
     * between campus locations.
     */
    @Test
    public void IntegrationShortestPathBetweenEngBuildings() {
        // Create actual Backend with DijkstraGraph implementation
        GraphADT<String, Double> graph = new DijkstraGraph<>();
        Backend backend = new Backend(graph);
        Frontend frontend = new Frontend(backend);

        try {
            // Load actual campus data
            backend.loadGraphData("campus.dot");

            // Test shortest path between Engineering buildings
            String response = frontend.generateShortestPathResponseHTML(
                    "Engineering Hall",
                    "Engineering Research Building"
            );

            // Verify response contains expected path elements
            Assertions.assertTrue(response.contains("Engineering Hall"));
            Assertions.assertTrue(response.contains("Engineering Research Building"));
            Assertions.assertTrue(response.contains("<ol>")); // Should have ordered list
            Assertions.assertTrue(response.contains("Total Travel Time")); // Should show time
        } catch (IOException e) {
            Assertions.fail("Failed to load graph data: " + e.getMessage());
        }
    }

    /**
     * Tests the integration between Frontend and Backend components for finding
     * reachable locations within a time limit.
     */
    @Test
    public void IntegrationReachableLocationsFromUnionSouth() {
        // Create actual Backend with DijkstraGraph implementation
        GraphADT<String, Double> graph = new DijkstraGraph<>();
        Backend backend = new Backend(graph);
        Frontend frontend = new Frontend(backend);

        try {
            // Load actual campus data
            backend.loadGraphData("campus.dot");

            // Test reachable locations from Union South within 200 seconds
            String response = frontend.generateReachableFromWithinResponseHTML(
                    "Union South",
                    200.0
            );

            // Verify response contains expected elements
            Assertions.assertTrue(response.contains("Union South"));
            Assertions.assertTrue(response.contains("200.0"));
            Assertions.assertTrue(response.contains("<ul>")); // Should have unordered list
            // Should contain nearby buildings
            Assertions.assertTrue(response.contains("Wendt Commons"));
        } catch (IOException e) {
            Assertions.fail("Failed to load graph data: " + e.getMessage());
        }
    }

    /**
     * Tests the integration between Frontend and Backend components for handling
     * non-existent locations in the shortest path search.
     */
    @Test
    public void IntegrationInvalidLocationShortestPath() {
        // Create actual Backend with DijkstraGraph implementation
        GraphADT<String, Double> graph = new DijkstraGraph<>();
        Backend backend = new Backend(graph);
        Frontend frontend = new Frontend(backend);

        try {
            // Load actual campus data
            backend.loadGraphData("campus.dot");

            // Test path between invalid location and valid location
            String response = frontend.generateShortestPathResponseHTML(
                    "Non-Existent Building",
                    "Union South"
            );

            // Verify response indicates no path found
            Assertions.assertTrue(response.contains("No path found"));
        } catch (IOException e) {
            Assertions.fail("Failed to load graph data: " + e.getMessage());
        }
    }

    /**
     * Tests the integration between Frontend and Backend components for handling
     * unreachable locations within time limit.
     */
    @Test
    public void IntegrationUnreachableLocationsWithinTime() {
        // Create actual Backend with DijkstraGraph implementation
        GraphADT<String, Double> graph = new DijkstraGraph<>();
        Backend backend = new Backend(graph);
        Frontend frontend = new Frontend(backend);

        try {
            // Load actual campus data
            backend.loadGraphData("campus.dot");

            // Test reachable locations with very short time limit
            String response = frontend.generateReachableFromWithinResponseHTML(
                    "Union South",
                    1.0  // Very short time, shouldn't reach any locations
            );

            // Verify response contains only the starting location
            Assertions.assertTrue(response.contains("Union South"));
            Assertions.assertTrue(response.contains("1.0"));
            // Should only contain Union South in the list
            int locationCount = response.split("<li>").length - 1;
            Assertions.assertTrue(locationCount <= 2,
                    "Should find very few or no reachable locations within 1 second");
        } catch (IOException e) {
            Assertions.fail("Failed to load graph data: " + e.getMessage());
        }
    }
}
