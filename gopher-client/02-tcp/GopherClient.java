
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
    {SockLine.writeLine(sock, request);}

    protected static void gopherRecursive(HashSet<DirectoryEntry> des) throws IOException {
        for (DirectoryEntry k : des) {
            GopherResponse gr;
            if (Objects.equals(k.host, "")) {continue;}
            //sends a gopher request to the link

            try {gr = gopherSendAndRecv(k);}
            catch(java.net.ConnectException | java.net.UnknownHostException d) {
                System.out.printf("%s %s connection error --", k.host, k.selector);
                GopherStats.printStats();
                continue;}

            // calls gopherRecursive if there are more directories within it
            if (gr == null) {
                continue;}
            System.out.printf("%s: %s --\t", k.host, k.selector);
            GopherStats.printStats();

            // adds gopherResponse to gopherstats and if it is a directory, calls gopherRecursive
            if ( k.type== 49) {
                gopherRecursive(((GopherDirectory) gr).filePaths);
            }
            else {
                //TODO fix filesort
                GopherStats.fileSort((GopherFile) gr);
            }
        };
    }

    protected static GopherResponse gopherSendAndRecv(DirectoryEntry de)
            throws IOException {
        Socket              sock;
        GopherResponse gr;

        InetAddress address = InetAddress.getByName(de.host);
        sock = new Socket(address.getHostAddress(), 70);

        // if host is an external server or page is visited before, returns null
        if (GopherStats.pageCheck(address.getHostAddress(), de.selector) == 0 || !address.getHostAddress().equals(serviceHost)) {
            return null;}

        // checks to see if file or directory
        if (de.type == 49) {gr = new GopherDirectory(de.host, de.selector);}
        else {gr = new GopherFile(de.host, de.selector, de.type);}

        // sending and reading
        if (!de.selector.isEmpty()) {sendRequest(sock, de.selector);}
        sendRequest(sock, "\r\n");
        gr.read(sock);

        // closing actions
        sock.close();
        GopherStats.pageAdd(address.getHostAddress(), de.selector);
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
