/**
 * Class representing the headers for a directoryEntry listing for
 * a Gopher response. Used mainly by the GopherDirectory class in
 * the read method. Uses a singleton class as we only require one
 * instance of it.
 *
 * */
public class Header {

    private static Header instance;

    // Field representing the currentHeader being processed by the socket
    public HeaderType currentHeader = HeaderType.TYPE;

    /**
     * enum representing each field the socket could be currently processing
     * */
    public enum HeaderType {
        TYPE,
        NAME,
        SELECTOR,
        HOST,
        PORT

    }

    public static Header getInstance() {
        if (instance == null) {
            instance = new Header();
        }
        return instance;
    }

    /**
     * Method for rotating to the next header.
     */
    public void nextHeader() {
        switch (currentHeader) {
            case TYPE:
                currentHeader = HeaderType.NAME;
                break;
            case NAME:
                currentHeader = HeaderType.SELECTOR;
                break;
            case SELECTOR:
                currentHeader = HeaderType.HOST;
                break;
            case HOST:
                currentHeader = HeaderType.PORT;
                break;
            case PORT:
                currentHeader = HeaderType.TYPE;
                break;
        }
    }


    public void setHeader(HeaderType h) {
        currentHeader = h;
    }

}
