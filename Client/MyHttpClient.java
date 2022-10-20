package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;

public class MyHttpClient implements MyWebClient {

    private Socket socket;
    private String host;
    private int port;
    private InputStream in;
    private OutputStream out;

    public MyHttpClient(String hostName, int portNumber) throws IOException{
        try{
            host = hostName;
            port = portNumber;
            socket = new Socket(hostName, portNumber);
            in = socket.getInputStream();
            out = socket.getOutputStream();

        }catch (IOException e){
			
			System.err.println("Couldn't get I/O for the connection to " + hostName);
			System.exit(1);
			
		}
    }

    public void getResource(String objectName) throws IOException{

        try{

            StringBuilder req = new StringBuilder("");

            req.append("Get /" + objectName + " HTTP/1.1\r\n");
            req.append("Host: " + host + ":" + port + "\r\n");
            req.append("Accept: text/html,application/xhtml+xml\r\n");

            BufferedReader inReader = new BufferedReader(new InputStreamReader(in));
            out.write(req.toString().getBytes());
            String response = inReader.readLine();

            System.out.println(response);

            socket.close();
            
        } catch (IOException e){}

            System.err.println("Couldn't get I/O for the connection to " + host);
			System.exit(1);

    }

    public void postData(String[] data) throws IOException{

    }
    public void sendUnimplementedMethod(String wrongMethodName) throws IOException{

    }
    public void malformedRequest(int type) throws IOException{

    }
    public void close(){

    }

}
