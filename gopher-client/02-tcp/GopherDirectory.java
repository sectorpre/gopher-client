import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;

public class GopherDirectory extends GopherResponse {
    HashSet<DirectoryEntry> filePaths = new HashSet<>();

    public GopherDirectory(String host, String filepath) {
        super(host, filepath);
    }

    @Override
    public void read(Socket sock) throws IOException, MalformedDirectory {
        int     ch;
        int skipPrint = 0;
        String portAcc = "";
        DirectoryEntry de = new DirectoryEntry();
        HashSet<DirectoryEntry> paths = new HashSet<>();

        while (true) {
            ch = sock.getInputStream().read();;

            // checks for special characters
            if (ch < 0) {break;}
            else if (ch == '\n') {
                //System.out.printf("host:%s selector:%s port:%s\n", de.host,de.selector,portAcc );
                if (skipPrint == 0 ) {
                    if (Header.currentHeader != Header.HeaderType.PORT) {
                        throw new MalformedDirectory();
                    }
                    de.port = Integer.valueOf(portAcc);
                    paths.add(de);
                }
                de = new DirectoryEntry();
                portAcc = "";
                Header.setHeader(Header.HeaderType.TYPE);
                skipPrint = 0;
                continue;
            }
            else if (ch == '\t') {
                Header.nextHeader();
                continue;
            }

            if (skipPrint == 1) continue;

            // checks TYPE byte
            if (Header.currentHeader == Header.HeaderType.TYPE) {
                // of type "information" so skip
                if (ch == 105 || ch == 46) {skipPrint = 1;}
                de.type = ch;
                Header.nextHeader();
            }
            else if (Header.currentHeader == Header.HeaderType.SELECTOR) {de.selector += (char) ch;}
            else if (Header.currentHeader == Header.HeaderType.HOST) {de.host += (char) ch;}
            else if (Header.currentHeader == Header.HeaderType.PORT) {
                if (ch == 13) {continue;}
                portAcc += (char) ch;
            }
        }
        this.filePaths = paths;
    }

    @Override
    public void addToStats(String ip) {
        super.addToStats(ip);
        GopherStats.dirMap.add(this);

    }
}
