import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;

/**
 * Abstract class representing the response the Gopher server will provide, given a directoryEntry
 * instance. I chose to represent each response as am abstract class, as both directory and file
 * type responses share common fields. Each type of response also require similar methods and lines
 * of code can be shared between the two.
 * */
public abstract class GopherResponse {
    public DirectoryEntry de;
    public String errorMessage;
    public String ip;
    public Integer dontRecurseFlag;

    public GopherResponse(DirectoryEntry de, String ip) {
        this.de = de;
        this.ip = ip;
        dontRecurseFlag = 0;
        errorMessage = "";
    }

    /**
     * Abstract function representing how data should be processed when it is read from a socket.
     * I abstracted this method as it only needs to be declared in subclasses
     * */
    public abstract void read(Socket sock) throws IOException, GopherResponseError;

    /**
     * Method which adds relevant information of the response to the GopherStats class. Each
     * implementation of GopherResponse also adds to this method with more code.
     * */
    public void addToStats() throws IOException {
        GopherStats.visitedPages.add(de.selector);
        GopherStats.pagesVisited += 1;
        if (!GopherStats.externalServers.containsKey(ip)) {
            GopherStats.externalServers.put(ip, new HashSet<>());}
        GopherStats.externalServers.get(ip).add(de.port);
    }

    /**
     * Parent class for all GopherResponse errors that can be thrown
     * */
    public static class GopherResponseError extends Exception {
        public GopherResponseError(String message) {
            super(message);
        }
    }

}


