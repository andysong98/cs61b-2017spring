package db;

/**
 * Created by Anand on 3/1/2017.
 */
public class TestCell {
    public static void main(String[] args) {
        Cell<String> b = new StringCell("Hi");
        Cell<Double> c = new DoubleCell(5.1);
        Cell<Integer> d = new IntCell(5);
        Cell<String> z = new StringCell();
        Cell<Double> y = new DoubleCell();
        Cell<Integer> x = new IntCell();
        String e =  b.getValue();
        double g =  c.getValue();
        int f = d.getValue();
        System.out.println(e);
        System.out.println(g);
        System.out.println(f);
        System.out.println(z);
        System.out.println(y);
        System.out.println(x);
    }
}
