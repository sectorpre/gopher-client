import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.util.HashSet;

public abstract class GopherResponse {
    public String selector;
    public String host;
    public String ip;
    public Integer port;
    public Integer dontRecurseFlag = 0;

    public GopherResponse(String host, String ip, String selector, Integer port) {
        this.host = host;
        this.selector = selector;
        this.ip = ip;
        this.port = port;
    }


    public void read(Socket sock) throws IOException, DataExceedException, MalformedDirectory {}

    public void addToStats() {
        GopherStats.visitedPages.add(selector);
        GopherStats.pagesVisited += 1;
        if (!GopherStats.externalServers.containsKey(ip)) {
            GopherStats.externalServers.put(ip, new HashSet<>());}
        GopherStats.externalServers.get(ip).add(port);

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


