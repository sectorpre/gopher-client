import java.nio.ByteBuffer;

public class GopherFile extends GopherResponse {
    int size;
    String fileType;
    ByteBuffer fileData;

    public GopherFile(String host ,String filepath, String ft) {
        super(host, filepath);
        fileType = ft;

    }
}

