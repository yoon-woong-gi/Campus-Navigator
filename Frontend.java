import java.util.List;
import java.util.NoSuchElementException;

/**
 * This class represents the frontend implementation of the application.
 * It provides methods to generate HTML for user inputs and responses
 * based on the shortest path or reachable locations, interacting with
 * the backend to retrieve the necessary data.
 */
public class Frontend implements FrontendInterface {
    private BackendInterface backend;

    /**
     * Main constructor that gets backend interface.
     *
     * @param backend gets the graph and the shortest algorithm
     */
    public Frontend(BackendInterface backend) {
        this.backend = backend;
    }

    /**
     * Returns an HTML code that get Start and End locations inputs and button
     * to find the shortest path. When user click the button,
     * execute findShortestPath() method in Backend.
     *
     * @return two HTML input tags and one HTML button tag
     */
    public String generateShortestPathPromptHTML() {
        return "<input type='text' id='start'>Start Location</input>" +
                "<input type='text' id='end'>End Location</input>" +
		"<input type='button' value='Find Shortest Path' onclick='findLocationsOnShortestPath(document.getElementById(\"start\").value, document.getElementById(\"end\").value)'>";
    }

    /**
     * Returns an HTML code that find shortest path when user give start and end point.
     * Firstly, explain start location and end location. Next, give the shortest path
     * using ordered list. If there is no path from start to end location,
     * it will return "No path found". Lastly, give the total travel time.
     *
     * @param start start location to find the shortest path
     * @param end   end location to find the shortest path
     * @return HTML tags to find the shortest path
     */
    public String generateShortestPathResponseHTML(String start, String end) {
        // Describe of path's start and end locations
        StringBuilder lines = new StringBuilder(
                "<p>Start Location: " +
                        start +
                        "&nbsp End Location: " +
                        end +
                        "</p>"
        );

        List<String> path;

        // Check whether these locations are connected
        try {
            path = backend.findLocationsOnShortestPath(start, end);
            if (path.isEmpty()) {
                lines.append("<p>No path found</p>");
                return lines.toString();
            }
        } catch (NoSuchElementException e) {
            return "<p>Error: " + e.getMessage() + "</p>";
        }

        // List of shortest path
        lines.append("<ol>");
        for (int i = 0; i < path.size(); i++) {
            lines.append("<li>").append(path.get(i)).append("</li>");
        }
        lines.append("</ol>");

        // Get total travel time
	List<Double> times = backend.findTimesOnShortestPath(start, end);
        double time = times.get(times.size() - 1);
        lines.append("<p>Total Travel Time: ").append(time).append("</p>");

        return lines.toString();
    }

    /**
     * Returns an HTML code that gets start location and max time limit and button
     * to get reachable locations from within start location.
     * When user click the button, execute getReachableFromWithin() method in Backend.
     *
     * @return HTML tags that can get reachable locations from start location within max time limit.
     */
    public String generateReachableFromWithinPromptHTML() {
        return "<input type='text' id='from'>Start Location</input>" +
                "<input type='text' id='time'>Max Time Limit</input>" +
		"<input type='button' value='Find Reachable Nodes' onclick='getReachableFromWithin(document.getElementById(\"from\").value, document.getElementById(\"time\").value)'>";
    }

    /**
     * Returns an HTML code that find reachable locations from start location within travel time limit.
     * Firstly, describe start location and travel time limit. Next, give locations that can reachable
     * locations within travel times. If there is no any place within time, return "No path found".
     *
     * @param start      start location to search
     * @param travelTime setting maximum travel time
     * @return an HTML tags can reachable locations within limited time
     */
    public String generateReachableFromWithinResponseHTML(String start, double travelTime) {
        List<String> locations; // List to hold locations reachable within the given travel time
        StringBuilder lines = new StringBuilder(); // StringBuilder to construct the HTML response

        // Describe the start location and travel time allowed
        lines.append("<p>Start Location: "); // Add opening paragraph for start location
        lines.append(start); // Include the provided start location
        lines.append("&nbsp Travel time allowed: "); // Add description for travel time
        lines.append(travelTime).append("</p>"); // Include the provided travel time and close the paragraph

        // Find reachable locations from the start within the given travel time
        try {
            // Call the backend to retrieve locations within the travel time
            locations = backend.getReachableFromWithin(start, travelTime);
            if (locations.isEmpty()) { // Check if no locations are reachable
                lines.append("<p>No path found</p>"); // Inform the user that no paths were found
                return lines.toString(); // Return the response as HTML
            }
        }
        // Handle the case where a location is invalid or the backend throws an exception
        catch (NoSuchElementException e) {
            // Return an error message with details from the exception
            return "<p>Error: " + e.getMessage() + "</p>";
        }

        // Add reachable locations to the HTML response
        lines.append("<ul>"); // Start an unordered list for locations
        for (int i = 0; i < locations.size(); i++) { // Iterate through all reachable locations
            lines.append("<li>").append(locations.get(i)).append("</li>"); // Add each location as a list item
        }
        lines.append("</ul>"); // Close the unordered list

        return lines.toString(); // Return the constructed HTML response
    }

}
