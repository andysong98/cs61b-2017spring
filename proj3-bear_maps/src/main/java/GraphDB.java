import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Graph for storing all of the intersection (vertex) and road (edge) information.
 * Uses your GraphBuildingHandler to convert the XML files into a graph. Your
 * code must include the vertices, adjacent, distance, closest, lat, and lon
 * methods. You'll also need to include instance variables and methods for
 * modifying the graph (e.g. addNode and addEdge).
 *
 * @author Alan Yao, Josh Hug
 */
public class GraphDB {
    /** Your instance variables for storing the graph. You should consider
     * creating helper classes, e.g. Node, Edge, etc. */

    ArrayList<Node> vertices;
    HashMap<Long, ArrayList<Node>> adjancencyList;
    HashMap<Long, Node> longtoNode;


    /**
     * Example constructor shows how to create and start an XML parser.
     * You do not need to modify this constructor, but you're welcome to do so.
     * @param dbPath Path to the XML file to be parsed.
     */
    public GraphDB(String dbPath) {
        vertices = new ArrayList<>();
        adjancencyList = new HashMap<>();
        longtoNode = new HashMap<>();
        try {
            File inputFile = new File(dbPath);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            GraphBuildingHandler gbh = new GraphBuildingHandler(this);
            saxParser.parse(inputFile, gbh);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        clean();

    }

    public class Node {
        Long id;
        double lon;
        double lat;
        double priority;

        public Node(Long id, double lon, double lat) {
            this.id = id;
            this.lon = lon;
            this.lat = lat;
            priority = Double.MAX_VALUE;
        }

        @Override
        public int hashCode() {
            return this.id.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o.getClass() != getClass()) {
                return false;
            }
            Node n = (Node) o;
            return n.id == id && n.lon == lon && n.lat == lat;
        }
        @Override
        public String toString() {
            return id.toString();
        }
    }


    /**
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

    /**
     *  Remove nodes with no connections from the graph.
     *  While this does not guarantee that any two nodes in the remaining graph are connected,
     *  we can reasonably assume this since typically roads are connected.
     */
    private void clean() {
        for (long key: vertices()) {
            if (adjancencyList.get(key) == null) {
                adjancencyList.remove(key);
                vertices.remove(nodeFinder(key));
            }
        }
    }

    public void defaultPriority() {
        for (Node vert: vertices) {
            vert.priority = Double.MAX_VALUE;
        }
    }

    /** Returns an iterable of all vertex IDs in the graph. */
    Iterable<Long> vertices() {
        //YOUR CODE HERE, this currently returns only an empty list.
        ArrayList<Long> vertLong = new ArrayList<>();

        for (int i = 0; i < vertices.size(); i++) {
            vertLong.add(vertices.get(i).id);
        }

        return vertLong;
    }

    /** Returns ids of all vertices adjacent to v. */
    Iterable<Long> adjacent(long v) {
        ArrayList<Node> adjacentNodes = adjancencyList.get(v);
        ArrayList<Long> adjacent = new ArrayList<>();

        for (int i = 0; i < adjacentNodes.size(); i++) {
            adjacent.add(adjacentNodes.get(i).id);
        }
        return adjacent;
    }

    /** Returns the Euclidean distance between vertices v and w, where Euclidean distance
     *  is defined as sqrt( (lonV - lonV)^2 + (latV - latV)^2 ). */
    double distance(long v, long w) {
        Node a = nodeFinder(v);
        Node b = nodeFinder(w);

        double lonDiffSquare = (b.lon - a.lon) * (b.lon - a.lon);
        double latDiffSquare = (b.lat - a.lat) * (b.lat - a.lat);
        return Math.sqrt(lonDiffSquare + latDiffSquare);
    }

    /** Returns the vertex id closest to the given longitude and latitude. */
    long closest(double lon, double lat) {
        addNode(Long.MAX_VALUE, lon, lat);
        Node start = nodeFinder(Long.MAX_VALUE);

        long closest = vertices.get(0).id;
        for (int i = 1; i < vertices.size() - 1; i++) {
            if (distance(vertices.get(i).id, start.id) < distance(closest, start.id)) {
                closest = vertices.get(i).id;
            }
        }

        vertices.remove(start);
        return closest;
    }

    /** Longitude of vertex v. */
    double lon(long v) {
        Node a = nodeFinder(v);
        return a.lon;
    }

    /** Latitude of vertex v. */
    double lat(long v) {
        Node a = nodeFinder(v);
        return a.lat;
    }

    Node nodeFinder(long v) {
        return longtoNode.get(v);
    }

    void addNode(long v, double lon, double lat) {
        Node newEntry = new Node(v, lon, lat);

        longtoNode.put(v, newEntry);
        vertices.add(newEntry);
    }

    void addEdge(long v, long w) {
        Node a = nodeFinder(v);
        Node b = nodeFinder(w);

        if (adjancencyList.get(v) == null) {
            adjancencyList.put(v, new ArrayList<>());
        }
        if (adjancencyList.get(w) == null) {
            adjancencyList.put(w, new ArrayList<>());
        }
        adjancencyList.get(v).add(b);
        adjancencyList.get(w).add(a);
    }

    void removeEdge(long v, long w) {
        Node a = nodeFinder(v);
        Node b = nodeFinder(w);

        adjancencyList.get(w).remove(a);
        adjancencyList.get(v).remove(b);
    }

    void removeNode(long v) {
        Node vNode = nodeFinder(v);

        for (Long adj: vertices()) {
            removeEdge(adj, v);
        }

        vertices.remove(vNode);
    }
}
