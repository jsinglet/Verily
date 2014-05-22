package verily.lang.exceptions;

import verily.lang.util.TableDiffResult;

import java.util.List;

public class TableHomomorphismException extends RuntimeException  {

    public List<TableDiffResult> errorLocations;

    public TableHomomorphismException(String s) {
        super(s);
    }

    public TableHomomorphismException(String s, List<TableDiffResult> diffs) {
        super(s);

        this.errorLocations = diffs;
    }

}
