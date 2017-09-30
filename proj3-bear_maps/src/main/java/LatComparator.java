import java.util.Comparator;

/**
 * Created by Anand on 4/15/2017.
 */
public class LatComparator implements Comparator<Rasterer.QuadNode> {
    public int compare(Rasterer.QuadNode o1, Rasterer.QuadNode o2) {
        if ((o1.ullat == o2.ullat) && (o1.lrlat == o2.lrlat)) {
            return 0;
        } else if ((o1.ullat > o2.ullat) && (o1.lrlat > o2.lrlat)) {
            return 1;
        } else {
            return -1;
        }
    }
}
