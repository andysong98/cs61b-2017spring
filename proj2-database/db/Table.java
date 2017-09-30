package db;

import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.jar.Attributes;
import java.util.regex.Pattern;

/**
 * Created by Anand on 2/27/2017.
 */

public class Table {
    String name;
    int numcols;
    ArrayDeque<Col> columns;
    HashMap<String, Col> colmap;

    //Constructs an empty table with no columns and assigns it the given inName
    public Table(String inName) {
        if (!Pattern.matches(Database.ID, inName)) {
            throw new ArrayIndexOutOfBoundsException();
        }
        columns = new ArrayDeque<>();
        numcols = 0;
        name = inName;
        colmap = new HashMap<>();
    }

    //Adds a column to the table
    public void addColumn(Col newCol) {
        if (colmap.containsKey(newCol.getName())) {
            throw new NumberFormatException(newCol.getName());
        } else {
            colmap.put(newCol.getName(), newCol);
            columns.addLast(newCol);
            numcols += 1;
        }
    }

    //Creates and returns a column
    public static Col createColumn(String colName, String type) {
        if (type.equals("int")) {
            Col<Integer> newCol = new Col<>(colName, type);
            return newCol;
        }
        if (type.equals("float")) {
            Col<Double> newCol = new Col<>(colName, type);
            return newCol;
        }
        if (type.equals("string")) {
            Col<String> newCol = new Col<>(colName, type);
            return newCol;
        } else {
            throw new InputMismatchException("ERROR: Invalid type: type");
        }
    }

    //Returns the number of items in the table's column (or the number of rows in a table)
    public int colSize() {
        Col sample = getColumn(0);
        return sample.size();
    }

    //Returns the number of rows in a column
    public int rowSize() {
        return numcols;
    }


    public Col getColumn(int i) {
        return columns.get(i);
    }

    public Col getColumn(String colName) {
        if (colmap.containsKey(colName)) {
            Col target = colmap.get(colName);
            return target;
        } else {
            throw new InputMismatchException("Column Not Found: " + colName);
        }
    }

    //Inserts the cell values in a given array into the table
    public void inserts(Cell[] values) {
        if ((values.length % numcols) != 0) {
            throw new InputMismatchException("");
        }

        int i = 0;
        for (Cell b: values) {
            Col currCol = columns.get(i % numcols);
            currCol.inserts(b);
            i += 1;
        }
    }

    //Returns the string representation of the whole table
    public String getValues() {
        String values = "";
        if (numcols == 0) {
            return values;
        }
        Col currCol = columns.get(0);

        for (int i = 0; i < numcols; i++) {
            currCol = columns.get(i);
            values += currCol.getLabel() + ",";
        }
        if (values.length() > 0) {
            values = values.substring(0, values.length() - 1);
        }
        values += System.lineSeparator();

        for (int j = 0; j < currCol.size(); j++) {
            for (int i = 0; i < numcols; i++) {
                currCol = columns.get(i);
                values += currCol.cellString(j) + ",";
            }
            if (values.length() > 0) {
                values = values.substring(0, values.length() - 1);
            }
            values += System.lineSeparator();
        }
        return values;
    }
}
