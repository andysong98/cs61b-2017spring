import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {
    // Recommended: QuadTree instance variable. You'll need to make
    //              your own QuadTree since there is no built-in quadtree in Java.
    QuadTree queryTree;
    

    /** imgRoot is the name of the directory containing the images.
     *  You may not actually need this for your class. */
    public Rasterer(String imgRoot) {
        QuadNode queryNode = new QuadNode(MapServer.ROOT_ULLON, MapServer.ROOT_ULLAT,
                MapServer.ROOT_LRLON, MapServer.ROOT_LRLAT, "img/root.png", 0);
        queryTree = recursiveBuilder(new QuadTree(queryNode, null, null, null, null));
    }

    public class QuadTree {
        QuadNode node;
        QuadTree upperLeft;
        QuadTree upperRight;
        QuadTree lowerLeft;
        QuadTree lowerRight;

        public QuadTree(QuadNode root, QuadTree upLeft, QuadTree upRight,
                        QuadTree lowLeft, QuadTree lowRight) {
            node = root;
            upperLeft = upLeft;
            upperRight = upRight;
            lowerLeft = lowLeft;
            lowerRight = lowRight;
        }

        public boolean intersectTiles(double queryUllon, double queryUllat,
                                      double queryLrlon, double queryLrlat) {
            return !((node.ullon > queryLrlon) || (queryLrlat > node.ullat)
                    || (queryUllon > node.lrlon) || (node.lrlat > queryUllat));
        }

        public boolean lonDPPsmallerThanOrIsLeaf(double queriesLonDPP) {
            double lonDPP = (node.lrlon - node.ullon) / MapServer.TILE_SIZE;
            if (upperLeft == null && upperRight == null
                    && lowerLeft == null && lowerRight == null) {
                return true;
            } else {
                return lonDPP < queriesLonDPP;
            }
        }
    }

    public class QuadNode {
        double ullon;
        double ullat;
        double lrlon;
        double lrlat;
        String img;
        int depth;

        public QuadNode(double ullon, double ullat,
                        double lrlon, double lrlat, String img, int depth) {
            this.ullon = ullon;
            this.ullat = ullat;
            this.lrlon = lrlon;
            this.lrlat = lrlat;
            this.img = img;
            this.depth = depth;
        }
    }

    public QuadTree recursiveBuilder(QuadTree root) {
        QuadNode rootNode = root.node;
        if (rootNode.depth == 7) {
            return root;
        } else if (rootNode.img.equals("img/root.png")) {
            QuadNode upLeft = new QuadNode(rootNode.ullon, rootNode.ullat,
                    (((rootNode.lrlon - rootNode.ullon) / 2) + rootNode.ullon),
                    (((rootNode.lrlat - rootNode.ullat) / 2) + rootNode.ullat),
                    "img/1.png", rootNode.depth + 1);
            QuadNode upRight = new QuadNode((((rootNode.lrlon - rootNode.ullon) / 2)
                    + rootNode.ullon), rootNode.ullat, rootNode.lrlon,
                    (((rootNode.lrlat - rootNode.ullat) / 2) + rootNode.ullat),
                    "img/2.png", rootNode.depth + 1);
            QuadNode lowLeft = new QuadNode(rootNode.ullon,
                    (((rootNode.lrlat - rootNode.ullat) / 2) + rootNode.ullat),
                    (((rootNode.lrlon - rootNode.ullon) / 2) + rootNode.ullon),
                    rootNode.lrlat, "img/3.png", rootNode.depth + 1);
            QuadNode lowRight = new QuadNode((((rootNode.lrlon
                     - rootNode.ullon) / 2) + rootNode.ullon),
                    (((rootNode.lrlat - rootNode.ullat) / 2) + rootNode.ullat),
                    rootNode.lrlon, rootNode.lrlat, "img/4.png", rootNode.depth + 1);

            QuadTree treeUpLeft = new QuadTree(upLeft, null, null, null, null);
            QuadTree treeUpRight = new QuadTree(upRight, null, null, null, null);
            QuadTree treeLowLeft = new QuadTree(lowLeft, null, null, null, null);
            QuadTree treeLowRight = new QuadTree(lowRight, null, null, null, null);

            root.upperLeft = recursiveBuilder(treeUpLeft);
            root.upperRight = recursiveBuilder(treeUpRight);
            root.lowerLeft = recursiveBuilder(treeLowLeft);
            root.lowerRight = recursiveBuilder(treeLowRight);
            return root;
        } else {
            String path = rootNode.img.substring(0, rootNode.img.indexOf("."));

            QuadNode upLeft = new QuadNode(rootNode.ullon, rootNode.ullat,
                    (((rootNode.lrlon - rootNode.ullon) / 2) + rootNode.ullon),
                    (((rootNode.lrlat - rootNode.ullat) / 2) + rootNode.ullat),
                    path + "1.png", rootNode.depth + 1);
            QuadNode upRight = new QuadNode((((rootNode.lrlon - rootNode.ullon) / 2)
                    + rootNode.ullon), rootNode.ullat, rootNode.lrlon,
                    (((rootNode.lrlat - rootNode.ullat) / 2) + rootNode.ullat),
                    path + "2.png", rootNode.depth + 1);
            QuadNode lowLeft = new QuadNode(rootNode.ullon, (((rootNode.lrlat
                    - rootNode.ullat) / 2) + rootNode.ullat),
                    (((rootNode.lrlon - rootNode.ullon) / 2) + rootNode.ullon),
                    rootNode.lrlat, path + "3.png", rootNode.depth + 1);
            QuadNode lowRight = new QuadNode((((rootNode.lrlon
                    - rootNode.ullon) / 2) + rootNode.ullon),
                    (((rootNode.lrlat - rootNode.ullat) / 2) + rootNode.ullat),
                    rootNode.lrlon, rootNode.lrlat, path + "4.png", rootNode.depth + 1);

            QuadTree treeUpLeft = new QuadTree(upLeft, null, null, null, null);
            QuadTree treeUpRight = new QuadTree(upRight, null, null, null, null);
            QuadTree treeLowLeft = new QuadTree(lowLeft, null, null, null, null);
            QuadTree treeLowRight = new QuadTree(lowRight, null, null, null, null);

            root.upperLeft = recursiveBuilder(treeUpLeft);
            root.upperRight = recursiveBuilder(treeUpRight);
            root.lowerLeft = recursiveBuilder(treeLowLeft);
            root.lowerRight = recursiveBuilder(treeLowRight);
            return root;
        }


    }


    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     * <p>
     *     The grid of images must obey the following properties, where image in the
     *     grid is referred to as a "tile".
     *     <ul>
     *         <li>The tiles collected must cover the most longitudinal distance per pixel
     *         (LonDPP) possible, while still covering less than or equal to the amount of
     *         longitudinal distance per pixel in the query box for the user viewport size. </li>
     *         <li>Contains all tiles that intersect the query bounding box that fulfill the
     *         above condition.</li>
     *         <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     *     </ul>
     * </p>
     * @param params Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     *
     * @return A map of results for the front end as specified:
     * "render_grid"   -> String[][], the files to display
     * "raster_ul_lon" -> Number, the bounding upper left longitude of the rastered image <br>
     * "raster_ul_lat" -> Number, the bounding upper left latitude of the rastered image <br>
     * "raster_lr_lon" -> Number, the bounding lower right longitude of the rastered image <br>
     * "raster_lr_lat" -> Number, the bounding lower right latitude of the rastered image <br>
     * "depth"         -> Number, the 1-indexed quadtree depth of the nodes of the rastered image.
     *                    Can also be interpreted as the length of the numbers in the image
     *                    string. <br>
     * "query_success" -> Boolean, whether the query was able to successfully complete. Don't
     *                    forget to set this to true! <br>
     * @see # REQUIRED_RASTER_REQUEST_PARAMS */

    public Map<String, Object> getMapRaster(Map<String, Double> params) {
        //System.out.println(params);
        Map<String, Object> results = new HashMap<>();
        //System.out.println("Since you haven't implemented getMapRaster, nothing is displayed in "
         //                  + "your browser.");
        ArrayDeque<QuadNode> imageNodes = new ArrayDeque<>();
        queueBuilder(imageNodes, queryTree, params);
        QuadNode[][] nodeGrid = gridBuilder(imageNodes);
        String[][] renderGrid = new String[nodeGrid.length][nodeGrid[0].length];

        for (int i = 0; i < renderGrid.length; i++) {
            for (int j = 0; j < renderGrid[0].length; j++) {
                renderGrid[i][j] = nodeGrid[i][j].img;
            }
        }

        double rasterUllon = nodeGrid[0][0].ullon;
        double rasterUllat = nodeGrid[0][0].ullat;
        double rasterLrlon = nodeGrid[nodeGrid.length - 1][nodeGrid[0].length - 1].lrlon;
        double rasterLrlat = nodeGrid[nodeGrid.length - 1][nodeGrid[0].length - 1].lrlat;

        int depth = nodeGrid[0][0].depth;
        boolean querySuccess = true;

        results.put("render_grid", renderGrid);
        results.put("raster_ul_lon", rasterUllon);
        results.put("raster_ul_lat", rasterUllat);
        results.put("raster_lr_lon", rasterLrlon);
        results.put("raster_lr_lat", rasterLrlat);
        results.put("depth", depth);
        results.put("query_success", querySuccess);


        return results;
    }

    public void queueBuilder(ArrayDeque<QuadNode> displayQueue,
                             QuadTree root, Map<String, Double> params) {
        double lrlon = params.get("lrlon");
        double lrlat = params.get("lrlat");
        double ullon = params.get("ullon");
        double ullat = params.get("ullat");
        double w = params.get("w"); double h = params.get("h");
        double queriesLONDPP = (lrlon - ullon) / w;

        if (!root.intersectTiles(ullon, ullat, lrlon, lrlat)) {
            return;
        } else if (root.intersectTiles(ullon, ullat, lrlon, lrlat)
                && !root.lonDPPsmallerThanOrIsLeaf(queriesLONDPP)) {
            queueBuilder(displayQueue, root.upperLeft, params);
            queueBuilder(displayQueue, root.upperRight, params);
            queueBuilder(displayQueue, root.lowerLeft, params);
            queueBuilder(displayQueue, root.lowerRight, params);
        } else if (root.intersectTiles(ullon, ullat, lrlon, lrlat)
                && root.lonDPPsmallerThanOrIsLeaf(queriesLONDPP)) {
            displayQueue.addLast(root.node);
        }
    }

    public QuadNode[][] gridBuilder(ArrayDeque<QuadNode> displayQueue) {
        LonComparator lonc  = new LonComparator();
        LatComparator latc = new LatComparator();
        QuadNode sample = displayQueue.getFirst(); int i = 0; int j = 0;

        for (QuadNode node: displayQueue) {
            if (lonc.compare(sample, node) == 0) {
                i++;
            }
            if (latc.compare(sample, node) == 0) {
                j++;
            }
        }
        QuadNode[][] displayGrid = new QuadNode[i][j];

        for (int a = 0; a < i; a++) {
            for (int b = 0; b < j; b++) {
                QuadNode gridEntry = findNodeBigYSmallX(displayQueue);
                displayGrid[a][b] = gridEntry;
            }
        }

        return displayGrid;
    }

    public QuadNode findNodeBigYSmallX(ArrayDeque<QuadNode> displayQueue) {
        LonComparator lonc  = new LonComparator();
        LatComparator latc = new LatComparator();
        QuadNode nodeBigYSmallX = displayQueue.getFirst();

        for (QuadNode node: displayQueue) {
            if (latc.compare(nodeBigYSmallX, node) < 0) {
                nodeBigYSmallX = node;
            }
        }
        for (QuadNode node: displayQueue) {
            if (lonc.compare(nodeBigYSmallX, node) > 0
                    && (latc.compare(nodeBigYSmallX, node) == 0)) {
                nodeBigYSmallX = node;
            }
        }

        displayQueue.remove(nodeBigYSmallX);
        return nodeBigYSmallX;
    }

    /* public static void main(String[] args) {
        Rasterer ourRaster = new Rasterer("img/");
        QuadTree notice = ourRaster.queryTree;
        HashMap<String, Double> params = new HashMap<>();
        params.put("lrlon", -122.20908713544797);
        params.put("ullon", -122.3027284165759);
        params.put("w", 305.0);
        params.put("h", 300.0);
        params.put("ullat", 37.88708748276975);
        params.put("lrlat", 37.848731523430196);

        Map<String, Object> results = ourRaster.getMapRaster(params);
        String[][] hey = (String[][]) results.get("render_grid");
        System.out.print("Complete");
    } */
}
