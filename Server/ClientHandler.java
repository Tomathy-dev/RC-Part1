package Server;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;


public class ClientHandler implements Runnable {
    
    private final Socket socket;
    private final String host;
    private final int port;

    private final String dir = "./Server";

    public ClientHandler(Socket s, String host, int port){
        this.socket = s;
        this.host = host;
        this.port = port;
    }

    public String DateFormat(){
        DateTimeFormatter day = DateTimeFormatter.ofPattern("dd");
        DateTimeFormatter year = DateTimeFormatter.ofPattern("yyyy HH:mm:ss");
        LocalDateTime nowLDT = LocalDateTime.now();
        Calendar nowC = Calendar.getInstance();
        StringBuilder date = new StringBuilder("");

        switch(nowC.get(Calendar.DAY_OF_WEEK)){
            case 1: date.append("Sun, "); break;
            case 2: date.append("Mon, "); break;
            case 3: date.append("Tue, "); break;
            case 4: date.append("Wed, "); break;
            case 5: date.append("Thu, "); break;
            case 6: date.append("Fri, "); break;
            case 7: date.append("Sat, "); break;
        }

        date.append(day.format(nowLDT));

        switch(nowC.get(Calendar.MONTH)){
            case 0: date.append(" Jan "); break;
            case 1: date.append(" Feb "); break;
            case 2: date.append(" Mar "); break;
            case 3: date.append(" Apr "); break;
            case 4: date.append(" May "); break;
            case 5: date.append(" Jun "); break;
            case 6: date.append(" Jul "); break;
            case 7: date.append(" Aug "); break;
            case 8: date.append(" Sep "); break;
            case 9: date.append(" Oct "); break;
            case 10: date.append(" Nov "); break;
            case 11: date.append(" Dec "); break;
        }

        date.append(year.format(nowLDT));

        return date.toString();

    }

    public void run() {
        String myName = Thread.currentThread().getName() + " ";

        System.out.println("[Server]::Created " + myName + "\n");
        System.out.println("[Server]::Reading request from socket...\n");
        try{
            BufferedReader bfReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                String fileContent = "";
                StringBuilder response = new StringBuilder();
                StringBuilder s = new StringBuilder();
                StringBuilder sbmethod = new StringBuilder();
                StringBuilder sbpage = new StringBuilder();
                StringBuilder sbversion = new StringBuilder();
                StringBuilder sbhostLine = new StringBuilder();
                StringBuilder sbclength = new StringBuilder();
                StringBuilder sbbody = new StringBuilder();
                String reply, clength, method, page, version, hostLine, body;
                String name = "";
                String id = "";
                int charint;
                int ncount = 0;
                int rcount = 0;
                int scount = 0;
                char ch;
                int val = 0;
                int cl = 0;
                int status = 0;
                boolean bad = false;

                while ((charint = bfReader.read()) != -1){
                    ch = (char) charint;
                    s.append(ch);
                    if(ch == ' '){
                        break;
                    }else{
                        sbmethod.append(ch);
                    }
                }

                method = sbmethod.toString();

                if(!(method.equals("GET") || method.equals("POST"))){
                    bad = true;
                    status = 501;
                }

                while ((charint = bfReader.read()) != -1){
                    ch = (char) charint;
                    s.append(ch);
                    if(ch == ' ' && val == 0){
                        bad = true;
                        status = 400;
                    }else if(ch == ' '){
                        break;
                    }else{
                        sbpage.append(ch);
                        val++;
                    }
                }

                page = sbpage.toString();
                if(!bad){
                    if (!((page.equals("/index.html") && method.equals("GET")) || (page.equals("/simpleForm.html") && method.equals("POST")))) {
                        bad = true;
                        status = 404;
                    }
                }

                val = 0;
                while ((charint = bfReader.read()) != -1){
                    ch = (char) charint;
                    s.append(ch);
                    if(ch == ' ' && val == 0){
                        bad = true;
                        status = 400;
                    }else if(ch == '\r'){
                        break;
                    }else{
                        sbversion.append(ch);
                        val++;
                    }
                }

                version = sbversion.toString();
                if(!bad){
                    if (!version.equals("HTTP/1.1")) {
                        bad = true;
                        status = 505;
                    }
                }

                charint = bfReader.read();
                ch = (char) charint;
                s.append(ch);
                if(ch != '\n'){
                    bad = true;
                    status = 400;
                }

                while ((charint = bfReader.read()) != -1){
                    ch = (char) charint;
                    s.append(ch);
                    if(ch == '\r'){
                        break;
                    }else{
                        sbhostLine.append(ch);
                    }
                }

                hostLine = sbhostLine.toString();
                if(!bad){
                    if(!hostLine.equals("Host: " + host + ":" + port)){
                        bad = true;
                        status = 421;
                    }
                }

                val = 0;
                if(!method.equals("POST")){
                    while(val < 3){
                        charint = bfReader.read();
                        ch = (char) charint;
                        s.append(ch);
                        val++;
                    }
                }else{

                    charint = bfReader.read();
                    ch = (char) charint;
                    s.append(ch);

                    while ((charint = bfReader.read()) != -1){
                        ch = (char) charint;
                        s.append(ch);
                        if(ch == '\r'){
                            break;
                        }else{
                            sbclength.append(ch);
                        }
                    }

                    clength = sbclength.toString();
                    clength = clength.replace("Content-Length: ", "");
                    cl = Integer.parseInt(clength);
                    while(val < 3){
                        charint = bfReader.read();
                        ch = (char) charint;
                        s.append(ch);
                        val++;
                    }
                    val = 0;
                    while(val < cl){
                        charint = bfReader.read();
                        ch = (char) charint;
                        s.append(ch);
                        sbbody.append(ch);
                        val++;
                    }

                    body = sbbody.toString();

                    if(!bad){
                        String[] temp1 = body.split("&");
                        if(temp1.length == 2){
                            String[] tempname = temp1[0].split("=");
                            String[] tempid = temp1[1].split("=");
                            if (!tempname[0].equals("StudentName") || !tempid[0].equals("StudentID")) {
                                bad = true;
                                status = 400;
                            }else{
                                name = tempname[1];
                                id = tempid[1];
                            }
                        }else{
                            bad = true;
                            status = 400;
                        }
                    }
                }
                System.out.println("[Server]: from [Client]\n" + s);
                System.out.println("[Server] Preparing reply...\n");

                if (bad) {
                    switch (status) {
                        case 400:
                            System.out.println("[Server]Status: " + status);
                            response.append("HTTP/1.1 400 Bad Request\r\n");
                            break;
                        case 404:
                            System.out.println("[Server]Status: " + status);
                            response.append("HTTP/1.1 404 Not Found\r\n");
                            break;
                        case 421:
                            System.out.println("[Server]Status: " + status);
                            response.append("HTTP/1.1 421 Misdirected Request\r\n");
                            break;
                        case 501:
                            System.out.println("[Server]Status: " + status);
                            response.append("HTTP/1.1 501 Not Implemented\r\n");
                            break;
                        case 505:
                            System.out.println("[Server]Status: " + status);
                            response.append("HTTP/1.1 505 HTTP Version Not Supported\r\n");
                            break;
                    }
                } else {
                    status = 200;
                    System.out.println("[Server]Status: " +  status);
                    response.append("HTTP/1.1 200 OK\r\n");
                }

                if(!bad){
                    if(method.equals("GET")){
                        BufferedReader file = new BufferedReader((new FileReader(dir + page)));
                        StringBuilder fileS = new StringBuilder();
                        String read;
                        while ((read = file.readLine()) != null) {
                            fileS.append(read);
                        }
                        fileContent = fileS.toString();
                        file.close();
                    }else{
                        fileContent = "<h1>" + name + "</h1><p>" + id + "</p>";
                    }
                }

                response.append("Date: ").append(DateFormat()).append("GMT\r\n");
                if(!bad) {
                    response.append("Content-length: ").append(fileContent.length()).append("\r\n");
                }
                response.append("\r\n");
                if(!bad){
                    response.append(fileContent);
                }

                reply = response.toString();
                System.out.println("[Server] Reply ready!\n");
                System.out.println(reply);
                OutputStream out = socket.getOutputStream();
                out.write(reply.getBytes());
                out.flush();
                System.out.println("[HTTP] Reply sent!");


            }
        }catch (Exception e) {
            try {

                System.out.println("!EXCEPTION FOUND! ==> socket from " + myName + "closed");
                socket.close();

            } catch (IOException ioe) {

                System.err.println("I/O Exception at thread " + myName);
                System.exit(1);

            }
        }
    }
}
