import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;


/**
 * This class provides a shortestPath method for finding routes between two points
 * on the map. Start by using Dijkstra's, and if your code isn't fast enough for your
 * satisfaction (or the autograder), upgrade your implementation by switching it to A*.
 * Your code will probably not be fast enough to pass the autograder unless you use A*.
 * The difference between A* and Dijkstra's is only a couple of lines of code, and boils
 * down to the priority you use to order your vertices.
 */
public class Router {
    /**
     * Return a LinkedList of <code>Long</code>s representing the shortest path from st to dest, 
     * where the longs are node IDs.
     */

    public static LinkedList<Long> shortestPath(GraphDB g, double stlon,
                                                double stlat, double destlon, double destlat) {
        HashMap<Long, Double> distTo = new HashMap<>();
        HashMap<Long, Long> edgeTo = new HashMap<>();
        PriorityQueue<GraphDB.Node> fringe = new PriorityQueue<>(new GraphComparator());
        GraphDB.Node goal = g.nodeFinder(g.closest(destlon, destlat));
        GraphDB.Node start = g.nodeFinder(g.closest(stlon, stlat));
        start.priority = 0;


        for (GraphDB.Node vert: g.vertices) {
            fringe.add(vert);
        }

        edgeTo.put(start.id, start.id);
        distTo.put(start.id, 0.0);
        GraphDB.Node last = fringe.poll();

        while (last.id != goal.id) {
            for (Long adj: g.adjacent(last.id)) {
                double cond = distTo.get(last.id) + g.distance(last.id, adj);
                if (distTo.get(adj) == null) {
                    distTo.put(adj, Double.MAX_VALUE);
                }
                if (cond < distTo.get(adj)) {
                    edgeTo.put(adj, last.id);
                    distTo.put(adj, cond);
                    GraphDB.Node adjNode = g.nodeFinder(adj);
                    adjNode.priority = cond + g.distance(adj, goal.id);
                    fringe.remove(adjNode);
                    fringe.add(adjNode);
                }
            }
            last = fringe.poll();
        }

        LinkedList<Long> shortestPath = pathTo(edgeTo, start.id, goal.id);
        g.defaultPriority();

        return shortestPath;
    }

    public static LinkedList<Long> pathTo(HashMap<Long, Long> edgeTo, Long startid, Long destid) {
        LinkedList<Long> trail = new LinkedList<>();
        Long node = destid;

        while (node != startid) {
            trail.addFirst(node);
            node = edgeTo.get(node);
        }

        trail.addFirst(node);
        return trail;
    }
}
