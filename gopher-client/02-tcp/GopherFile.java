import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

public class GopherFile extends GopherResponse {
    int size;
    Integer fileType;
    ByteBuffer fileData;

    public GopherFile(String host ,String filepath, Integer ft) {
        super(host, filepath);
        fileType = ft;

    }

    public void read(Socket sock) throws IOException {
        int     ch;
        int responseSize = 0;
        do {
            ch = sock.getInputStream().read();
            ;
            responseSize += 1;
        } while (ch >= 0);
        this.size = responseSize;
    }
}

