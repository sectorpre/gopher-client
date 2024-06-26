/**
 * Gopher Client which recursively discovers files/directories
 * and prints results at the end of exectuion
 *
 *  Run with
 *      java GopherClient [ IP addr/hostname] [ port ] [debug]
 *
 *
 *  Written by Tay Shao An u7553225, ANU, 2024
 *
 *  using code written by Hugh Fisher u9011925 as a template
 */

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;

public class GopherClient {

    //  IP address and port that client will contact
    static String   serviceHost = "127.0.0.1";
    static int      servicePort = 70;

    // debug = 1 shows the current page the program has ran to as well
    // as the number of files it has processed
    static int      debug = 0;


    // the number of ms before a socket timeout error is thrown
    static int socketTimeout = 3000;

    /** Function to checkif a given ip: port combination is an external server*/
    protected static int externalCheck(String ip, Integer port) {
        if (!ip.equals(serviceHost) || !(port == servicePort)) {return 1;}
        return 0;
    }

    /** Read input until EOF. Send as request to host, print response */
    protected static void inputLoop() {
        //starting directoryEntry which we will use to query the server
        DirectoryEntry de = new DirectoryEntry();
        de.host = serviceHost;
        de.port = servicePort;
        HashSet<DirectoryEntry> start = new HashSet<>();
        start.add(de);

        System.out.println("running...");
        gopherRecursive(start);
        GopherStats.printAll();
    }

    /** Send our request to server */
    protected static void sendRequest(Socket sock, String request)
        throws IOException
    { sock.getOutputStream().write(request.getBytes("UTF-8"));}

    /** Function which recursively sends Gopher request to host:port/selector listed
     * within a DirectoryEntry and adds statistics to the GopherStats class.
     * */
    protected static void gopherRecursive(HashSet<DirectoryEntry> des) {
        // Get the current date and time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        for (DirectoryEntry k : des) {
            GopherResponse gr;
            // send a request based on information given in the DirectoryEntry
            gr = gopherSafeRequest(k);

            // if no response for whatever reason, continue to next directoryEntry
            if (gr == null) {continue;}

            // gets the current time
            LocalDateTime now = LocalDateTime.now();
            String formattedDateTime = now.format(formatter);

            if (debug == 1) {
                System.out.printf("%s - %s:%d -> %s --\t", formattedDateTime, k.host, k.port, k.selector);
                GopherStats.printStats();
            }

            // calls gopherRecursive if the dontRecurseFlag is not set.
            // it will only be set in two situations
            // - the DirectoryEntry was a file
            // - the DirectoryEntry was for an external server
            if (gr.dontRecurseFlag == 0) {gopherRecursive(((GopherDirectory) gr).filePaths);};

        };
    }

    /**
     * Safe way of connecting to a gopher server by handling all errors
     * and exceptions and adding them to Gopher statistics.
     * */
    protected static GopherResponse gopherSafeRequest(DirectoryEntry k) {
        GopherResponse gr;
        try {gr = gopherConnect(k);}
        catch (Throwable e) {
            if (e.getMessage().equals("Connection refused")) {
                GopherStats.unresponsive.add(k);
            }
            GopherStats.allErrors.add(String.format(("%s:%d -> %s : %s"),k.host,k.port,k.selector,e.getMessage()));
            return null;
        }
        return gr;
    }

    /**
     * Sends a request to a destination as specified from a directory
     * Entry and reads a response.
     * Basically converts a DirectoryEntry into a GopherResponse
     *
     * */
    protected static GopherResponse gopherConnect(DirectoryEntry de)
            throws GopherResponse.GopherResponseError, IOException {
        Socket              sock;
        GopherResponse gr;

        String ip = InetAddress.getByName(de.host).getHostAddress();

        // if page is visited before, returns null
        if (externalCheck(ip, de.port) == 0) {
            if (GopherStats.visitedPages.contains(de.selector)) {
                return null;}
        }

        // new socket creation
        sock = new Socket(ip, de.port);
        sock.setSoTimeout(socketTimeout);

        // checks to see if file or directory
        if (de.type == 49) {gr = new GopherDirectory(de, ip);}
        else {gr = new GopherFile(de, ip);}

        // sending a request to remote server
        if (!de.selector.isEmpty()) {sendRequest(sock, de.selector);}
        sendRequest(sock, "\r\n");

        // reads from socket and stores information within gr
        gr.read(sock);

        // closes socket and adds gopherResponse into the gopherStats
        sock.close();
        gr.addToStats();

        // checks if the ip is an external address if it is, sets the dontRecurseFlag
        if (externalCheck(ip, de.port) == 1) {
            gr.dontRecurseFlag = 1;}

        return gr;
    }

    /** Handle command line arguments. */
    protected static void processArgs(String[] args) throws UnknownHostException {
        if (args.length > 0) {
            serviceHost = InetAddress.getByName(args[0]).getHostAddress();
            GopherStats.serviceHost = serviceHost;
            if (args.length > 1) {
                servicePort = Integer.parseInt(args[1]);
                GopherStats.servicePort = servicePort;
            }
            if (args.length > 2) {
                debug = Integer.parseInt(args[2]);
            }
        }
    }

    public static void main(String[] args)
    {
        try {
            processArgs(args);
            inputLoop();
            System.out.println("Done.");
        } catch (Exception e) {
            System.exit(-1);   
        }
    }

}
