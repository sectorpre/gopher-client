
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

    public static <K, V> void selectiveAdd(HashMap<K, HashSet<V>> map, K variable1, V variable2) {
        if (map.containsKey(variable1)) {map.get(variable1).add(variable2);}
        else {
            HashSet<V> newSet = new HashSet<>();
            newSet.add(variable2);
            map.put(variable1, newSet);
        }
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
        String portAcc = "";
        DirectoryEntry de = new DirectoryEntry();
        HashSet<DirectoryEntry> paths = new HashSet<>();

        if (gr.getClass().equals(GopherFile.class)) {
            while (true) {
                ch = sock.getInputStream().read();;
                responseSize += 1;

                // checks for special characters
                if (ch < 0) {break;}
            }
            ((GopherFile) gr).size = responseSize;
        }
        else if (gr.getClass().equals(GopherDirectory.class)){
            while (true) {
                ch = sock.getInputStream().read();;
                responseSize += 1;

                // checks for special characters
                if (ch < 0) {break;}
                else if (ch == '\n') {
                    System.out.printf("%s %s %s\n", hostAcc,selectorAcc,portAcc );
                    if (skipPrint == 0 && !hostAcc.isEmpty()) {
                        de.selector = selectorAcc;
                        de.host = hostAcc;
                        de.port = Integer.valueOf(portAcc);
                        paths.add(de);
                    }
                    de = new DirectoryEntry();
                    selectorAcc = "";
                    hostAcc = "";
                    portAcc = "";
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
                    de.type = ch;
                    if (ch == 105) {skipPrint = 1;}
                    Header.nextHeader();
                }
                else if (Header.currentHeader == Header.HeaderType.SELECTOR) {
                    selectorAcc += (char) ch;
                }
                else if (Header.currentHeader == Header.HeaderType.HOST) {
                    hostAcc += (char) ch;
                }
                else if (Header.currentHeader == Header.HeaderType.PORT) {
                    if (ch == 13) {continue;}
                    portAcc += (char) ch;
                }
            }
            ((GopherDirectory) gr).filePaths = paths;
        }

        return gr;
    }
}
