package db;

import java.util.InputMismatchException;

/**
 * Created by Anand on 3/1/2017.
 */
public class DoubleCell extends Cell<Double> {

    //Constructor for NOVALUE if no arguments are given.  Satisfies default constructor needs.
    public DoubleCell() {
        this("NOVALUE");
    }

    //Constructor for all valid values
    public DoubleCell(double val) {
        if (val == (1.0/0)) {
            value = Double.MAX_VALUE;
            nanValue = true;
        } else {
            value = val;
            nanValue = false;
        }
        noValue = false;
    }

    //Constructor for all valid values
    public DoubleCell(double val, boolean novalue) {
        if (val == (1.0/0)) {
            value = Double.MAX_VALUE;
            nanValue = true;
        } else {
            value = val;
            nanValue = false;
        }
        noValue = novalue;
    }

    //Contructor for NOVALUE if string value is given
    public DoubleCell(String val) {
        if (val.equals("NaN")) {
            value = Double.MAX_VALUE;
            nanValue = true;
            noValue = false;
        }
        else if (val.equals("NOVALUE")) {
            value = 0.0;
            noValue = true;
            nanValue = false;
        } else {
            throw new InputMismatchException("WRONG INPUT TYPE");
        }
    }

    @Override
    public DoubleCell plus(Cell other) {
        if (nanValue) {
            return this;
        } else if (other.nanValue) {
            return new DoubleCell("NaN");
        }
        return new DoubleCell(getValue() + (new Double(other.getValue().toString())),
                noValue && other.noValue);
    }

    @Override
    public DoubleCell minus(Cell other) {
        if (nanValue) {
            return this;
        } else if (other.nanValue) {
            return new DoubleCell("NaN");
        }
        return new DoubleCell(getValue() - (new Double(other.getValue().toString())),
                noValue && other.noValue);
    }

    @Override
    public DoubleCell star(Cell other) {
        if (nanValue) {
            return this;
        } else if (other.nanValue) {
            return new DoubleCell("NaN");
        }
        return new DoubleCell(getValue() * (new Double(other.getValue().toString())),
                noValue && other.noValue);
    }

    @Override
    public DoubleCell div(Cell other) {
        if (nanValue) {
            return this;
        } else if (other.nanValue) {
            return new DoubleCell("NaN");
        }
        Double o = new Double(other.getValue().toString());
        if (o.equals(0)) {
           return new DoubleCell("NaN");
        }
        return new DoubleCell(getValue() / o, noValue && other.noValue);
    }

    @Override
    public boolean equal(Cell other) {
        if (nanValue && other.nanValue) {
            return true;
        }
        if (noValue || other.noValue)
            return false;
        return getValue().equals((new Double(other.getValue().toString())));
    }

    @Override
    public boolean gt(Cell other) {
        if (nanValue && !other.nanValue) {
            return true;
        }
        if (noValue || other.noValue)
            return false;
        return getValue() > ((new Double(other.getValue().toString())));
    }

    @Override
    public boolean lt(Cell  other) {
        if (!nanValue && other.nanValue) {
            return true;
        }
        if (noValue || other.noValue)
            return false;
        return getValue() < ((new Double(other.getValue().toString())));
    }
    
    @Override
    public boolean equal(String other) {
        if (nanValue) {
            return false;
        }
        if (noValue)
            return false;
        return getValue().equals(Double.parseDouble(other));
    }

    @Override
    public boolean gt(String other) {
        if (nanValue) {
            return true;
        }
        if (noValue)
            return false;
        return getValue() > Double.parseDouble(other);
    }

    @Override
    public boolean lt(String  other) {
        if (nanValue) {
            return false;
        }
        if (noValue)
            return false;
        return getValue() < Double.parseDouble(other);
    }
    
    @Override
    public Double getValue() {
        return value;
    }

    public DoubleCell duplicate() {
        return new DoubleCell(value);
    }

    @Override
    public String toString() {
        if (noValue) {
            return "NOVALUE";
        }
        else if (nanValue || Double.isInfinite(getValue())) {
            return "NaN";
        } else {
            return String.format("%.3f", value);
        }
    }
}
