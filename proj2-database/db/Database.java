package db;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Database {
    Map<String, Table> stored;

    private static final String REST  = "\\s*(.*)\\s*",
                                COMMA = "\\s*,\\s*",
                                AND   = "\\s+and\\s+",
                                AS    = "\\s+as\\s+",
                                COL_OPER = "\\s*[+\\-*/]\\s*",
                                COND_OPER = "\\s*(?:==|!=|<=|>=|<|>)\\s*";

    // create table t1 (X int, Y int)

    // Stage 1 syntax, contains the command name.
    private static final Pattern CREATE_CMD = Pattern.compile("\\s*create\\s+table\\s+" + REST),
                                 LOAD_CMD   = Pattern.compile("\\s*load\\s+" + REST),
                                 STORE_CMD  = Pattern.compile("\\s*store\\s+" + REST),
                                 DROP_CMD   = Pattern.compile("\\s*drop\\s+table\\s+" + REST),
                                 INSERT_CMD = Pattern.compile("\\s*insert\\s+into\\s+" + REST),
                                 PRINT_CMD  = Pattern.compile("\\s*print\\s+" + REST),
                                 SELECT_CMD = Pattern.compile("\\s*select\\s+" + REST);


    // Stage 2 syntax, contains the clauses of commands.
    // create table t1 (X int, Y int)
    // Regex for single (table and column) identifier.
    public static final String ID = "[A-Za-z](?:\\w|\\d)*";
    //
    private static final String ARITHMETIC = ID + COL_OPER + ID + "(?:\\s+as\\s+" + ID + ")";
    // Regex for single column expression.
    private static final String COL_EXPR = "(?:" + ID + "|" + ARITHMETIC + ")";
    // Regex for a single literal.
    private static final String LITERAL = "(?:[+-]?[0-9]*\\.?[0-9]+|'[^'\"\n\t,]*')";
    private static final String COND_STMT = ID + COND_OPER + "(?:"
            + LITERAL + "|" + ID + ")";
    private static final Pattern CREATE_NEW  = Pattern.compile("(" + ID + ")\\s+\\(\\s*("
            + ID + "\\s+\\w+\\s*" + "(?:,\\s*" + ID + "\\s+\\w+\\s*)*)\\)"),
                                 SELECT_CLS  = Pattern.compile("(" + COL_EXPR + "\\s*(?:,\\s*"
                                                + COL_EXPR + "\\s*)*|\\*)\\s+from\\s+"
                                                + "(" + ID + "\\s*(?:,\\s*" + ID
                                                + "\\s*)*)(?:\\s+where\\s+(" + COND_STMT
                                                + "(?:\\s+and\\s+" + COND_STMT + ")*))?"),
                                 CREATE_SEL  = Pattern.compile("(" + ID + ")\\s+as select\\s+"
                                                + SELECT_CLS.pattern()),
                                 INSERT_CLS  = Pattern.compile("(" + ID + ")\\s+values\\s+((?:"
                                         + LITERAL + "|" + ID + ")\\s*(?:,\\s*(?:" + LITERAL
                                         + "|" + ID + ")\\s*)*)");


    public Database() {
        stored = new HashMap<>();
    }

    /*Creates table and stores it*/
    public void create(String expr) {
        Matcher m;
        if ((m = CREATE_NEW.matcher(expr)).matches()) {
            Table newT = new Table(m.group(1));
            if (stored.containsKey(m.group(1))) {
                throw new NullPointerException("ERROR: Table already exists: " + m.group(1));
            }
            String[] cols = m.group(2).split(COMMA), splitStr;
            for (int i = 0; i < cols.length; i++) {
                splitStr = cols[i].split("\\s+");
                newT.addColumn(Table.createColumn(splitStr[0], splitStr[1]));
            }
            stored.put(m.group(1), newT);
        } else if ((m = CREATE_SEL.matcher(expr)).matches()) {
            // Implement `select` command.
            String[] conds = (m.group(4) == null) ? new String[0] : m.group(4).split(AND);
            Table newT;
            newT = select(m.group(2).split(COMMA), m.group(3).split(COMMA), conds, m.group(1));
            stored.put(m.group(1), newT);
        } else {
            throw new ArrayIndexOutOfBoundsException("ERROR: Malformed create: " + expr);
        }
    }

    /*Insert literals into next row of column*/
    public void insert(String expr) {
        Matcher m;
        if ((m = INSERT_CLS.matcher(expr)).matches()) {
            Table table = stored.get(m.group(1));
            if (table == null) {
                throw new NullPointerException("ERROR: No such table: " +  m.group(1));
            }
            String[] literals = m.group(2).split(COMMA);
            if (literals.length == table.rowSize()) {
                ArrayDeque<String> ad = new ArrayDeque<>();
                Pattern lit = Pattern.compile(LITERAL);
                for (int i = 0; i < literals.length; i++) {
                    if (!(literals[i].equals("NOVALUE"))
                            && !(m = lit.matcher(literals[i])).matches()) {
                        throw new NumberFormatException("Malformed insert query: " + literals[i]);
                    }
                    Col column = table.getColumn(i);
                    // Do type checking here to make sure the literal matches the column type.
                    if (column.getType().equals("string") && !(literals[i].equals("NOVALUE"))) {
                        if (literals[i].substring(0, 1).equals("'")
                                && literals[i].substring(literals[i].length()
                                - 1, literals[i].length()).equals("'")) {
                            ad.addLast(literals[i].substring(1, literals[i].length() - 1));
                        } else {
                            throw new NumberFormatException("ERROR: Row does not match table");
                        }
                    } else {
                        ad.addLast(literals[i]);
                    }
                }
                table.inserts(TableReader.convertValues(ad, table));
            } else {
                throw new NumberFormatException("ERROR: Row does not match table%n");
            }
        } else {
            System.err.printf("Malformed insert: " +  expr);
            throw new NumberFormatException("ERROR: Row does not match table");
        }
    }

    /*Saves file as a tbl*/
    public void save(String name) {
        if (stored.containsKey(name)) {
            Table table = stored.get(name);
            try {
                File file = new File(name + ".tbl");
                PrintWriter writer = new PrintWriter(file);
                writer.print(table.getValues());
                writer.close();
            } catch (IOException ie) {
                System.err.printf("File not found: %s%n", ie.getMessage());
            }
        } else {
            throw new InputMismatchException("ERROR: NO SUCH TABLE");
        }
    }

    public void load(String tablename) {
        String filePath = tablename + ".tbl";
        Table loading = TableReader.load(filePath, tablename);
        stored.put(tablename, loading);
    }

    public void delete(String tablename) {
        if (stored.containsKey(tablename)) {
            stored.remove(tablename);
        } else {
            throw new InputMismatchException("NO SUCH TABLE:" + tablename);
        }
    }

    public String printTable(String name) {
        if (!stored.containsKey(name)) {
            return "ERROR: No such table: " + name;
        }
        Table table = stored.get(name);
        return table.getValues();
    }

    private static Table merge(Table A, Table B, String resultTableName) {
        ArrayDeque<Col> sharedColumnsA = new ArrayDeque<>();
        ArrayDeque<Col> sharedColumnsB = new ArrayDeque<>();
        ArrayDeque<Col> columnsUniqueToA = new ArrayDeque<>();
        ArrayDeque<Col> columnsUniqueToB = new ArrayDeque<>();
        Table result = new Table(resultTableName);
        for (Col colInA: A.colmap.values()) {
            try {
                Col colInB = B.getColumn(colInA.getName());
                if (!colInA.getType().equals(colInB.getType())) {
                    throw new InputMismatchException("ERROR: Incompatible types: "
                            + colInA.getType() + " and " + colInB.getType());
                }
                sharedColumnsA.addLast(colInA); sharedColumnsB.addLast(colInB);
            } catch (InputMismatchException e) {
                columnsUniqueToA.addLast(colInA);
            }
        }

        for (Col colInB: B.colmap.values()) {
            try {
                A.getColumn(colInB.getName());
            } catch (InputMismatchException e) {
                columnsUniqueToB.addLast(colInB);
            }
        }
        for (int i = 0; i < sharedColumnsA.size(); i++) {
            result.addColumn(Table.createColumn(sharedColumnsA.get(i).getName(),
                    sharedColumnsA.get(i).getType()));
        }
        for (int i = 0; i < columnsUniqueToA.size(); i++) {
            result.addColumn(Table.createColumn(columnsUniqueToA.get(i).getName(),
                    columnsUniqueToA.get(i).getType()));
        }
        for (int i = 0; i < columnsUniqueToB.size(); i++) {
            result.addColumn(Table.createColumn(columnsUniqueToB.get(i).getName(),
                    columnsUniqueToB.get(i).getType()));
        }
        for (int rowA = 0; rowA < A.colSize(); rowA++) {
            for (int rowB = 0; rowB < B.colSize(); rowB++) {
                Cell[] row = new Cell[result.rowSize()];
                if (sharedColumnsA.size() > 0) {
                    boolean matches = false;
                    for (int i = 0; i < sharedColumnsA.size(); i++) {
                        matches = sharedColumnsA.get(i).get(rowA)
                                .equals(sharedColumnsB.get(i).get(rowB));
                        if (!matches) {
                            break;
                        }
                        row[i] = sharedColumnsA.get(i).duplicate(rowA);
                    }
                    if (matches) {
                        int index = sharedColumnsA.size();
                        for (int n = 0; n < columnsUniqueToA.size(); n++) {
                            row[index] = columnsUniqueToA.get(n).duplicate(rowA);
                            index++;
                        }
                        for (int n = 0; n < columnsUniqueToB.size(); n++) {
                            row[index] = columnsUniqueToB.get(n).duplicate(rowB);
                            index++;
                        }
                        result.inserts(row);
                    }
                } else {
                    // Cartesian product
                    int index = 0;
                    for (int n = 0; n < columnsUniqueToA.size(); n++) {
                        row[index] = columnsUniqueToA.get(n).duplicate(rowA);
                        index++;
                    }
                    for (int n = 0; n < columnsUniqueToB.size(); n++) {
                        row[index] = columnsUniqueToB.get(n).duplicate(rowB);
                        index++;
                    }
                    result.inserts(row);
                }
            }
        }
        return result;
    }

    private String select(String expr) {
        Matcher m = SELECT_CLS.matcher(expr);

        if (!m.matches()) {
            return "ERROR: Malformed select: " + expr + "\n";
        }
        String[] conds = (m.group(3) == null) ? new String[0] : m.group(3).split(AND);
        try {
            return select(m.group(1).split(COMMA), m.group(2).split(COMMA),
                    conds, "temp").getValues();
        } catch (InputMismatchException e) {
            return "ERROR: " + e.getMessage();
        } catch (ClassCastException c) {
            return "ERROR: " + c.getMessage();
        } catch (NumberFormatException e) {
            return "ERROR: " + e.getMessage();
        }
    }

    private Table selectTableChecker(String[] tables, String resultTableName) {
        Table joined = null;
        for (int i = 0; i < tables.length; i++) {
            if (stored.containsKey(tables[0])) {
                if (i == 0) {
                    joined = stored.get(tables[i]);
                } else {
                    joined = merge(joined, stored.get(tables[i]), resultTableName);
                }
            } else {
                throw new InputMismatchException("ERROR: NO SUCH TABLE: " + tables[0]);
            }
        }
        return joined;
    }

    private void selectModifyer(String[] exprs, Table expressed, Table joined) {
        Matcher m;
        Pattern art = Pattern.compile(ARITHMETIC);
        for (String expression : exprs) {
            if ((m = art.matcher(expression)).matches()) {
                String[] components = expression.split(AS);
                String[] operands = components[0].split(COL_OPER);
                Col first = joined.getColumn(operands[0].replaceAll("//s+", ""));
                Col second = joined.getColumn(operands[1].replaceAll("//s+", ""));
                String type = first.getType();
                if (second.getType().equals("float")) {
                    type = second.getType();
                }
                Col resultColumn = Table.createColumn(components[1], type);
                for (int row = 0; row < joined.colSize(); row++) {
                    if (expression.contains("+")) {
                        resultColumn.inserts(first.getCell(row).plus(second.getCell(row)));
                    } else if (expression.contains("-")) {
                        resultColumn.inserts(first.getCell(row).minus(second.getCell(row)));
                    } else if (expression.contains("*")) {
                        resultColumn.inserts(first.getCell(row).star(second.getCell(row)));
                    } else {
                        resultColumn.inserts(first.getCell(row).div(second.getCell(row)));
                    }
                }
                expressed.addColumn(resultColumn);
            } else {
                expressed.addColumn(joined.getColumn(expression));
            }
        }
    }
    private Table select(String[] exprs, String[] tables, String[] conds, String resultTableName) {
        // Handle NaN and NoValue business in Cell arithmetic and condition methods.
        // Can you reference X + Y in a where condition
        // (e.g., select X + Y as b from t1 where b < 1)?
        Table joined = selectTableChecker(tables, resultTableName);
        Table expressed;
        if (exprs[0].equals("*")) {
            expressed = joined;
        } else {
            expressed = new Table(resultTableName); Matcher m;
            Pattern art = Pattern.compile(ARITHMETIC);
            selectModifyer(exprs, expressed, joined);
        }
        Table result;
        if (conds.length > 0) {
            // Handle conditions.
            result = new Table(resultTableName);
            Pattern L = Pattern.compile(LITERAL); Matcher m;
            for (int col = 0; col < expressed.rowSize(); col++) {
                result.addColumn(Table.createColumn(expressed.getColumn(col)
                        .getName(), expressed.getColumn(col).getType()));
            }
            for (int row = 0; row < expressed.colSize(); row++) {
                boolean passed = true;
                for (String condition : conds) {
                    String[] operands = condition.split(COND_OPER);
                    Cell first = expressed.getColumn(operands[0]).getCell(row);
                    // Move these condition calls into a function to avoid duplicating code.
                    if ((m = L.matcher(operands[1])).matches()) {
                        if (condition.contains("==")) {
                            passed = first.equal(operands[1]);
                        } else if (condition.contains("!=")) {
                            passed = !first.equal(operands[1]);
                        } else if (condition.contains("<=")) {
                            passed = first.lt(operands[1]) || first.equal(operands[1]);
                        } else if (condition.contains(">=")) {
                            passed = first.gt(operands[1]) || first.equal(operands[1]);
                        } else if (condition.contains("<")) {
                            passed = first.lt(operands[1]);
                        } else if (condition.contains(">")) {
                            passed = first.gt(operands[1]);
                        }
                    } else {
                        Cell second = expressed.getColumn(operands[1]).getCell(row);
                        if (condition.contains("==")) {
                            passed = first.equal(second);
                        } else if (condition.contains("!=")) {
                            passed = !first.equal(second);
                        } else if (condition.contains("<=")) {
                            passed = first.lt(second) || first.equal(second);
                        } else if (condition.contains(">=")) {
                            passed = first.gt(second) || first.equal(second);
                        } else if (condition.contains("<")) {
                            passed = first.lt(second);
                        } else if (condition.contains(">")) {
                            passed = first.gt(second);
                        }
                    }
                    if (!passed) {
                        break;
                    }
                }
                if (passed) {
                    Cell[] rowValues = new Cell[expressed.rowSize()];
                    for (int col = 0; col < expressed.rowSize(); col++) {
                        rowValues[col] = expressed.getColumn(col).getCell(row);
                    }
                    result.inserts(rowValues);
                }
            }
        } else {
            result = expressed;
        }
        return result;
    }

    public String transact(String query) {
        Matcher m;
        if ((m = CREATE_CMD.matcher(query)).matches()) {
            try {
                create(m.group(1));
            } catch (NullPointerException e) {
                return e.getMessage();
            } catch (NumberFormatException e) {
                return "ERROR: Duplicate column name: " + e.getMessage();
            } catch (ClassCastException e) {
                return "ERROR: " + e.getMessage();
            } catch (InputMismatchException e) {
                return e.getMessage();
            } catch (ArrayIndexOutOfBoundsException e) {
                return e.getMessage();
            }
        } else if ((m = LOAD_CMD.matcher(query)).matches()) {
            try {
                load(m.group(1));
            } catch (InputMismatchException e) {
                return "ERROR: Invalid type: " + e.getMessage();
            } catch (NumberFormatException e) {
                return "ERROR: Malformed data entry: " + e.getMessage();
            } catch (IllegalArgumentException e) {
                return "ERROR: No such TBL file: " + m.group(1) + ".tbl";
            } catch (ArrayIndexOutOfBoundsException e) {
                return "ERROR: Malformed Table";
            }
        } else if ((m = STORE_CMD.matcher(query)).matches()) {
            try {
                save(m.group(1));
            } catch (InputMismatchException e) {
                return "ERROR: No such table: " + m.group(1);
            }
        } else if ((m = DROP_CMD.matcher(query)).matches()) {
            try {
                delete(m.group(1));
            } catch (InputMismatchException e) {
                return "ERROR: No such table: " + m.group(1);
            }
        } else if ((m = INSERT_CMD.matcher(query)).matches()) {
            try {
                insert(m.group(1));
            } catch (NumberFormatException e) {
                return "ERROR: Row does not match table";
            } catch (NullPointerException e) {
                return e.getMessage();
            }
        } else if ((m = PRINT_CMD.matcher(query)).matches()) {
            return printTable(m.group(1));
        } else if ((m = SELECT_CMD.matcher(query)).matches()) {
            return select(m.group(1));
        } else {
            return "ERROR: Malformed query: " + query + "\n";
        }
        return " ";
    }

    public static void main(String[] args) {
        Double a = 5.0 / 0.0;
        Double c = 5 / a;
        System.out.print(c);
        Database b = new Database();
        b.transact("create table t1 (X int, Y float, Z string)");
        b.transact("insert into t1 values NOVALUE, NOVALUE, NOVALUE");
    }
}
