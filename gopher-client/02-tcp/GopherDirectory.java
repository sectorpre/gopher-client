import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;

/**
 * Implementation of GopherResponse representing a Gopher response of a directory
 * entry list.
 * */
public class GopherDirectory extends GopherResponse {
    HashSet<DirectoryEntry> filePaths = new HashSet<>();

    public GopherDirectory(DirectoryEntry de, String ip) {
        super(de, ip);
    }

    /**
     * Exception for when a directory entry is not formatted correctly
     * */
    public static class MalformedDirectory extends GopherResponseError {
        public MalformedDirectory(String message) {
            super(message);
        }
    }


    /**
     * Reads a directory entry list from a given socket and adds it to
     * the filePaths field within GopherDirectory. Throws MalformedDirectory if
     * the directory is formatted wrongly.
     * */
    @Override
    public void read(Socket sock) throws IOException, MalformedDirectory{
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
        Header header = Header.getInstance();
        header.setHeader(Header.HeaderType.TYPE);

        while (true) {
            ch = sock.getInputStream().read();
            // CHARACTER CHECKS
            if (ch < 0) {break;}
            // indicates last character of a directory entry
            else if (ch == '\n') {
                if (skipPrint == 0 ) {

                    // if final header being processed is not PORT, this means
                    // that the Directory list does not follow the appropriate
                    // format.
                    if (header.getHeader() != Header.HeaderType.PORT) {
                        throw new MalformedDirectory("Malformed Directory Error");}

                    de.port = Integer.valueOf(portAcc);
                    paths.add(de);
                }
                de = new DirectoryEntry();
                portAcc = "";
                header = Header.getInstance();
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

            // HEADER CHECKS
            if (header.getHeader() == Header.HeaderType.TYPE) {
                // of type "information" so skip
                if (ch == 105) {skipPrint = 1;}

                // last entry of directory listing
                else if (ch == 46) {break;}

                // if reads type error, adds an entry to allErrors but
                // does not throw an error and keeps reading
                else if (ch == 51) {
                    GopherStats.allErrors.add(
                            String.format(("%s:%d -> %s : %s"),this.de.host,this.de.port,this.de.selector,
                                    "error type in directory entry"));
                }

                de.type = ch;
                header.nextHeader();
            }
            else if (header.getHeader() == Header.HeaderType.SELECTOR) {de.selector += (char) ch;}
            else if (header.getHeader() == Header.HeaderType.HOST) {de.host += (char) ch;}
            else if (header.getHeader() == Header.HeaderType.PORT) {
                if (ch == 13) {continue;}
                portAcc += (char) ch;
            }
        }
        this.filePaths = paths;
    }

    @Override
    public void addToStats() throws IOException {
        super.addToStats();
        GopherStats.dirMap.add(this);
    }


}
