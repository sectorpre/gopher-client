import java.util.HashMap;
import java.util.HashSet;

/**
 * Class for storing statistics for all connections/interactions with
 * the Gopher Server. The GopherStats and GopherClient relationship uses
 * as a pseudo observer pattern, where the GopherClient acts as a publisher
 * which calls GopherResponse.addToStats for each GopherResponse it receives,
 * wherein GopherStats is responsible for storing the information stored
 * within each GopherResponse in its fields. The GopherStats itself does not
 * modify/interact with its fields and merely prints all its information as
 * specified within its methods.
 *
 */
public class GopherStats {
    public static HashSet<String> visitedPages = new HashSet<>();

    // Hashmap of external servers which the client has visited which maps
    // a string representing the ip address to a HashSet of the ports which
    // were visited.
    //
    // note that the ip of the external servers are used as the keys of
    // the external server to avoid duplicate entries of the same server
    // with different host name
    public static HashMap<String, HashSet<Integer>> externalServers = new HashMap<>();

    // externalServers that fail to connect
    public static HashSet<DirectoryEntry> unresponsive = new HashSet<>();

    // all binary files
    public static HashSet<GopherFile> binaryMap = new HashSet<>();

    // all text files
    public static HashSet<GopherFile> textMap = new HashSet<>();

    // all gopher directories
    public static HashSet<GopherDirectory> dirMap = new HashSet<>();

    // number of pages that the client visited
    public static Integer pagesVisited = 0;
    public static int[] errorMap = {0,0,0,0,0,0,0,0};

    static String   serviceHost = "127.0.0.1";
    static int      servicePort = 70;

    /**
     * Prints all available statistics
     * */
    public static void printAll() {
        printStats();
        printServers();
        printDownServers();
        printText();
        printBinary();
        printErrors();
    }

    /**
     * Prints all errors that occurred
     * */
    public static void printErrors() {
        System.out.println("========== Error count ==========");
        System.out.printf("unknown server (java.net.UnknownHostException d) : %d\n", errorMap[0]);
        System.out.printf("server unresponsive (java.net.SocketTimeoutException d): %d\n", errorMap[1]);
        System.out.printf("connect exception (java.net.ConnectException d ): %d\n", errorMap[2]);
        System.out.printf("data exceeded limit (GopherFile.DataExceedException d): %d\n", errorMap[3]);
        System.out.printf("malformed directory (GopherDirectory.MalformedDirectory d): %d\n", errorMap[4]);
        System.out.printf("text file formatted wrongly (GopherFile.FileFormatError): %d\n", errorMap[7]);
        System.out.printf("IOexception: %d\n", errorMap[5]);
        System.out.printf("errortype in directory listing: %d\n", errorMap[6]);

    }

    /**
     * Prints all servers that the client connected to
     * */
    public static void printServers() {
        System.out.println("==========External servers==========");
        for (var k : externalServers.entrySet()) {
            for (var p: k.getValue()) {
                if (k.getKey().equals(serviceHost) && p == servicePort) {
                    System.out.printf("Server: %s:%d (original server) \n" , k.getKey(), p);
                    continue;
                }
                System.out.printf("Server: %s:%d \n" , k.getKey(), p);
            }
        }
    }

    /**
     * Prints all servers that the client failed to connect to
     * */
    public static void printDownServers() {
        System.out.println("==========Servers that are down==========");
        for (var k: unresponsive) {
            System.out.printf("Server: %s:%d\n", k.host, k.port);
        }
    }

    /**
     * Prints information on all text files
     * */
    public static void printText() {
        System.out.println("==========Text files==========");
        GopherFile largest = new GopherFile(null, "");
        GopherFile smallest = new GopherFile(null, "");
        for (var k: textMap) {
            if (largest.size == -1) {largest = k;}
            if (smallest.size == -1) {smallest = k;}

            if (k.size < smallest.size) {smallest = k;}
            if (k.size > largest.size) {largest = k;}

            System.out.printf("%s:%s size - %d\n", k.de.host, k.de.selector, k.size);
        }
        System.out.println("==============================");
        System.out.printf("smallest text file: %s %s %d\n", smallest.de.host, smallest.de.selector, smallest.size);
        System.out.printf("largest text file: %s %s %d\n", largest.de.host, largest.de.selector, largest.size);
        System.out.println("======= smallest file data =========");
        System.out.printf("%s\n", smallest.fileData);
        System.out.println("===================================");


    }

    /**
     * Prints information on all binary files
     * */
    public static void printBinary() {
        GopherFile largest = new GopherFile(null, "");
        GopherFile smallest = new GopherFile(null, "");

        System.out.println("==========Binary files==========");
        for (var k: binaryMap) {
            if (largest.size == -1) {largest = k;}
            if (smallest.size == -1) {smallest = k;}

            if (k.size < smallest.size) {smallest = k;}
            if (k.size > largest.size) {largest = k;}

            System.out.printf("%s:%s size - %d\n", k.de.host, k.de.selector, k.size);
        }
        System.out.println("==============================");
        System.out.printf("smallest binary file: %s %s %d\n", smallest.de.host, smallest.de.selector, smallest.size);
        System.out.printf("largest binary file: %s %s %d\n", largest.de.host, largest.de.selector, largest.size);
    }

    /**
     * Prints the overall statistics
     * */
    public static void printStats() {
        System.out.printf("pages visited: %d, directories: %d ,text files: %d, binary files %d\n",
                pagesVisited,
                dirMap.size(),
                textMap.size(),
                binaryMap.size());
    }
}
