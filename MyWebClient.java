import java.io.IOException;

public interface MyWebClient {
    
    public void getResource(String objectName) throws IOException;
    public void postData(String[] data) throws IOException;
    public void sendUnimplementedMethod(String wrongMethodName) throws IOException;
    public void malformedRequest(int type) throws IOException;
    public void close();
}
