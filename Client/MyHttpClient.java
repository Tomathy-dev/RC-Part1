package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class MyHttpClient implements MyWebClient {

    private Socket socket;
    private String host;
    private int port;
    private InputStream in;
    private OutputStream out;

    private String formatPost(String[] input){
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
            this.host = hostName;
            this.port = portNumber;
            this.socket = new Socket(hostName, portNumber);
            this.in = socket.getInputStream();
            this.out = socket.getOutputStream();

        }catch (IOException e){
			
			System.err.println("Couldn't get I/O for the connection to " + hostName);
			System.exit(1);
			
		}
    }

    public void getResource(String objectName) throws IOException{

        try{

            int ncount = 0;
            int chint, clength;
            char ch;

            String req = "GET /" + objectName + " HTTP/1.1\r\n" +
                    "Host: " + host + ":" + port + "\r\n" +
                    "\r\n";
            out.write(req.getBytes(StandardCharsets.UTF_8));
            BufferedReader inReader = new BufferedReader(new InputStreamReader(in));
            StringBuilder response = new StringBuilder();
            while((chint = inReader.read()) != -1){
                ch = (char) chint;
                response.append(ch);
                if(ch == '\n')
                    ncount++;
                if(ncount == 4)
                    break;
            }

            String[] resS = response.toString().split(": ");
            clength = Integer.parseInt(resS[2].replace("\r\n", ""));
            for(int i = 0; i < clength; i++){
                chint = inReader.read();
                ch = (char) chint;
                response.append(ch);
            }
            inReader.close();
            System.out.println("\n" + response);
            
        } catch (IOException e) {

            System.err.println("Couldn't get I/O for the connection to " + host);
            System.exit(1);
        }
    }

    public void postData(String[] data) throws IOException{

        try{

            StringBuilder req = new StringBuilder();
            int ncount = 0;
            int chint, clength;
            char ch;

            String eData = formatPost(data);
            req.append("POST /simpleForm.html HTTP/1.1\r\n");
            req.append("Host: ").append(host).append(":").append(port).append("\r\n");
            req.append("Content-Length: ").append(eData.length()).append("\r\n");
            req.append("\r\n");
            req.append(eData);


            out.write(req.toString().getBytes(StandardCharsets.UTF_8));

            BufferedReader inReader = new BufferedReader(new InputStreamReader(in));
            StringBuilder response = new StringBuilder();
            while((chint = inReader.read()) != -1){
                ch = (char) chint;
                response.append(ch);
                if(ch == '\n')
                    ncount++;
                if(ncount == 4)
                    break;
            }

            String[] resS = response.toString().split(": ");
            clength = Integer.parseInt(resS[2].replace("\r\n", ""));
            for(int i = 0; i < clength; i++){
                chint = inReader.read();
                ch = (char) chint;
                response.append(ch);
            }
            inReader.close();
            System.out.println("\n" + response);

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
