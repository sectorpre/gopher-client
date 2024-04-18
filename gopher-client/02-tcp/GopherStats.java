import java.net.InetAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class GopherStats {
    public static HashMap<String, HashSet<String>> visitedPages = new HashMap<>();
    public static HashSet<GopherFile> binaryMap = new HashSet<>();
    public static HashSet<GopherFile> textMap = new HashSet<>();
    public static HashSet<GopherDirectory> dirMap = new HashSet<>();
    public static Integer pagesVisited = 0;

    // if hostIpAddress is set, only pages of the hostIPAddress are added
    public static String hostname = "";

    public static void printServers() {
        System.out.println("====================");
        for (var k : visitedPages.entrySet()) {
            System.out.printf("Server: %s \n" , k.getKey());
        }
        System.out.println("====================");

    }

    public static void printText() {
        System.out.println("====================");
        for (var k: textMap) {
            System.out.printf("%s:%s \n", k.host, k.selector);
        }
        System.out.println("====================");
    }

    public static void printBinary() {
        System.out.println("====================");
        for (var k: binaryMap) {
            System.out.printf("%s:%s \n", k.host, k.selector);
        }
        System.out.println("====================");
    }

    public static int pageCheck(String host, String selector) {
        //ensures pages are not visited in a loop
        if (visitedPages.containsKey(host)) {
            if (visitedPages.get(host).contains(selector)) {
                return 0;}
        }
        return 1;
    }

    public static int pageAdd(String host, String selector) {
        //ensures pages are not visited in a loop
        if (visitedPages.containsKey(host)) {
            GopherStats.visitedPages.get(host).add(selector);
        }
        else {
            HashSet<String> newEntry = new HashSet<>();
            newEntry.add(selector);
            GopherStats.visitedPages.put(host, newEntry);
        }
        GopherStats.pagesVisited += 1;
        return 1;
    }

    public static void printStats() {
        System.out.printf("pages visited: %d, text files: %d, binary files %d\n",
                pagesVisited,
                textMap.size(),
                binaryMap.size());
    }

    public static int fileSort(GopherFile gr) {
        if (gr.fileType == 57) {
            GopherStats.binaryMap.add(gr);
        }
        else if (gr.fileType == 48) {
            GopherStats.textMap.add(gr);
        }
        return 0;
    }



}
