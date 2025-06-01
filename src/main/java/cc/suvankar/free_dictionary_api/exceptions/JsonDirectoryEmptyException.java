package cc.suvankar.free_dictionary_api.exceptions;

public class JsonDirectoryEmptyException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public JsonDirectoryEmptyException(String message) {
        super(message);
    }

    public JsonDirectoryEmptyException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonDirectoryEmptyException(Throwable cause) {
        super(cause);
    }

}
