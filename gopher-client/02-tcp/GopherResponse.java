public abstract class GopherResponse {
    public String selector;
    public String host;

    public GopherResponse(String host, String selector) {
        this.host = host;
        this.selector = selector;
    }
}


