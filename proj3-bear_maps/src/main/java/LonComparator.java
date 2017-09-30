import java.util.Comparator;
/**
 * Created by Anand on 4/15/2017.
 */
public class LonComparator implements Comparator<Rasterer.QuadNode> {
    public int compare(Rasterer.QuadNode o1, Rasterer.QuadNode o2) {
        if ((o1.ullon == o2.ullon) && (o1.lrlon == o2.lrlon)) {
            return 0;
        } else if ((o1.ullon > o2.ullon) && (o1.lrlon > o2.lrlon)) {
            return 1;
        } else {
            return -1;
        }
    }
}
