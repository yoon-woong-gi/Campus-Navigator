import java.io.File;
import java.io.IOException;
import java.util.*;

public class Backend implements BackendInterface{
  private GraphADT<String, Double> graph;
  /**
  * Sets an instance of the GraphADT graph
  */
  public Backend(GraphADT<String,Double> graph){
    this.graph = graph;
  }
  /**
   * Loads graph data from a dot file.  If a graph was previously loaded, this method should first
   * delete the contents (nodes and edges) of the existing graph before loading a new one.
   *
   * @param filename the path to a dot file to read graph data from
   * @throws IOException if there was any problem reading from this file
   */
  @Override
  public void loadGraphData(String filename) throws IOException {
    graph = new DijkstraGraph<>();
    try(Scanner scanner = new Scanner(new File(filename))){
      while(scanner.hasNextLine()){
        try{
          String line = scanner.nextLine();
          // "node1" -> "node2" [seconds=x]
          if(line.contains("->")){
            //Splits the line into three parts
            String[] parts = line.split("->");
            //Starting destination
            String startNode = parts[0].trim().replaceAll("\"", "");
            //End part of string
            String[] endWeight = parts[1].split("\\[seconds=");
            //Ending destination
            String endNode = endWeight[0].trim().replaceAll("\"", "");
            double weight = Double.parseDouble(endWeight[1].replace(";","").replace("]", "").trim());

            graph.insertNode(startNode);
            graph.insertNode(endNode);
            graph.insertEdge(startNode, endNode, weight);
          }
        }
        catch(Exception e){
          //Something went wrong, this shouldn't happen
          throw new IOException("Inncorrectly parsed data");
        }
      }
    }

  }

  /**
   * Returns a list of all locations (node data) available in the graph.
   *
   * @return list of all location names
   */
  @Override
  public List<String> getListOfAllLocations() {
    return graph.getAllNodes();
  }

  /**
   * Return the sequence of locations along the shortest path from startLocation to endLocation, or
   * an empty list if no such path exists.
   *
   * @param startLocation the start location of the path
   * @param endLocation   the end location of the path
   * @return a list with the nodes along the shortest path from startLocation to endLocation, or an
   * empty list if no such path exists
   */
  @Override
  public List<String> findLocationsOnShortestPath(String startLocation, String endLocation) {
    if(!graph.containsNode(startLocation) || !graph.containsNode(endLocation))
        return Collections.emptyList();
    return graph.shortestPathData(startLocation, endLocation);
  }

  /**
   * Return the walking times in seconds between each two nodes on the shortest path from
   * startLocation to endLocation, or an empty list of no such path exists.
   *
   * @param startLocation the start location of the path
   * @param endLocation   the end location of the path
   * @return a list with the walking times in seconds between two nodes along the shortest path from
   * startLocation to endLocation, or an empty list if no such path exists
   */
  @Override
  public List<Double> findTimesOnShortestPath(String startLocation, String endLocation) {
    List<String> path = findLocationsOnShortestPath(startLocation, endLocation);
    List<Double> times = new ArrayList<>();
    if(path.isEmpty() || path.size() < 2)
      return times; //Return empty list, no valid path
    //Iterate over the path and process edges between consecutive nodes 
    for(int i = 0; i<path.size() - 1; i++){
      String start = path.get(i);
      String end = path.get(i + 1);
      //Try to add the edge data to times list
      try{
        times.add(graph.getEdge(start, end));
      }catch(Exception e){
	System.out.println("Unable to add edge: " + e.getMessage());
      }
    }
    return times;
  }

  /**
   * Returns the list of locations that can be reached when starting from the provided
   * startLocation, and travelling a maximum of travelTime seconds.
   *
   * @param startLocation the location to find the reachable locations from
   * @param travelTime    is the maximum number of seconds away the start location that a
   *                      destination must be in order to be returned
   * @return the list of destinations that can be reached from startLocation in travelTime seconds
   * or less
   * @throws NoSuchElementException if startLocation does not exist
   */
  @Override
  public List<String> getReachableFromWithin(String startLocation, double travelTime)
      throws NoSuchElementException {
    if(!graph.containsNode(startLocation))
      throw new NoSuchElementException("Start location doesn't exist");

    List<String> reachableNodes = new ArrayList<>(); //Nodes that can be reached within the travel time
    List<String> toExplore = new ArrayList<>(); //Nodes that still need to be explored
    List<Double> distances = new ArrayList<>(); //Distances for nodes in toExplore
    //Add starting info into explore and distance lists
    toExplore.add(startLocation);
    distances.add(0.0);
    while(!toExplore.isEmpty()){
      String current = toExplore.remove(0);
      double currentDistance = distances.remove(0);
      //If the current node is reachable add it to the list
      if(currentDistance <= travelTime && !reachableNodes.contains(current))
        reachableNodes.add(current);
      //Finds all neighbor nodes
      for(String neighboringNode : graph.getAllNodes()){
        if(graph.containsEdge(current, neighboringNode)){
          //Weight of the edge between current node and its neighbor
          double weight = graph.getEdge(current, neighboringNode);
          //Distance from starting point to neighbor
          double distance = currentDistance + weight;
          //If the neighbor node is within travel time and hasn't already been added to the reachableNodes list
          if(distance <= travelTime && !reachableNodes.contains(neighboringNode)){
            toExplore.add(neighboringNode);
            distances.add(distance);
          }
        }
      }
    }
    return reachableNodes;
  }
}
