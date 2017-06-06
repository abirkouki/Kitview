package exception;

/**
 * Created by Administrateur on 19/05/2017.
 */

public class FolderNonExistent extends Exception {

    public FolderNonExistent(String message) {
        super(message);
    }

    public FolderNonExistent(String message, Throwable throwable) {
        super(message, throwable);
    }

}