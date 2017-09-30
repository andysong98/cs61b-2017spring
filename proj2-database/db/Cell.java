package db;

/**
 * Created by Anand on 2/28/2017.
 */
public class Cell<T> {
    T value;
    boolean noValue;
    boolean nanValue;

    //Creates Cell object with null as the value
    public Cell() {
    }

    //Creates Cell object with val as the value
    public Cell(T val) {
        value = val;
        if (val.equals("NOVALUE")) {
            noValue = true;
        } else {
            noValue = false;
        }
    }

    /*Returns the value associated with a Cell.
    If noValue is True, then it returns the empty version of its respective type*/
    public T getValue() {
        return value;
    }

    /*Returns the string representation of the value of noValue is False.
    NOVALUE otherwise*/
    @Override
    public String toString() {
        if (nanValue) {
            return "NaN";
        }
        if (noValue) {
            return "NOVALUE";
        }
        return value.toString();
    }

    public Cell<T> plus(Cell other) {
        return null;
    }

    public Cell<T> minus(Cell other) {
        return null;
    }

    public Cell<T> star(Cell other) {
        return null;
    }

    public Cell<T> div(Cell other) {
        return null;
    }

    public boolean equal(Cell other) {
        return true;
    }

    public boolean gt(Cell other) {
        return true;
    }

    public boolean lt(Cell other) {
        return true;
    }

    public boolean equal(String other) {
        return true;
    }

    public boolean gt(String other) {
        return true;
    }

    public boolean lt(String other) {
        return true;
    }

    public Cell duplicate() {
        return new Cell(value);
    }

}

