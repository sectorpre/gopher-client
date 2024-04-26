# gopher-client

Authored by Tay Shao An u7553225 

A scraper based on the Gopher protocol which recursively searches directories and downloads all text and binary files. 
Features robust error handling with detailed error handling messages and an easily understandable framework.

## How to run?
1) enter gopher-client/02-tcp
2) compile the code by running "javac GopherClient.java"
3) run the program with "java GopherClient [host] [port] [debug:{0, 1}]"

## Framework

- GopherClient.java - entry point to the program, implements all the sockets, and decides the next destination to query. The main input loop
  works like so:
   - Queries server to receive a list of DirectoryEntrys stored within a GopherResponse instance
   - For each DirectoryEntry class queries the "host:port -> selector combination stored within.
   - If the query from step 2 was for a file, downloads it and moves to the next DirectoryEntry.
   - If the query from step 2 was for a Directory, access the list of DirectoryEntries within the GopherResponse
     and go back to step 1.
   - If an error occurred within the query, does some error handling and moves to the next DirectoryEntry
- GopherStats.java - stores all information processed by GopherClient and prints it out
- GopherResponse.java - abstract class for processing and storing the information that is returned by the external server. Uses primarily two functions
  - read() - Given a socket as a parameter, implementations of this class are able to read characters and process them in their own specific way
  - addToStats() - Implementations of this class will use this function to add to various fields within GopherStats
- GopherDirectory.java - implementation of GopherResponse meant to represent a Gopher response in the form of a directory.
- GopherFile.java - implementation of GopherFile meant to represent a Gopher file in the form of a file(binary/txt).
- Header.java - Used by GopherDirectory for keeping track of the current header for processing directory entries.
- DirectoryEntry.java - Used by storing directory entry information when reading reading from an external server.
