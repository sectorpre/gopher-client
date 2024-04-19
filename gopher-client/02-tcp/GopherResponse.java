import java.io.IOException;
import java.net.Socket;

public abstract class GopherResponse {
    public String selector;
    public String host;

    public GopherResponse(String host, String selector) {
        this.host = host;
        this.selector = selector;
    }

    public void read(Socket sock) throws IOException {}
}


