package db;

import java.util.InputMismatchException;

/**
 * Created by Anand on 3/1/2017.
 */
public class StringCell extends Cell<String> {


    public StringCell() {
        this("NOVALUE");
    }

    public StringCell(String val) {
        nanValue = false;
        if (val.equals("NOVALUE") || val.equals("")) {
            value = "";
            noValue = true;
        } else {
            value = val;
            noValue = false;
        }
    }

    @Override
    public StringCell plus(Cell other) {
        return new StringCell(getValue() + other.getValue());
    }

    @Override
    public StringCell minus(Cell other) {
        throw new InputMismatchException("ERROR: string does not support - operator.");
    }

    @Override
    public StringCell star(Cell other) {
        throw new InputMismatchException("ERROR: string does not support * operator.");
    }

    @Override
    public StringCell div(Cell other) {
        throw new InputMismatchException("ERROR: string does not support / operator.");
    }

    @Override
    public boolean equal(Cell other) {
        if (noValue || other.noValue)
            return false;
        return getValue().equals(other.getValue());
    }

    @Override
    public boolean gt(Cell other) {
        if (noValue || other.noValue)
            return false;
        return getValue().compareTo((String) other.getValue()) > 0;
    }

    @Override
    public boolean lt(Cell  other) {
        if (noValue || other.noValue)
            return false;
        return getValue().compareTo((String) other.getValue()) < 0;
    }

    public boolean equal(String other) {
        if (noValue)
            return false;
        return getValue().equals(other.substring(1, other.length() - 1));
    }

    @Override
    public boolean gt(String other) {
        if (noValue)
            return false;
        return getValue().compareTo(other.substring(1, other.length() - 1)) > 0;
    }

    @Override
    public boolean lt(String  other) {
        if (noValue)
            return false;
        return getValue().compareTo(other.substring(1, other.length() - 1)) < 0;
    }

    @Override
    public String getValue() {
        return value;
    }

    public String toString() {
        if (noValue) {
            return "NOVALUE";
        } else {
            return "'" + value + "'";
        }
    }

    public StringCell duplicate() {
        return new StringCell(value);
    }
}
