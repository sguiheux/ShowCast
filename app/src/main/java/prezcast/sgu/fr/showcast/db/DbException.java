package prezcast.sgu.fr.showcast.db;

/**
 * Exception when error occurs during database access.
 */
public class DbException extends Exception {
    public DbException(String s) {
        super(s);
    }
}
