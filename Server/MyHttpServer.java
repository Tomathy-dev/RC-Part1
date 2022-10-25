package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MyHttpServer{
    public static void main(String[] args) throws IOException{
        if(args.length != 1){
            System.err.println("Usage: java MyHttpServer <port>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);

        try {
            ThreadGroup group_t = new ThreadGroup("threads");
            ServerSocket serverSocket = new ServerSocket(portNumber);

            while(true){

                System.out.println("\nThread count" + group_t.activeCount());
                System.out.println("Listening for connections...");

                while(group_t.activeCount() >= 5){
                    Socket socket = serverSocket.accept();
                    System.out.println("[HTTP]::Received new request");
                    System.out.println("\n!!!======== Too many Connections ========!!!");
                    socket.getOutputStream().write("HTTP/1.1 503 Service Unavailable\r\n".getBytes());
                    socket.getOutputStream().flush();
                    System.out.println("[HTTP]::Response Sent");
                    socket.close();
                }

                if(group_t.activeCount() < 5) {
                    Socket socket = serverSocket.accept();
                    System.out.println("[HTTP]::Received new request");
                    System.out.println("\n======== Connection Established ========\n");
                    ClientHandler thread = new ClientHandler(socket, "localhost");
                    (new Thread(group_t, thread)).start();
                }

            }
        }catch (IOException e) {
			
			// something went wrong went connecting to the server or during a method call
			System.err.println("Couldn't get I/O for the connection to");
			System.exit(1);
			
		}
    }
}