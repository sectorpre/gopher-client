import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.util.HashSet;

public abstract class GopherResponse {
    public String selector;
    public String host;

    public GopherResponse(String host, String selector) {
        this.host = host;
        this.selector = selector;
    }


    public void read(Socket sock) throws IOException, DataExceedException, MalformedDirectory {}

    public void addToStats(String ip) {
        if (GopherStats.visitedPages.containsKey(ip)) {
            GopherStats.visitedPages.get(ip).add(selector);
        }
        else {
            HashSet<String> newEntry = new HashSet<>();
            newEntry.add(selector);
            GopherStats.visitedPages.put(ip, newEntry);
        }
        GopherStats.pagesVisited += 1;

    }

    public static class DataExceedException extends Exception {
        public DataExceedException() {
        }
    }

    public static class MalformedDirectory extends Exception {
        public MalformedDirectory() {
        }
    }
}


