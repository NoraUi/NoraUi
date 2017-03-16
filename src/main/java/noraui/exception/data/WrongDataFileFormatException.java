package noraui.exception.data;

public class WrongDataFileFormatException extends Exception {

    /**
     * serialUid
     */
    private static final long serialVersionUID = 4190215494417846482L;

    public WrongDataFileFormatException() {
        super();
    }

    public WrongDataFileFormatException(String message) {
        super(message);
    }

}
