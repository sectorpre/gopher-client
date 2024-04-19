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

    @Override
    public void read(Socket sock) throws IOException, DataExceedException {
        int     ch;
        int responseSize = 0;
        do {
            if (responseSize > 100000) {
                throw new GopherResponse.DataExceedException();
            }
            ch = sock.getInputStream().read();
            ;
            responseSize += 1;
        } while (ch >= 0);
        this.size = responseSize;
    }

    @Override
    public void addToStats(String ip) {
        super.addToStats(ip);
        if (this.fileType == 57) {
            GopherStats.binaryMap.add(this);
        }
        else if (this.fileType == 48) {
            GopherStats.textMap.add(this);
        }
    }
}

