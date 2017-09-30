package db;

import edu.princeton.cs.algs4.In;

/**
 * Created by Anand on 3/2/2017.
 */
public class TableReader {
    public static String[] readLabels(String file) {
        In labelReader = new In(file);
        String columns = labelReader.readLine();
        String[] labels = uncommaLabels(columns);
        return labels;
    }
    
    private static String[] uncommaLabels(String phrase) {
        String[] phrases = phrase.split(",");
        String buffer = "";
        for (String word: phrases) {
            buffer += word + " ";
        }
        String[] noComma = buffer.split(" ");
        return noComma;
    }

    private static String[] uncommaLine(String phrase) {
        String[] phrases = phrase.split(",");
        for (int i = 0; i < phrases.length; i++) {
            String apost = phrases[i].substring(0, 1);
            if (apost.equals("'")) {
                String reformat = phrases[i].substring(1, phrases[i].length() - 1);
                phrases[i] = reformat;
            }
        }
        return phrases;
    }

    public static ArrayDeque<String> readValues(String file, Table t) {
        In valueReader = new In(file);
        String waste = valueReader.readLine();
        ArrayDeque<String> valueDeque = new ArrayDeque<>();
        while (valueReader.hasNextLine()) {
            String values = valueReader.readLine();
            String[] listVal = uncommaLine(values);
            if (listVal.length != t.rowSize()) {
                throw new ArrayIndexOutOfBoundsException();
            }
            for (String val: listVal) {
                valueDeque.addLast(val);
            }
        }
        return valueDeque;
    }


    public static Table load(String file, String name) {
        String[] labels = readLabels(file);
        Table loaded = new Table(name);
        for (int i = 0; i < labels.length; i = i + 2) {
            loaded.addColumn(Table.createColumn(labels[i], labels[i + 1]));
        }
        ArrayDeque<String> values = readValues(file, loaded);
        Cell[] cellValues = convertValues(values, loaded);
        loaded.inserts(cellValues);
        return loaded;
    }

    public static Cell[] convertValues(ArrayDeque<String> stringVals, Table target) {
        Cell[] cellValues = new Cell[stringVals.size()];
        for (int i = 0; i < stringVals.size(); i++) {
            Col targetCol = target.getColumn((i % target.rowSize()));
            String value = stringVals.get(i);
            String targetType = targetCol.getType();
            cellValues[i] = createCell(targetType, value);
        }
        return cellValues;
    }

    public static Cell createCell(String type, String value) {
        Cell newValue = null;
        if (value.equals("NOVALUE")) {
            if (type.equals("int")) {
                newValue = new IntCell();
            }
            if (type.equals("float")) {
                newValue = new DoubleCell();
            }
            if (type.equals("string")) {
                newValue = new StringCell();
            }
        } else {
            try {
                if (type.equals("int")) {
                    newValue = new IntCell(Integer.parseInt(value));
                }
                if (type.equals("float")) {
                    newValue = new DoubleCell(Double.parseDouble(value));
                }
                if (type.equals("string")) {
                    newValue = new StringCell(value);
                }
            } catch (NumberFormatException e) {
                throw new NumberFormatException(value);
            }
        }
        return newValue;
    }

    public static void main(String[] args) {
        Table t2 = TableReader.load("C:/Users/Anand/cs61b/atm/proj2/examples/fans.tbl", "t2");
        t2.getValues();
        System.out.println(t2.getValues());
    }
}
