package cc.suvankar.free_dictionary_api.exceptions;

public class JsonFileParseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public JsonFileParseException(String message) {
        super(message);
    }

    public JsonFileParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonFileParseException(Throwable cause) {
        super(cause);
    }

}
