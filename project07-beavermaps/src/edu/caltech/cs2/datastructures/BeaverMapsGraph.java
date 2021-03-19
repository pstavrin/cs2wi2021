package edu.caltech.cs2.datastructures;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.IDictionary;
import edu.caltech.cs2.interfaces.IPriorityQueue;
import edu.caltech.cs2.interfaces.ISet;

import java.io.FileReader;
import java.io.IOException;


public class BeaverMapsGraph extends Graph<Long, Double> {
    private IDictionary<Long, Location> ids;
    private ISet<Location> buildings;

    public BeaverMapsGraph() {
        super();
        this.buildings = new ChainingHashSet<>();
        this.ids = new ChainingHashDictionary<>(MoveToFrontDictionary::new);

    }

    /**
     * Reads in buildings, waypoinnts, and roads file into this graph.
     * Populates the ids, buildings, vertices, and edges of the graph
     * @param buildingsFileName the buildings filename
     * @param waypointsFileName the waypoints filename
     * @param roadsFileName the roads filename
     */
    public BeaverMapsGraph(String buildingsFileName, String waypointsFileName, String roadsFileName) {
        this();

        JsonElement buildings = fromFile(buildingsFileName);
        JsonElement waypoints = fromFile(waypointsFileName);
        JsonElement roads = fromFile(roadsFileName);
        for (JsonElement b : buildings.getAsJsonArray()) {
            Location loc = new Location(b.getAsJsonObject());
            this.buildings.add(loc);
            this.addVertex(loc);
            this.ids.put(loc.id, loc);
        }
        for (JsonElement w : waypoints.getAsJsonArray()) {
            Location loc = new Location(w.getAsJsonObject());
            this.addVertex(loc);
            this.ids.put(loc.id, loc);
        }
        for (JsonElement r : roads.getAsJsonArray()) {
            Long locationIDOne = null;
            for (JsonElement locID : r.getAsJsonArray()) {
                if (locationIDOne == null) {
                    locationIDOne = locID.getAsLong();
                    continue;
                }
                long locationIDTwo = locID.getAsLong();
                Location locOne = this.ids.get(locationIDOne);
                Location locTwo = this.ids.get(locationIDTwo);
                Double weight = locOne.getDistance(locTwo);
                this.addUndirectedEdge(locationIDOne, locationIDTwo, weight);
                locationIDOne = locationIDTwo;
            }
        }
    }

    /**
     * Returns a deque of all the locations with the name locName.
     * @param locName the name of the locations to return
     * @return a deque of all location with the name locName
     */
    public IDeque<Location> getLocationByName(String locName) {
        IDeque<Location> outLocations = new ArrayDeque<>();
        for (Location loc : this.ids.values()) {
            if (loc.name != null && loc.name.equals(locName)) {
                outLocations.add(loc);
            }
        }
        return outLocations;
    }

    /**
     * Returns the Location object corresponding to the provided id
     * @param id the id of the object to return
     * @return the location identified by id
     */
    public Location getLocationByID(long id) {
        return this.ids.get(id);
    }

    /**
     * Adds the provided location to this map.
     * @param n the location to add
     * @return true if n is a new location and false otherwise
     */
    public boolean addVertex(Location n) {
        if (this.ids.get(n.id) != null) {
            return false;
        }
        this.addVertex(n.id);
        this.ids.put(n.id, n);
        return true;
    }

    /**
     * Returns the closest building to the location (lat, lon)
     * @param lat the latitude of the location to search near
     * @param lon the longitute of the location to search near
     * @return the building closest to (lat, lon)
     */
    public Location getClosestBuilding(double lat, double lon) {
        Location closestLocation  = null;
        for (Location building : this.buildings) {
            if (closestLocation == null) {
                closestLocation = building;
                continue;
            }
            if (building.getDistance(lat, lon) < closestLocation.getDistance(lat, lon)) {
                closestLocation = building;
            }
        }
        return closestLocation;
    }

    /**
     * Returns a set of locations which are reachable along a path that goes no further than `threshold` feet from start
     * @param start the location to search around
     * @param threshold the number of feet in the search radius
     * @return
     */
    public ISet<Location> dfs(Location start, double threshold) {
        double lat = start.lat;
        double lon = start.lon;
        ISet<Location> nearby = new ChainingHashSet<>();
        IDeque<Location> theStack = new ArrayDeque<>();
        theStack.add(start);
        while (theStack.size() != 0) {
            Location popped = ((ArrayDeque<Location>) theStack).pop();
            nearby.add(popped);
            for (Long child : this.vertices.get(popped.id).keySet()) {
                if (getLocationByID(child).getDistance(lat, lon) < threshold && !nearby.contains(getLocationByID(child))) {
                    theStack.add(getLocationByID(child));
                }
            }
        }
        return nearby;
    }

    /**
     * Returns a list of Locations corresponding to
     * buildings in the current map.
     * @return a list of all building locations
     */
    public ISet<Location> getBuildings() {
        return this.buildings;
    }

    /**
     * Returns a shortest path (i.e., a deque of vertices) between the start
     * and target locations (including the start and target locations).
     * @param start the location to start the path from
     * @param target the location to end the path at
     * @return a shortest path between start and target
     */
    public IDeque<Location> dijkstra(Location start, Location target) {
        if (start.equals(target)) {
            IDeque<Location> outt = new ArrayDeque<>();
            outt.add(start);
            return outt;
        }
        IPriorityQueue<Location> theHeap = new MinFourHeap<>();
        theHeap.enqueue(new IPriorityQueue.PQElement<Location>(start, 0));
        ChainingHashSet<Long> last = new ChainingHashSet<>();

        ChainingHashDictionary<Location, Double> shortestDistances = new ChainingHashDictionary<Location, Double>(MoveToFrontDictionary::new);
        shortestDistances.put(start, 0.0);

        ChainingHashDictionary<Location, Location> parents = new ChainingHashDictionary<>(MoveToFrontDictionary::new);

        while (theHeap.size() != 0) {
            IPriorityQueue.PQElement<Location> popped = theHeap.dequeue();
            shortestDistances.put(popped.data, popped.priority);
            last.add(popped.data.id);
            if (popped.equals(target)) {
                break;
            }
            Location whereWeAreNow = popped.data;
            double lat = whereWeAreNow.lat;
            double lon = whereWeAreNow.lon;

            for (Long child : this.vertices.get(whereWeAreNow.id).keySet()) {
                if (getLocationByID(child).type == Location.Type.BUILDING && !getLocationByID(child).equals(target)) {
                    continue;
                }
                if (!shortestDistances.containsKey(getLocationByID(child))) {
                    theHeap.enqueue(new IPriorityQueue.PQElement<Location>(getLocationByID(child), Double.POSITIVE_INFINITY));
                    shortestDistances.put(getLocationByID(child), Double.POSITIVE_INFINITY);
                    parents.put(getLocationByID(child), whereWeAreNow);
                }
                double distance = adjacent(whereWeAreNow.id, child);
                if (shortestDistances.get(getLocationByID(child)) > shortestDistances.get(whereWeAreNow) + distance) {
                    shortestDistances.put(getLocationByID(child), shortestDistances.get(whereWeAreNow) + distance);
                    theHeap.decreaseKey(new IPriorityQueue.PQElement<Location>(getLocationByID(child), shortestDistances.get(getLocationByID(child))));
                    parents.put(getLocationByID(child), whereWeAreNow);
                }
            }
        }

        IDeque<Location> out = new ArrayDeque<>();
        out.add(target);
        if (!parents.containsKey(target)) {
            return null;
        }
        while (!out.peekFront().equals(start) && out.peekFront() != null) {
            out.addFront(parents.get(out.peekFront()));
        }
        if (out.peekFront() == null) {
            return null;
        }
        return out;
    }

    /**
     * Returns a JsonElement corresponding to the data in the file
     * with the filename filename
     * @param filename the name of the file to return the data from
     * @return the JSON data from filename
     */
    private static JsonElement fromFile(String filename) {
        try (FileReader reader = new FileReader(filename)) {
            return JsonParser.parseReader(reader);
        } catch (IOException e) {
            return null;
        }
    }
}
