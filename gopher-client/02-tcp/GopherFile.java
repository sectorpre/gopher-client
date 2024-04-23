import java.io.IOException;
import java.net.Socket;

/**
 * Represents a Gopher response in the form of a file.
 */
public class GopherFile extends GopherResponse {
    int MaximumFileSize = 100000;
    int size = -1;
    Integer fileType;
    String fileData = "";


    public GopherFile(String host, String ip, String filepath, Integer port, Integer ft) {
        super(host, ip, filepath, port);
        fileType = ft;
        dontRecurseFlag = 1;
    }

    public GopherFile() {
        super("", "","", 0);
    }

    /**
     * Reads the file data from the given socket. If the data being read
     * exceeds MaximumFileSize, a DataExceedException is thrown.
     *
     * @param sock The socket to read data from.
     * @throws IOException         If an I/O error occurs.
     * @throws DataExceedException If the data exceeds a certain limit.
     */
    @Override
    public void read(Socket sock) throws IOException, DataExceedException {
        int     ch;
        size = 0;
        do {
            if (size > MaximumFileSize) {
                throw new GopherResponse.DataExceedException();
            }
            ch = sock.getInputStream().read();
            fileData += ch;
            ;
            size += 1;
        } while (ch >= 0);
    }

    /**
     * Adds this file to the statistics based on its type.
     *
     */
    @Override
    public void addToStats() {
        super.addToStats();
        if (this.fileType == 57) {
            GopherStats.binaryMap.add(this);
        }
        else if (this.fileType == 48) {
            GopherStats.textMap.add(this);
        }
    }
}

