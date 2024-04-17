import java.util.HashMap;
import java.util.HashSet;

public class GopherDirectory extends GopherResponse {
    HashMap<String, HashSet<String>> filePaths;

    public GopherDirectory(String host, String filepath) {
        super(host, filepath);
    }

    public GopherDirectory(String host, String filepath, HashMap<String, HashSet<String>> filePaths) {
        super(host, filepath);
        this.filePaths = filePaths;
        this.selector = filepath;
    }
}
