import java.util.HashMap;
import java.util.HashSet;

public class GopherDirectory extends GopherResponse {
    HashSet<DirectoryEntry> filePaths = new HashSet<>();

    public GopherDirectory(String host, String filepath) {
        super(host, filepath);
    }

    public GopherDirectory(String host, String filepath, HashSet<DirectoryEntry> filePaths) {
        super(host, filepath);
        this.filePaths = filePaths;
        this.selector = filepath;
    }
}
