package Server;

import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {
    
    private Socket socket;
    private Logger log;
    private String myName;

    public ClientHandler(Socket s, Logger log){
        this.socket = s;
        this.log = log;
    }

    public void run(){
        myName = Thread.currentThread().getName() + " ";
        System.out.println("[HTTP/1.1] New thread " + myName + "\n");
        try{
            System.out.println("");
        } catch(Exception e){
            try{
                System.out.println("[HTTP/1.1] Exception found " + e.getClass().toString() + " " + e.getMessage());
                socket.close();
            } catch(IOException ioe){
                e.printStackTrace();
            }

        }
    }
    
}
