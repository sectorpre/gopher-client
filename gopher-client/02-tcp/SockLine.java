
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
}
