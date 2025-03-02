import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import java.util.List;

public class BackendTests {
   /**
   * Tests the loadGraphData Backend method
   */
   @Test
   public void roleTest1(){
     Graph_Placeholder graph = new Graph_Placeholder();
     Backend backend = new Backend(graph);
     try {
       backend.loadGraphData("campus.dot");
     }catch(Exception e){
       Assertions.assertTrue(false);
     }
     Assertions.assertTrue(true);
   }
   /**
   * Tests the getListOfAllLocations Backend method and
   * findLocationsOnShortestPath Backend method
   */
   @Test
   public void roleTest2(){
    Graph_Placeholder graph = new Graph_Placeholder();
    Backend backend = new Backend(graph);
    //There should be three locations in the graph
    List<String> result = backend.getListOfAllLocations();
    Assertions.assertTrue(result.size() == 3);
    Assertions.assertTrue(result.get(0).equals("Union South"));
    Assertions.assertTrue(result.get(1).equals("Computer Sciences and Statistics"));
    Assertions.assertTrue(result.get(2).equals("Atmospheric, Oceanic and Space Sciences"));

    result = backend.findLocationsOnShortestPath("Union South", "Atmospheric, Oceanic and Space Sciences");
    //Should return the only three locations in order
    Assertions.assertTrue(result.size() == 3);
    Assertions.assertTrue(result.get(0).equals("Union South"));
    Assertions.assertTrue(result.get(1).equals("Computer Sciences and Statistics"));
    Assertions.assertTrue(result.get(2).equals("Atmospheric, Oceanic and Space Sciences"));
  }


  /**
   * Tests the findTimesOnShortestPath backend method
   * and the getReachableFromWithin backend method
   */
  @Test
  public void roleTest3(){
    Graph_Placeholder graph = new Graph_Placeholder();
    Backend backend = new Backend(graph);
    List<Double> result = backend.findTimesOnShortestPath("Union South", "Atmospheric, Oceanic and Space Sciences");
    //Should return [1.0, 2.0]
    Assertions.assertTrue(result.get(0).equals(1.0));
    Assertions.assertTrue(result.get(1).equals(2.0));
    Assertions.assertTrue(result.size() == 2);

    List<String> result2 = backend.getReachableFromWithin("Union South", 2);
    //Should return [Union South, Computer Sciences and Statistics]
    Assertions.assertTrue(result2.get(0).equals("Union South"));
    Assertions.assertTrue(result2.get(1).equals("Computer Sciences and Statistics"));
    Assertions.assertTrue(result2.size() == 2);
    result2 = backend.getReachableFromWithin("Union South", 3);
    //Should return [Union South, Computer Sciences and Statistics, Atmospheric, Oceanic and Space Sciences]
    Assertions.assertTrue(result2.get(0).equals("Union South"));
    Assertions.assertTrue(result2.get(1).equals("Computer Sciences and Statistics"));
    Assertions.assertTrue(result2.get(2).equals("Atmospheric, Oceanic and Space Sciences"));
    Assertions.assertTrue(result2.size() == 3);
  }  

}
