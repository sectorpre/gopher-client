import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Represents a Gopher response in the form of a file.
 */
public class GopherFile extends GopherResponse {
    static int MaximumFileSize = 100000;
    int size = -1;
    String fileData = "";


    public GopherFile(DirectoryEntry de, String ip) {
        super(de, ip);
        dontRecurseFlag = 1;
    }

    /**
     * Exception for when the data being read exceeds the MaximumFileSize
     * */
    public static class DataExceedException extends GopherResponseError {
        public DataExceedException(String message) {
            super(message);
        }
    }

    /**
     * Exception for when a text file is not terminated with the lastline string.
     * TextFile  ::= {TextBlock} Lastline
     * */
    public static class FileFormatError extends GopherResponseError {
        public FileFormatError(String message) {
            super(message);
        }

    }

    /**
     * Reads the file data from the given socket. If the data being read
     * exceeds MaximumFileSize, a DataExceedException is thrown.
     */
    @Override
    public void read(Socket sock) throws IOException, DataExceedException, FileFormatError {
        int     ch;
        size = 0;

        while (true) {
            // if the amount of data being read exceeds the maximum file size
            // throws an error
            if (size > MaximumFileSize) {
                throw new DataExceedException("data exceeded limit");}

            ch = sock.getInputStream().read();
            if (ch < 0) {break;}
            fileData += (char) ch;
            size += 1;
        }
        // ensures that a text block is terminated with a lastline string
        // of characters as specified in rfc1436:
        if (de.type == 48) {
            String lastThreeCharacters = fileData.substring(Math.max(fileData.length() - 3, 0));
            if (!lastThreeCharacters.equals(".\r\n")) {
                throw new FileFormatError("text file format error");
            }
            fileData = fileData.substring(0, fileData.length() - 3);


        }

    }

    /**
     * Adds this file to the statistics based on its type.
     */
    @Override
    public void addToStats() throws IOException {
        saveToFile();
        super.addToStats();
        if (de.type== 57) {
            GopherStats.binaryMap.add(this);
        }
        else if (de.type == 48) {
            GopherStats.textMap.add(this);
        }
    }

    /**
     * Saves the file to the current directory.
     * */
    public void saveToFile() throws IOException {
        String fileName = Paths.get(de.selector).getFileName().toString(); // Name of the file to be created

        // Get the current directory
        String currentDirectory = System.getProperty("user.dir");
        Path filePath = Paths.get(currentDirectory, fileName);

        // Write content to the file
        Files.write(filePath, fileData.getBytes());
            

    }
}

