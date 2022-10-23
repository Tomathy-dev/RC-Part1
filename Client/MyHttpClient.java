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

    private String format(String[] input){
        String format = "";

        for(int i = 0; i < input.length; i++){
            input[i] = input[i].replace(": ", "=");
            input[i] = input[i].replace(" ", "+");
            format = (i == input.length - 1) ? format.concat(input[i]) : format.concat(input[i]).concat("&");
        }
        return format;
    }

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

            String req = "";

            req = req.concat("GET /" + objectName + " HTTP/1.1\r\n");
            req = req.concat("Host: " + host + ":" + port + "\r\n");
            req = req.concat("Accept: text/html; charset=UTF-8\r\n");
            req = req.concat("\r\n");

            BufferedReader inReader = new BufferedReader(new InputStreamReader(in));
            out.write(req.getBytes());
            String response = inReader.readLine();

            System.out.println(response);

            socket.close();
            
        } catch (IOException e) {

            System.err.println("Couldn't get I/O for the connection to " + host);
            System.exit(1);
        }
    }

    public void postData(String[] data) throws IOException{

        try{

            String req = "";

            req = req.concat("POST /simpleForm.html HTTP/1.1\r\n");
            req = req.concat("Host: " + host + ":" + port + "\r\n");
            req = req.concat("Accept: text/html; charset=UTF-8\r\n");
            req = req.concat("\r\n");
            req = req.concat(format(data));

            BufferedReader inReader = new BufferedReader(new InputStreamReader(in));
            out.write(req.getBytes());
            String response = inReader.readLine();

            System.out.println(response);

            socket.close();

        } catch (IOException e) {

            System.err.println("Couldn't get I/O for the connection to " + host);
            System.exit(1);
        }

    }
    public void sendUnimplementedMethod(String wrongMethodName) throws IOException{

    }
    public void malformedRequest(int type) throws IOException{

    }
    public void close(){

    }

}
