package Server;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*****************ClientHandler*****************\
 Classe que gere os requests dos clients, avalia-os e retorna uma resposta adequada
 */
public class ClientHandler implements Runnable {
    
    private final Socket socket;
    private final String privhost;

    private final String dir = "./Server";

    public ClientHandler(Socket s, String host){
        this.socket = s;
        this.privhost = host;
    }

    //Returns a Matcher to evaluate if the request has correct spacing
    public Matcher matchingRegX(String regex, String s){
        return Pattern.compile(regex).matcher(s);
    }

    //Method that return Day of the Week and Month to later attach it to response by the server
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

        try{
            BufferedReader bfReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                String fileContent = "";
                StringBuilder response = new StringBuilder();
                StringBuilder sb = new StringBuilder();
                StringBuilder sbmethod = new StringBuilder();
                StringBuilder sbpage = new StringBuilder();
                StringBuilder sbversion = new StringBuilder();
                StringBuilder sbhostLine = new StringBuilder();
                StringBuilder sbclength = new StringBuilder();
                StringBuilder sbbody = new StringBuilder();
                String cabecalho, reply, method, version, host;
                String page = "";
                String name = "";
                String id = "";
                String body = "";
                int charint, index;
                int ncount = 0;
                int rcount = 0;
                int scount = 0;
                int contentLength = 0;
                char ch;
                int val = 0;
                int cl = 0;
                int status = 0;
                boolean bad = false;
                boolean flag = false; //flag: Has Stream been read?

                System.out.println("[Server]::Reading request from socket...\n");

                //Reads the Request Line and headers of the request
                while ((charint = bfReader.read()) != -1) {
                    ch = (char) charint;
                    sb.append(ch);
                    switch(ch){
                        case ' ': scount++; break;
                        case '\r': rcount++; break;
                        case '\n': ncount++; break;
                    }
                    if(sb.indexOf("\r\n\r\n") != -1)
                        break;
                    flag = true;
                }
                if(flag){
                    cabecalho = sb.toString();

                    method = cabecalho.substring(0,cabecalho.indexOf(" "));

                    Matcher matcherSpace = matchingRegX("(?<=[^ ]) (?=[^ ])", cabecalho);
                    Matcher matcherR = matchingRegX("\\r(?=\\n)", cabecalho);
                    Matcher matcherS = matchingRegX("(?<=\\r)\\n", cabecalho);
                    int s_count = 0;
                    int r_count = 0;
                    int n_count = 0;
                    while(matcherSpace.find())
                        s_count++;
                    while(matcherR.find())
                        r_count++;
                    while(matcherS.find())
                        n_count++;

                    //Number of spaces, \r and \n is expected or not
                    if(s_count != scount || r_count != rcount || n_count != ncount){
                        bad = true;
                        status = 400;
                    }
                    //Method not recognized, Malformed GET/POST request
                    if(!method.equals("GET") && !method.equals("POST")){
                        bad = true;
                        status = 501;
                    }else if(method.equals("GET") && (scount != 3 || rcount != 3 || ncount != 3)){
                        bad = true;
                        status= 400;
                    }else if(method.equals("POST") && (scount != 4 || rcount != 4 || ncount != 4)){
                        bad = true;
                        status= 400;
                    }

                    String[] arr = cabecalho.split("\\r\\n");
                    String[] requestLines = Arrays.stream(arr).filter(x -> x.length() > 0).toArray(String[]::new);
                    if(method.equals("POST")) { //Reads the body of the request, if there is one (only happens with POST requests)
                        while (((charint = bfReader.read()) != -1)) {
                            val++;
                            ch = (char) charint;
                            sbbody.append(ch);
                            if (val == (contentLength = Integer.parseInt(requestLines[2].substring(16))))
                                break;
                        }
                        body = sbbody.toString();
                    }

                    if(!bad){
                        //appropriate number of headers
                        if((method.equals("GET") && requestLines.length != 2) || (method.equals("POST") && requestLines.length != 3)){
                            bad = true;
                            status = 400;
                        }
                        if(!bad) {
                            String[] stLine = requestLines[0].split(" ");
                            String[] hostLine = requestLines[1].split(" ");
                            if(stLine.length != 3 || hostLine.length != 2){ //Correct number of attributes for the headers
                                bad = true;
                                status = 400;
                            }else {
                                page = stLine[1];
                                version = stLine[2];
                                host = hostLine[1];
                                if(!((page.equals("/index.html") && method.equals("GET")) || (page.equals("/simpleForm.html") && method.equals("POST")))){//Do we recognize the page requested?
                                    bad = true;
                                    status = 404;
                                }
                                if(!version.equals("HTTP/1.1") && !bad){//Version Control
                                    bad = true;
                                    status = 505;
                                }
                                if(!host.equals(privhost) && !bad){
                                    bad = true;
                                    status = 421;
                                }else if(method.equals("POST") && !bad){//Evaluation of POST body, given then to name and id variables
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
                        }
                    }
                    System.out.println("[Server]:\n" + cabecalho + body);
                    System.out.println("[Server]:Preparing reply..." + status);

                    if (bad) {
                        switch (status) {
                            case 400:
                                System.out.println("[Server]:Status: " + status);
                                response.append("HTTP/1.1 400 Bad Request\r\n");
                                break;
                            case 404:
                                System.out.println("[Server]:Status: " + status);
                                response.append("HTTP/1.1 404 Not Found\r\n");
                                break;
                            case 421:
                                System.out.println("[Server]:Status: " + status);
                                response.append("HTTP/1.1 421 Misdirected Request\r\n");
                                break;
                            case 501:
                                System.out.println("[Server]:Status: " + status);
                                response.append("HTTP/1.1 501 Not Implemented\r\n");
                                break;
                            case 505:
                                System.out.println("[Server]:Status: " + status);
                                response.append("HTTP/1.1 505 HTTP Version Not Supported\r\n");
                                break;
                        }
                    } else {
                        status = 200;
                        System.out.println("[Server]:Status: " +  status);
                        response.append("HTTP/1.1 200 OK\r\n");
                    }

                    if(!bad){//Content to be sent
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

                    response.append("Date: ").append(DateFormat()).append(" GMT\r\n");
                    if(!bad) {
                        response.append("Content-length: ").append(fileContent.length()).append("\r\n");
                    }
                    response.append("\r\n");
                    if(!bad){
                        response.append(fileContent);
                    }

                    reply = response.toString();
                    System.out.println("[Server]:Reply ready!\n");
                    System.out.println(reply);
                    OutputStream out = socket.getOutputStream();
                    out.write(reply.getBytes());
                    out.flush();
                    System.out.println("[Server]:Reply sent!");
                }
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
