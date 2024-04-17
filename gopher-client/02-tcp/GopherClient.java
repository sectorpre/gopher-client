
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
        GopherDirectory gr = (GopherDirectory) gopherSendAndRecv(host, "");
        assert gr != null;
        gopherRecursive(gr.filePaths);
        GopherStats.printServers();
        GopherStats.printText();
        GopherStats.printBinary();
    }

    /** Send our request to server */

    protected static void sendRequest(Socket sock, String request)
        throws IOException
    {SockLine.writeLine(sock, request);}

    protected static void gopherRecursive(HashMap<String, HashSet<String>> pathMap) throws IOException {
        GopherResponse gr;
        for (var k: pathMap.entrySet()) {
            for (var p: k.getValue()) {
                if (Objects.equals(k.getKey(), "")) {continue;}
                //sends a gopher request to the link

                try {gr = gopherSendAndRecv(k.getKey(), p);}
                catch(java.net.ConnectException | java.net.UnknownHostException d) {
                    System.out.printf("%s connection error\n", p);
                    continue;}

                System.out.printf("%s: %s --\t", k.getKey(), p);
                GopherStats.printStats();

                // calls gopherRecursive if there are more directories within it
                if (gr == null) {continue;}

                // adds gopherResponse to gopherstats and if it is a directory, calls gopherRecursive
                if (GopherStats.fileSort(gr) == 1) {
                    gopherRecursive(((GopherDirectory) gr).filePaths);
                }
            }
        }
    }

    protected static GopherResponse gopherSendAndRecv(String ipAddress, String request)
            throws IOException {
        Socket              sock;
        GopherResponse gr;

        InetAddress address = InetAddress.getByName(ipAddress);
        sock = new Socket(address.getHostAddress(), 70);

        // if host is an external server or page is visited before, returns null
        if (GopherStats.pageAdd(address.getHostAddress(), request) == 0 || !address.getHostAddress().equals(serviceHost)) {
            return null;}

        // checks to see if file or directory
        String[] tempsplit = request.split("/");
        if (tempsplit.length == 0) {
            gr = new GopherDirectory(ipAddress, request);
        }
        else {
            String[] selectorSplit = tempsplit[tempsplit.length - 1].split("\\.");
            if (selectorSplit.length > 1) {
                gr = new GopherFile(ipAddress, request, selectorSplit[selectorSplit.length - 1].trim());
            }
            else {
                gr = new GopherDirectory(ipAddress, request);
            }

        }

        // send request
        if (!request.isEmpty()) {
            sendRequest(sock, request);
            sendRequest(sock, "\r\n");
        }
        else {
            sendRequest(sock, "\r\n");
        }
        SockLine.gopherRead(sock, gr);
        sock.close();
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
