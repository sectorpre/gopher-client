import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.util.HashSet;

public abstract class GopherResponse {
    public DirectoryEntry de;
    public String ip;
    public Integer dontRecurseFlag = 0;

    public GopherResponse(DirectoryEntry de, String ip) {
        this.de = de;
        this.ip = ip;
    }


    public void read(Socket sock) throws IOException, DataExceedException, MalformedDirectory {}

    public void addToStats() {
        GopherStats.visitedPages.add(de.selector);
        GopherStats.pagesVisited += 1;
        if (!GopherStats.externalServers.containsKey(ip)) {
            GopherStats.externalServers.put(ip, new HashSet<>());}
        GopherStats.externalServers.get(ip).add(de.port);

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


