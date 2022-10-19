import java.net.*;
import java.io.*;

public class MyHttpServer{
    public static void main( String[] args ){
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            while (true) {
                // implement client handler here
            }
        } catch (Exception e) {
            System.out.println("Error:" +   e.getMessage());
        }
    }
}