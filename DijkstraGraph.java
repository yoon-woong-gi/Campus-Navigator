// === CS400 File Header Information ===
// Name: Woonggi Yoon
// Email: wyoon@wisc.edu
// Group and Team: P2.3905
// Lecturer: Florian Heimerl
// Notes to Grader: Thank you for your hard work!
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.*;

/**
 * This class implements Dijkstra's shortest path algorithm by extending BaseGraph.
 * It provides functionality to find the shortest path between nodes and its cost
 * in a weighted directed graph.
 */
public class DijkstraGraph<NodeType, EdgeType extends Number>
        extends BaseGraph<NodeType, EdgeType>
        implements GraphADT<NodeType, EdgeType> {

    /**
     * SearchNode class represents a node in the path-finding process.
     * It stores the current node, the total cost to reach this node,
     * and the predecessor node in the shortest path.
     */
    protected class SearchNode implements Comparable<SearchNode> {
        public Node node;
        public double cost;
        public SearchNode predecessor;

        /**
         * Constructs a new SearchNode with the given parameters
         * 
         * @param node the current node
         * @param cost the cost to reach this node
         * @param predecessor the previous node in the path
         */
        public SearchNode(Node node, double cost, SearchNode predecessor) {
            this.node = node;
            this.cost = cost;
            this.predecessor = predecessor;
        }

        /**
         * Compares SearchNodes based on their cost for priority queue ordering
         * 
         * @param other the SearchNode to compare with
         * @return positive if this cost is greater, negative if less, 0 if equal
         */
        public int compareTo(SearchNode other) {
            if (cost > other.cost)
                return +1;
            if (cost < other.cost)
                return -1;
            return 0;
        }
    }

    /**
     * Creates a new DijkstraGraph with a PlaceholderMap as its underlying storage
     */
    public DijkstraGraph() {
        super(new HashtableMap<NodeType, BaseGraph<NodeType, EdgeType>.Node>());
    }

    /**
     * Implements Dijkstra's algorithm to find the shortest path between two nodes
     * 
     * @param start the starting node
     * @param end the destination node
     * @return SearchNode containing the end node and its shortest path information
     * @throws NoSuchElementException if start/end nodes don't exist or no path exists
     */
    protected SearchNode computeShortestPath(NodeType start, NodeType end) {
        // Verify both nodes exist in the graph
        if (!nodes.containsKey(start) || !nodes.containsKey(end)) {
            throw new NoSuchElementException("Start or end node not found.");
        }

        // Initialize data structures for Dijkstra's algorithm
        PriorityQueue<SearchNode> pq = new PriorityQueue<>();
        HashMap<NodeType, SearchNode> visited = new HashMap<>();

        // Create and add start node to priority queue
        SearchNode startNode = new SearchNode(nodes.get(start), 0, null);
        pq.add(startNode);

        // Main Dijkstra's algorithm loop
        while (!pq.isEmpty()) {
            SearchNode current = pq.poll();

            // Skip if node already visited
            if (visited.containsKey(current.node.data)) {
                continue;
            }
            visited.put(current.node.data, current);

            // Return if end node is reached
            if (current.node.data.equals(end)) {
                return current;
            }

            // Explore all neighboring nodes
            for (Edge edge : current.node.edgesLeaving) {
                NodeType successorData = edge.successor.data;
                if (!visited.containsKey(successorData)) {
                    double newCost = current.cost + edge.data.doubleValue();
                    pq.add(new SearchNode(edge.successor, newCost, current));
                }
            }
        }

        // No path found
        throw new NoSuchElementException("Path does not exist.");
    }

    /**
     * Returns the sequence of nodes in the shortest path from start to end
     * 
     * @param start the starting node
     * @param end the destination node
     * @return List of nodes in the shortest path order
     * @throws NoSuchElementException if no path exists
     */
    public List<NodeType> shortestPathData(NodeType start, NodeType end) {
        SearchNode endNode = computeShortestPath(start, end);
        
        // Reconstruct path from end to start
        LinkedList<NodeType> path = new LinkedList<>();
        SearchNode current = endNode;
        while (current != null) {
            path.addFirst(current.node.data);
            current = current.predecessor;
        }
        return path;
    }

    /**
     * Returns the total cost of the shortest path from start to end
     * 
     * @param start the starting node
     * @param end the destination node
     * @return the total cost of the shortest path
     * @throws NoSuchElementException if no path exists
     */
    public double shortestPathCost(NodeType start, NodeType end) {
        SearchNode endNode = computeShortestPath(start, end);
        return endNode.cost;
    }

    /**
     * Tests the shortest path algorithm on the example from lecture
     */
    @Test
    public void testLectureExample() {
        DijkstraGraph<String, Integer> graph = new DijkstraGraph<>();
        // Create lecture example graph
        graph.insertNode("A");
        graph.insertNode("B");
        graph.insertNode("C");
        graph.insertNode("D");
        graph.insertNode("E");
        
        graph.insertEdge("A", "B", 2);
        graph.insertEdge("A", "C", 4);
        graph.insertEdge("B", "C", 1);
        graph.insertEdge("B", "D", 3);
        graph.insertEdge("C", "D", 5);
        graph.insertEdge("D", "E", 2);
        
        // Test shortest path from A to E (should be A->B->D->E with cost 7)
        List<String> path = graph.shortestPathData("A", "E");
        Assertions.assertEquals(Arrays.asList("A", "B", "D", "E"), path);
        Assertions.assertEquals(7, graph.shortestPathCost("A", "E"));
    }
    
    /**
     * Tests finding a different path in the same graph
     */
    @Test
    public void testDifferentPathSameGraph() {
        DijkstraGraph<String, Integer> graph = new DijkstraGraph<>();
        // Recreate same graph as above
        graph.insertNode("A");
        graph.insertNode("B");
        graph.insertNode("C");
        graph.insertNode("D");
        graph.insertNode("E");
        
        graph.insertEdge("A", "B", 2);
        graph.insertEdge("A", "C", 4);
        graph.insertEdge("B", "C", 1);
        graph.insertEdge("B", "D", 3);
        graph.insertEdge("C", "D", 5);
        graph.insertEdge("D", "E", 2);
        
        // Test different path: A to D (should be A->B->D with cost 5)
        List<String> path = graph.shortestPathData("A", "D");
        Assertions.assertEquals(Arrays.asList("A", "B", "D"), path);
        Assertions.assertEquals(5, graph.shortestPathCost("A", "D"));
    }
    
    /**
     * Tests that appropriate exceptions are thrown when no path exists
     */
    @Test
    public void testNoPathExists() {
        DijkstraGraph<String, Integer> graph = new DijkstraGraph<>();
        graph.insertNode("A");
        graph.insertNode("B");
        graph.insertNode("C");
        
        // Only add edge A->B, leaving C disconnected
        graph.insertEdge("A", "B", 1);
        
        // Test that exception is thrown when no path exists
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            graph.shortestPathData("A", "C");
        });
        
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            graph.shortestPathCost("A", "C");
        });
    }
}
