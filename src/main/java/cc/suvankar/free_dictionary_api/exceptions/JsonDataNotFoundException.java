package cc.suvankar.free_dictionary_api.exceptions;

public class JsonDataNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public JsonDataNotFoundException(String message) {
        super(message);
    }

    public JsonDataNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonDataNotFoundException(Throwable cause) {
        super(cause);
    }

}
