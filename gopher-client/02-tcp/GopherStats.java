import java.util.HashMap;
import java.util.HashSet;

/**
 * Class for storing statistics for all connections/interactions with
 * the Gopher Server.
 */
public class GopherStats {
    public static HashMap<String, HashSet<String>> visitedPages = new HashMap<>();
    public static HashSet<GopherFile> binaryMap = new HashSet<>();
    public static HashSet<GopherFile> textMap = new HashSet<>();
    public static HashSet<GopherDirectory> dirMap = new HashSet<>();
    public static Integer pagesVisited = 0;


    public static void printServers() {
        System.out.println("==========External servers==========");
        for (var k : visitedPages.entrySet()) {
            System.out.printf("Server: %s \n" , k.getKey());
        }

    }

    public static void printText() {
        System.out.println("==========Text files==========");
        GopherFile largest = new GopherFile("","", 0);
        GopherFile smallest = new GopherFile("","", 0);;
        for (var k: textMap) {
            if (largest.size == -1) {largest = k;}
            if (smallest.size == -1) {smallest = k;}

            if (k.size < smallest.size) {smallest = k;}
            if (k.size > largest.size) {largest = k;}

            System.out.printf("%s:%s size - %d\n", k.host, k.selector, k.size);
        }
        System.out.println("==============================");
        System.out.printf("smallest text file: %s %s %d\n", smallest.host, smallest.selector, smallest.size);
        System.out.printf("largest text file: %s %s %d\n", largest.host, largest.selector, largest.size);
        System.out.println("==============================");
        System.out.printf("%s\n", smallest.fileData);
        System.out.println("==============================");


    }

    public static void printBinary() {
        GopherFile largest = new GopherFile("","", 0);
        GopherFile smallest = new GopherFile("","", 0);;

        System.out.println("==========Binary files==========");
        for (var k: binaryMap) {
            if (largest.size == -1) {largest = k;}
            if (smallest.size == -1) {smallest = k;}

            if (k.size < smallest.size) {smallest = k;}
            if (k.size > largest.size) {largest = k;}

            System.out.printf("%s:%s size - %d\n", k.host, k.selector, k.size);
        }
        System.out.println("==============================");
        System.out.printf("smallest binary file: %s %s %d\n", smallest.host, smallest.selector, smallest.size);
        System.out.printf("largest binary file: %s %s %d\n", largest.host, largest.selector, largest.size);
    }

    public static int pageCheck(String host, String selector) {
        //ensures pages are not visited in a loop
        if (visitedPages.containsKey(host)) {
            if (visitedPages.get(host).contains(selector)) {
                return 0;}
        }
        return 1;
    }


    public static void printStats() {
        System.out.printf("pages visited: %d, directories: %d ,text files: %d, binary files %d\n",
                pagesVisited,
                dirMap.size(),
                textMap.size(),
                binaryMap.size());
    }
}
