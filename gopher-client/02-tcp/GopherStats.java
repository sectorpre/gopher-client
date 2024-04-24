import java.util.ArrayList;
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

    // note that the ip of the external servers are used as the keys of
    // the external server to avoid duplicate entries of the same server
    // with different host name
    public static HashMap<String, HashSet<Integer>> externalServers = new HashMap<>();
    public static HashSet<DirectoryEntry> unresponsive = new HashSet<>();
    public static HashSet<GopherFile> binaryMap = new HashSet<>();
    public static HashSet<GopherFile> textMap = new HashSet<>();
    public static HashSet<GopherDirectory> dirMap = new HashSet<>();
    public static Integer pagesVisited = 0;
    public static int[] errorMap = {0,0,0,0,0,0,0};

    public static void printAll() {
        printStats();
        printServers();
        printDownServers();
        printText();
        printBinary();
        printErrors();
    }

    public static void printErrors() {
        System.out.println("========== Error count ==========");
        System.out.printf("unknown server(unknown host exception): %d\n", errorMap[0]);
        System.out.printf("server unresponsive(socket timeout) %d\n", errorMap[1]);
        System.out.printf("connect exception(unable to connect): %d\n", errorMap[2]);
        System.out.printf("data exceeded limit: %d\n", errorMap[3]);
        System.out.printf("malformed directory: %d\n", errorMap[4]);
        System.out.printf("IOexception: %d\n", errorMap[5]);
        System.out.printf("errortype in directory listing: %d\n", errorMap[6]);

    }


    public static void printServers() {
        System.out.println("==========External servers==========");
        for (var k : externalServers.entrySet()) {
            for (var p: k.getValue())
                System.out.printf("Server: %s:%d \n" , k.getKey(), p);
        }
    }

    public static void printDownServers() {
        System.out.println("==========Servers that are down==========");
        for (var k: unresponsive) {
            System.out.printf("Server: %s:%d\n", k.host, k.port);
        }
    }

    public static void printText() {
        System.out.println("==========Text files==========");
        GopherFile largest = new GopherFile();
        GopherFile smallest = new GopherFile();
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
        System.out.println("==============================");
        System.out.printf("%s\n", smallest.fileData);
        System.out.println("==============================");


    }

    public static void printBinary() {
        GopherFile largest = new GopherFile();
        GopherFile smallest = new GopherFile();;

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

    public static void printStats() {
        System.out.printf("pages visited: %d, directories: %d ,text files: %d, binary files %d\n",
                pagesVisited,
                dirMap.size(),
                textMap.size(),
                binaryMap.size());
    }
}
