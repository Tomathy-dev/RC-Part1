import java.net.*;
import java.io.*;

public class MyHttpServer{
    public static void main(String[] args) throws IOException{
        if(args.length != 1){
            System.err.println("Usage: java MyHttpServer <port>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);

        try {
            ServerSocket serverSocket = new ServerSocket(portNumber, 5);
            while(true){
                System.out.println("Connecting to Server...");
                Socket socket = serverSocket.accept();
                System.out.println("Connection Accepted");
            }
        }catch (IOException e) {
			
			// something went wrong went connecting to the server or during a method call
			System.err.println("Couldn't get I/O for the connection to");
			System.exit(1);
			
		}
    }
}