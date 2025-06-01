package cc.suvankar.free_dictionary_api.exceptions;

public class JsonDirectoryInvalidException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public JsonDirectoryInvalidException(String message) {
        super(message);
    }

    public JsonDirectoryInvalidException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonDirectoryInvalidException(Throwable cause) {
        super(cause);
    }

}
