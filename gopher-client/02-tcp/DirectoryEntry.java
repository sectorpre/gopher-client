/**
 * Container representing the information received by a Gopher Client
 * within each directory entry
 */
public class DirectoryEntry {
    public Integer type = 49;
    public String name = "";
    public String selector = "";
    public String host = "";
    public Integer port = 70;
}
