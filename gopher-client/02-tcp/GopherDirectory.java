import java.util.HashMap;
import java.util.HashSet;

public class GopherDirectory extends GopherResponse {
    HashMap<String, HashSet<String>> filePaths;

    public GopherDirectory(String filepath) {
        super(filepath);
    }

    public GopherDirectory(String filepath, HashMap<String, HashSet<String>> filePaths) {
        super(filepath);
        this.filePaths = filePaths;
        this.selector = filepath;
    }
}
