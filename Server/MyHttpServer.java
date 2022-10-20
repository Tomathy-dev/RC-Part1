package Server;
import java.net.*;
import java.io.*;

public class MyHttpServer{
    public static void main(String[] args) throws IOException{
        if(args.length != 1){
            System.err.println("Usage: java MyHttpServer <port>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);
        int count = 0;

        try {
            Logger log = new Logger();
            ServerSocket serverSocket = new ServerSocket(portNumber, 5);
            while(true){

                System.out.println("Listening for connections...\n");
                Socket socket = serverSocket.accept();
                System.out.println("========Connection Accepted========\n Connection went to thread " + count );
                count++;
                ClientHandler thread = new ClientHandler(socket, log);
                (new Thread(thread)).start();

            }
        }catch (IOException e) {
			
			// something went wrong went connecting to the server or during a method call
			System.err.println("Couldn't get I/O for the connection to");
			System.exit(1);
			
		}
    }
}