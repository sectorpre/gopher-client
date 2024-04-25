import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Represents a Gopher response in the form of a file.
 */
public class GopherFile extends GopherResponse {
    int MaximumFileSize = 100000;
    int size = -1;
    String fileData = "";


    public GopherFile(DirectoryEntry de, String ip) {
        super(de, ip);
        dontRecurseFlag = 1;
    }

    /**
     * Reads the file data from the given socket. If the data being read
     * exceeds MaximumFileSize, a DataExceedException is thrown.
     *
     */
    @Override
    public void read(Socket sock) throws IOException, DataExceedException {
        int     ch;
        size = 0;
        while (true) {
            if (size > MaximumFileSize) {
                throw new GopherResponse.DataExceedException();
            }
            ch = sock.getInputStream().read();
            if (ch < 0) {
                break;
            }
            fileData += (char) ch;
            size += 1;
        }
    }

    /**
     * Adds this file to the statistics based on its type.
     */
    @Override
    public void addToStats() {
        super.addToStats();
        if (de.type== 57) {
            GopherStats.binaryMap.add(this);
        }
        else if (de.type == 48) {
            GopherStats.textMap.add(this);
        }
        saveToFile();
    }

    /**
     * Saves the file to the current directory.
     * */
    public void saveToFile() {
        String fileName = Paths.get(de.selector).getFileName().toString(); // Name of the file to be created

        try {
            // Get the current directory
            String currentDirectory = System.getProperty("user.dir");

            // Resolve the file path in the current directory
            Path filePath = Paths.get(currentDirectory, fileName);

            // Write content to the file
            Files.write(filePath, fileData.getBytes());

        } catch (Exception e) {
            System.out.println("File could not be downloaded: " + e.getMessage());
        }

    }
}

