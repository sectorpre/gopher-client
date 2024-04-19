
/** TCP echo client program for ANU COMP3310.
 *
 *  Run with
 *      java TcpClient [ IP addr ] [ port ]
 *
 *  Written by Hugh Fisher u9011925, ANU, 2024
 *  Released under Creative Commons CC0 Public Domain Dedication
 *  This code may be freely copied and modified for any purpose
 */


import java.io.*;
import java.net.*;
import java.lang.Thread;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.regex.Pattern;


public class GopherClient {

    //  IP address and port that client will contact
    static String   serviceHost = "127.0.0.1";
    static int      servicePort = 3310;

    /** Read input until EOF. Send as request to host, print response */

    protected static void inputLoop(String host)
            throws IOException, InterruptedException {

        InetAddress address = InetAddress.getByName(host);
        serviceHost = address.getHostAddress();

        DirectoryEntry de = new DirectoryEntry();
        de.host = serviceHost;
        HashSet<DirectoryEntry> start = new HashSet<>();
        start.add(de);

        gopherRecursive(start);

        GopherStats.printServers();
        GopherStats.printText();
        GopherStats.printBinary();
    }

    /** Send our request to server */

    protected static void sendRequest(Socket sock, String request)
        throws IOException
    { sock.getOutputStream().write(request.getBytes("UTF-8"));}

    /** Function which recursively sends Gopher request to host:port/selector listed
     * within a DirectoryEntry and adds statistics to the GopherStats class.
     * */
    protected static void gopherRecursive(HashSet<DirectoryEntry> des) throws IOException {
        for (DirectoryEntry k : des) {
            GopherResponse gr;
            if (Objects.equals(k.host, "")) {continue;}

            // send a request based on information given in the DirectoryEntry k
            try {
                gr = gopherSendAndRecv(k);
                if (gr == null) {continue;}
            }
            catch (java.net.UnknownHostException d) {
                System.out.printf("%s:%d -> %s -- unknown server\n", k.host, k.port, k.selector);
                continue;
            }
            catch (java.net.SocketTimeoutException d) {
                System.out.printf("%s:%d -> %s -- server unresponsive\n", k.host, k.port, k.selector);
                continue;
            }
            catch(java.net.ConnectException d ) {
                System.out.printf("%s:%d -> %s -- connection error \n", k.host, k.port, k.selector);
                continue;
            }
            catch (GopherResponse.DataExceedException d) {
                System.out.printf("%s:%d -> %s -- data exceeded limit\n", k.host, k.port, k.selector);
                continue;
            }
            catch (GopherResponse.MalformedDirectory d) {
                System.out.printf("%s:%d -> %s -- malformed directory exception\n", k.host, k.port, k.selector);
                continue;
            }

            System.out.printf("%s:%d -> %s --\t", k.host, k.port, k.selector);
            GopherStats.printStats();

            // calls gopherRecursive if directoryEntry was a directory request
            // if not adds it into file sort
            if ( k.type== 49) {gopherRecursive(((GopherDirectory) gr).filePaths);}
        };
    }

    /**
     * Sends a request to a destination as specified from a directory
     * Entry and reads a response.
     * Basically converts a DirectoryEntry into a GopherResponse
     *
     * */
    protected static GopherResponse gopherSendAndRecv(DirectoryEntry de)
            throws IOException, GopherResponse.DataExceedException, GopherResponse.MalformedDirectory {
        Socket              sock;
        GopherResponse gr;

        String address = InetAddress.getByName(de.host).getHostAddress();
        sock = new Socket(address, 70);
        sock.setSoTimeout(1000);

        // if host is an external server or page is visited before, returns null
        if (GopherStats.pageCheck(address, de.selector) == 0 || !address.equals(serviceHost)) {
            return null;
        }

        // checks to see if file or directory
        if (de.type == 49) {gr = new GopherDirectory(de.host, de.selector);}
        else {gr = new GopherFile(de.host, de.selector, de.type);}

        // sending and reading
        if (!de.selector.isEmpty()) {sendRequest(sock, de.selector);}
        sendRequest(sock, "\r\n");
        gr.read(sock);

        // closing actions
        sock.close();
        gr.addToStats(address);
        return gr;
    }

    /** Handle command line arguments. */

    protected static void processArgs(String[] args)
    {
        //  This program has only two CLI arguments, and we know the order.
        //  For any program with more than two args, use a loop or package.
        if (args.length > 0) {
            serviceHost = args[0];
            if (args.length > 1) {
                servicePort = Integer.parseInt(args[1]);
            }
        }
    }

    public static void main(String[] args)
    {
        try {
            processArgs(args);
            inputLoop(serviceHost);
            System.out.println("Done.");
        } catch (Exception e) {
            System.out.println(e.toString());
            System.exit(-1);   
        }
    }

}
