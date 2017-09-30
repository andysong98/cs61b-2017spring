import java.util.Comparator;

/**
 * Created by Anand on 4/19/2017.
 */
public class GraphComparator implements Comparator<GraphDB.Node>{
    public int compare(GraphDB.Node o1, GraphDB.Node o2) {
        if ((o1.priority == o2.priority) && (o1.priority == o2.priority)) {
            return 0;
        } else if ((o1.priority > o2.priority) && (o1.priority > o2.priority)) {
            return 1;
        } else {
            return -1;
        }
    }
}
