public class Header {

    public HeaderType currentHeader = HeaderType.TYPE;
    public static enum HeaderType {
        TYPE,
        NAME,
        SELECTOR,
        HOST,
        PORT

    }

    public HeaderType nextHeader() {
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

    public String headerToString() {
        return switch (this.currentHeader) {
            case TYPE -> "type";
            case NAME -> "name";
            case SELECTOR -> "selector";
            case HOST -> "host";
            case PORT -> "port";
        };
    }

    public void setHeader(HeaderType h) {
        currentHeader = h;
    }

}
