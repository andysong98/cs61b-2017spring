package db;

import java.util.InputMismatchException;

/**
 * Created by Anand on 3/1/2017.
 */
public class IntCell extends Cell<Integer> {

    //Constructor for NOVALUE if no arguments are given.  Satisfies default constructor needs.
    public IntCell() {
        this("NOVALUE");
    }

    //Constructor for all valid values
    public IntCell(int val) {
        value = val;
        noValue = false;
        nanValue = false;
    }

    //Constructor for all valid values
    public IntCell(int val, boolean novalue) {
        value = val;
        noValue = novalue;
        nanValue = false;
    }

    //Contructor for NOVALUE if string value is given
    public IntCell(String val) {
        if (val.equals("NaN")) {
            value = Integer.MAX_VALUE;
            nanValue = true;
            noValue = false;
        }
        else if (val.equals("NOVALUE")) {
            value = 0;
            noValue = true;
            nanValue = false;
        } else {
            throw new InputMismatchException("WRONG INPUT TYPE");
        }
    }

    @Override
    public Cell plus(Cell other) {
        if (nanValue) {
            return this;
        } else if (other.nanValue) {
            return other;
        }
        try {
            return new IntCell(getValue() + (int) other.getValue(), noValue && other.noValue);
        } catch (ClassCastException e) {
            return new DoubleCell(getValue() + (double) other.getValue(),
                    noValue && other.noValue);
        }
    }

    @Override
    public Cell minus(Cell other) {
        if (nanValue) {
            return this;
        } else if (other.nanValue) {
            return other;
        }
        try {
            return new IntCell(getValue() - (int) other.getValue(), noValue && other.noValue);
        } catch (ClassCastException e) {
            return new DoubleCell(getValue() - (double) other.getValue(),
                    noValue && other.noValue);
        }
    }

    @Override
    public Cell star(Cell other) {
        if (nanValue) {
            return this;
        } else if (other.nanValue) {
            return other;
        }
        try {
            return new IntCell(getValue() * (int) other.getValue(), noValue && other.noValue);
        } catch (ClassCastException e) {
            return new DoubleCell(getValue() * (double) other.getValue(),
                    noValue && other.noValue);
        }
    }

    @Override
    public Cell div(Cell other) {
        if (nanValue) {
            return this;
        } else if (other.nanValue) {
            return other;
        }
        Cell cell;
        try {
            cell = new IntCell(getValue() / (int) other.getValue(), noValue && other.noValue);
            if (other.getValue().equals(0)) {
                return new IntCell("NaN");
            }
        } catch (ClassCastException e) {
            cell = new DoubleCell(getValue() / (double) other.getValue(),
                    noValue && other.noValue);
            if (other.getValue().equals(0.0)) {
                return new DoubleCell("NaN");
            }
        }
        return cell;
    }

    @Override
    public boolean equal(Cell other) {
        if (nanValue && other.nanValue) {
            return true;
        }
        if (noValue || other.noValue)
            return false;
        return new Double(value.toString()).equals(new Double(other.getValue().toString()));
    }

    @Override
    public boolean gt(Cell other) {
        if (nanValue && !other.nanValue) {
            return true;
        }
        if (noValue || other.noValue)
            return false;
        return new Double(value.toString()) > new Double(other.getValue().toString());
    }

    @Override
    public boolean lt(Cell  other) {
        if (!nanValue && other.nanValue) {
            return true;
        }
        if (noValue || other.noValue)
            return false;
        return new Double(value.toString()) < new Double(other.getValue().toString());
    }

    @Override
    public boolean equal(String other) {
        if (nanValue) {
            return false;
        }
        if (noValue || nanValue)
            return false;
        return new Double(value.toString()).equals(Double.parseDouble(other));
    }

    @Override
    public boolean gt(String other) {
        if (nanValue) {
            return true;
        }
        if (noValue)
            return false;
        return new Double(value.toString()) > Double.parseDouble(other);
    }

    @Override
    public boolean lt(String other) {
        if (nanValue) {
            return false;
        }
        if (noValue || nanValue)
            return false;
        return new Double(value.toString()) < Double.parseDouble(other);
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public String toString() {
        if (noValue) {
            return "NOVALUE";
        }
        if (nanValue || Double.isInfinite(getValue().doubleValue())) {
            return "NaN";
        } else {
            return getValue().toString();
        }
    }

    public IntCell duplicate() {
        return new IntCell(value);
    }
}
