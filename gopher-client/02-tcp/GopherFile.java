import java.nio.ByteBuffer;

public class GopherFile extends GopherResponse {
    int size;
    Integer fileType;
    ByteBuffer fileData;

    public GopherFile(String host ,String filepath, Integer ft) {
        super(host, filepath);
        fileType = ft;

    }
}

