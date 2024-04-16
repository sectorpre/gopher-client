public class Header {

    public static HeaderType currentHeader = HeaderType.TYPE;
    public enum HeaderType {
        TYPE,
        NAME,
        SELECTOR,
        HOST,
        PORT

    }

    public static HeaderType nextHeader() {
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
        return currentHeader;
    }

    public static String headerToString() {
        return switch (currentHeader) {
            case TYPE -> "type";
            case NAME -> "name";
            case SELECTOR -> "selector";
            case HOST -> "host";
            case PORT -> "port";
        };
    }

    public static void setHeader(HeaderType h) {
        currentHeader = h;
    }

}
