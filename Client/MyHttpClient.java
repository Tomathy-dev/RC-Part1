package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;

/*****************MyHttpClient*****************\
 Classe que implementa MyWebClient e disponibiliza os vários métodos para enviar Requests para o server.
 */
public class MyHttpClient implements MyWebClient {

    private Socket socket;
    private String host;
    private int port;
    private InputStream in;
    private OutputStream out;
    private BufferedReader inReader;

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
            this.inReader = new BufferedReader(new InputStreamReader(in));

        }catch (IOException e){
			
			System.err.println("Couldn't get I/O for the connection to " + hostName);
			System.exit(1);
			
		}
    }

    //Method sends GET request asking for the page <objectName>
    public void getResource(String objectName) throws IOException{

        try{

            int ncount = 0;
            int chint, clength;
            char ch;

            String req = "GET /" + objectName + " HTTP/1.1\r\n" +
                    "Host: " + host + "\r\n" +
                    "\r\n";
            out.write(req.getBytes());

            StringBuilder response = new StringBuilder();
            while((chint = inReader.read()) != -1){
                ch = (char) chint;
                response.append(ch);
                if(ch == '\n')
                    ncount++;
                if(ncount > 0 && (response.indexOf("200") != -1)){
                    if(ncount == 4)
                        break;
                }else if(ncount == 3)
                    break;
            }
            if(response.indexOf("200") != -1) {
                String[] resS = response.toString().split(": ");
                clength = Integer.parseInt(resS[2].replace("\r\n", ""));
                for (int i = 0; i < clength; i++) {
                    chint = inReader.read();
                    ch = (char) chint;
                    response.append(ch);
                }
            }
            System.out.println("\n" + response);
            
        } catch (IOException e) {

            System.err.println("Couldn't get I/O for the connection to " + host);
            System.exit(1);
        }
    }

    //Method sends POST request with <data>
    public void postData(String[] data) throws IOException{

        try{

            StringBuilder req = new StringBuilder();
            int ncount = 0;
            int chint, clength;
            char ch;

            String eData = formatPost(data);
            req.append("POST /simpleForm.html HTTP/1.1\r\n");
            req.append("Host: ").append(host).append("\r\n");
            req.append("Content-Length: ").append(eData.length()).append("\r\n");
            req.append("\r\n");
            req.append(eData);


            out.write(req.toString().getBytes());

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
            System.out.println("\n" + response);

        } catch (IOException e) {

            System.err.println("Couldn't get I/O for the connection to " + host);
            System.exit(1);
        }

    }

    //Method sends Unimplemnted Request to Server
    public void sendUnimplementedMethod(String wrongMethodName) throws IOException{
        try {

            int ncount = 0;
            int chint, clength;
            char ch;

            String req = wrongMethodName + " /index.html HTTP/1.1\r\n" + "Host: " + host + "\r\n" + "\r\n";


            out.write(req.getBytes());


            StringBuilder response = new StringBuilder();
            while ((chint = inReader.read()) != -1) {
                ch = (char) chint;
                response.append(ch);
                if (ch == '\n')
                    ncount++;
                if (ncount == 3)
                    break;
            }

            System.out.println("\n" + response);
        }catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + host);
            System.exit(1);

        }
    }

    /**
     * Sends a malformed request of <type>
     * type 1 - missing \r\n
     * tupe 2 - extra spaces
     * type 3 - missing HTTP version
     */
    public void malformedRequest(int type) throws IOException{
        try {
            String req;
            int ncount = 0;
            int chint, clength;
            char ch;

            String req1 = "GET /index.html HTTP/1.1Host: " + host + "\r\n\r\n";
            String req2 = "GET /index.html                    HTTP/1.1\r\nHost: " + host + "\r\n\r\n";
            String req3 = "GET /index.html\r\nHost: " + host + "\r\n\r\n";

            switch(type){
                case 1: req = req1; break;
                case 2: req = req2; break;
                case 3: req = req3; break;
                default: req = ""; break;
            }


            out.write(req.getBytes());


            StringBuilder response = new StringBuilder();
            while ((chint = inReader.read()) != -1) {
                ch = (char) chint;
                response.append(ch);
                if (ch == '\n')
                    ncount++;
                if (ncount == 3)
                    break;
            }

            System.out.println("\n" + response);
        }catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + host);
            System.exit(1);

        }
    }

    //closes the socket
    public void close(){
        try {
            inReader.close();
            socket.close();
            System.exit(0);
        }catch(IOException e){
            System.err.println("Couldn't get I/O for the connection to " + host);
            System.exit(1);
        }
    }

}
