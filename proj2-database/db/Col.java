package db;

/**
 * Created by Anand on 2/27/2017.
 */
public class Col<T> {
    ArrayDeque<Cell> backEnd;
    String colName;
    String colType;

    //Constructor with name of Col as the label
    public Col(String name, String type) {
        backEnd = new ArrayDeque<>();
        colType = type;
        colName = name;
    }

    //Retrieves name of the column without the type
    public String getName() {
        return colName;
    }

    //Retrieves name of the column with the type
    public String getLabel() {
        return colName + " " + colType;
    }

    //Inserts a cell into the column.  Adds it to the end of backEnd
    public void inserts(Cell item) {
        backEnd.addLast(item);
    }

    //Returns the amount of items in a column
    public int size() {
        return backEnd.size();
    }

    //Returns the primitive values represented in the column
    public T get(int i) {
        Cell<T> curr = backEnd.get(i);
        return curr.getValue();
    }

    public Cell getCell(int i) {
        Cell<T> target = backEnd.get(i);
        return target;
    }

    public String cellString(int i) {
        Cell<T> curr = backEnd.get(i);
        return curr.toString();
    }

    public Cell duplicate(int i) {
        Cell<T> targetCell = getCell(i);
        Cell<T> dupCell = targetCell.duplicate();
        return dupCell;
    }

    //Returns the last primitive value represented in the column
    public T getLast() {
        int last = backEnd.size() - 1;
        return get(last);
    }

    public String getType() {
        return colType;
    }

}
