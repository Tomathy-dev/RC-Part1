package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientHandler implements Runnable {
    
    private Socket socket;
    private String myName;

    public ClientHandler(Socket s){
        this.socket = s;
    }

    public void run(){
        myName = Thread.currentThread().getName() + " ";
        System.out.println("[HTTP]::Created " + myName +"\n");
        try{

            System.out.println("[HTTP]::Reading request from socket...\n");
            BufferedReader bfReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("[HTTP]::Received new request\n");
            String s = bfReader.readLine();

            System.out.println(s);

            socket.close();

        } catch(Exception e){
            try{

                System.out.println("!EXCEPTION FOUND! ==> " + e.getClass().toString() + "::" + e.getMessage());
                socket.close();

            } catch(IOException ioe){

                e.printStackTrace();

            }

        }
    }
    
}
