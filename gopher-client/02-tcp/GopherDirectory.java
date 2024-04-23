import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;

public class GopherDirectory extends GopherResponse {
    HashSet<DirectoryEntry> filePaths = new HashSet<>();

    public GopherDirectory(String host, String ip, String selector, Integer port) {
        super(host, ip, selector, port);
    }

    @Override
    public void read(Socket sock) throws IOException, MalformedDirectory {
        int     ch;

        // indicates that the current directory entry is an information listing
        // Hence, the information for this current directory entry is not stored
        int skipPrint = 0;

        // accumulator for storing the port string
        String portAcc = "";

        DirectoryEntry de = new DirectoryEntry();

        // set of DirectoryEntries that will be stored in the GopherDirectory response
        HashSet<DirectoryEntry> paths = new HashSet<>();

        // for keeping track of the current header
        Header header = new Header();

        while (true) {
            ch = sock.getInputStream().read();
            // ========Checks for the current character========
            // checks for special character
            if (ch < 0) {break;}
            // indicates last character of a directory entry
            else if (ch == '\n') {
                if (skipPrint == 0 ) {
                    if (header.currentHeader != Header.HeaderType.PORT) {throw new MalformedDirectory();}
                    de.port = Integer.valueOf(portAcc);
                    paths.add(de);
                }
                de = new DirectoryEntry();
                portAcc = "";
                header.setHeader(Header.HeaderType.TYPE);
                skipPrint = 0;
                continue;
            }
            // indicates a break between the DirectoryEntry fields
            else if (ch == '\t') {
                header.nextHeader();
                continue;
            }

            // if information type directory entry, doesn't bother
            // accumulating data
            if (skipPrint == 1) continue;

            // ========Checks for field========
            if (header.currentHeader == Header.HeaderType.TYPE) {
                // of type "information" so skip
                if (ch == 105) {skipPrint = 1;}

                // last entry of directory listing
                else if (ch == 46) {break;}

                de.type = ch;
                header.nextHeader();
            }
            else if (header.currentHeader == Header.HeaderType.SELECTOR) {de.selector += (char) ch;}
            else if (header.currentHeader == Header.HeaderType.HOST) {de.host += (char) ch;}
            else if (header.currentHeader == Header.HeaderType.PORT) {
                if (ch == 13) {continue;}
                portAcc += (char) ch;
            }
        }
        this.filePaths = paths;
    }

    @Override
    public void addToStats() {
        super.addToStats();
        GopherStats.dirMap.add(this);

    }
}
