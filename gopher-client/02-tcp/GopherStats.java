import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class GopherStats {
    public static HashMap<String, HashSet<String>> visitedPages = new HashMap<>();
    public static HashMap<String, Integer> binaryMap = new HashMap<>();
    public static HashMap<String, Integer> textMap = new HashMap<>();
    public static Integer pagesVisited = 0;

    // if hostIpAddress is set, only pages of the hostIPAddress are added
    public static String hostname = "";


    public static int pageAdd(String host, String selector) {
        //ensures pages are not visited in a loop
        if (visitedPages.containsKey(host)) {
            if (visitedPages.get(host).contains(selector)) {return 0;}
            else {GopherStats.visitedPages.get(host).add(selector);}
        }
        else {
            HashSet<String> newEntry = new HashSet<>();
            newEntry.add(selector);
            GopherStats.visitedPages.put(host, newEntry);
        }
        //SockLine.selectiveAdd(visitedPages, host, selector);
        GopherStats.pagesVisited += 1;
        return 1;
    }

    public static void printStats() {
        System.out.printf("pages visited: %d, text files: %d, binary files %d\n",
                pagesVisited,
                textMap.size(),
                binaryMap.size());
    }

    public static void fileSort(GopherFile gr) {
        if (Objects.equals(gr.fileType.trim(), "txt")) {
            GopherStats.textMap.put(gr.selector, gr.size);
        }
        else {
            GopherStats.binaryMap.put(gr.selector, gr.size);
        }

    }



}
