import java.nio.ByteBuffer;

public class GopherFile extends GopherResponse {
    int size;
    String fileType;
    ByteBuffer fileData;

    public GopherFile(String filepath, String ft) {
        super(filepath);
        fileType = ft;

    }
}

