
/** TCP utility code for ANU COMP3310.
 *  Read and write lines of text over TCP socket, handling
 *  EOL and decoding/encoding UTF-8. Nothing very complex
 *  but avoids copying and pasting over and over again.
 * 
 *  There is no limit on the size of a line.
 *
 *  Written by Hugh Fisher u9011925, ANU, 2024
 *  Released under Creative Commons CC0 Public Domain Dedication
 *  This code may be freely copied and modified for any purpose
 */

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.HashSet;

class SockLine {

    /** Write single line with LF */

    public static void writeLine(Socket sock, String txt)
        throws IOException
    {
        sock.getOutputStream().write(txt.getBytes("UTF-8"));
    }

    public static <K, V> HashMap<K, HashSet<V>> selectiveAdd(HashMap<K, HashSet<V>> map, K variable1, V variable2) {
        if (map.containsKey(variable1)) {map.get(variable1).add(variable2);}
        else {
            HashSet<V> newSet = new HashSet<>();
            newSet.add(variable2);
            map.put(variable1, newSet);
        }
        return map;
    }


    /** Read single line terminated by \n, or null if closed. */

    public static GopherResponse gopherRead(Socket sock, GopherResponse gr)
        throws IOException
    {
        int     ch;
        int skipPrint = 0;
        int responseSize = 0;
        String hostAcc = "";
        String selectorAcc = "";
        HashMap<String, HashSet<String>> paths = new HashMap<>();

        while (true) {
            ch = sock.getInputStream().read();
            responseSize += 1;

            // checks for special characters
            if (ch < 0) {break;}
            else if (ch == '\n') {
                if (skipPrint == 0) {
                    selectiveAdd(paths, hostAcc,selectorAcc);
                    selectorAcc = "";
                    hostAcc = "";
                }
                Header.setHeader(Header.HeaderType.TYPE);
                skipPrint = 0;
                continue;
            }
            else if (ch == '\t') {
                Header.nextHeader();
                continue;
            }

            if (skipPrint == 1) continue;

            // checks TYPE byte
            if (Header.currentHeader == Header.HeaderType.TYPE) {
                // of type "information" so skip
                if (ch == 105) {skipPrint = 1;}
                Header.nextHeader();
            }
            else if (Header.currentHeader == Header.HeaderType.SELECTOR) {
                selectorAcc += (char) ch;
            }
            else if (Header.currentHeader == Header.HeaderType.HOST) {
                hostAcc += (char) ch;
            }
        }

        // end

        if (gr.getClass().equals(GopherFile.class)) {
            ((GopherFile) gr).size = responseSize;
        }
        else if (gr.getClass().equals(GopherDirectory.class)){
            ((GopherDirectory) gr).filePaths = paths;
        }
        return gr;
    }

}
