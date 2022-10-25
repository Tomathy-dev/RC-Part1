*******Instruções para compilação, execução e testagem*********

O Projeto está dividido em duas packages, Server e Client. 
Dentro do Server temos as classes MyHttpServer e ClientHandler:

/*****************MyHttpServer*****************\
Classe que gere as threads e connexão com Clientes de fora.
Recebe uma conexão e decide se criará uma thread ou se devolverá uma mensagem de erro.


/*****************ClientHandler*****************\
Classe que gere os requests dos clients, avalia-os e retorna uma resposta adequada

-------------------------------------------------

Dentro do Client temos as classes MyWebClient, My HttpClient e TestMP1:


/*****************MyWebClient*****************\
Interface que agrupa métodos de envio de requests

/*****************MyHttpClient*****************\
Classe que implementa MyWebClient e disponibiliza os vários métodos para enviar Requests para o server.


/*****************TestMP1*****************\
Classe que permite ao utilizador escolher que request pretende mandar para o server.

-------------------------------------------------

*Compilação*

Passo 1: Compilar as classes de Client "$ javac Client/MyHttpClient.java Client/MyWebClient.java Client/TestMP1.java";

Passo 2: Compilar as classes de Server "$ javac Server/MyHttpServer.java Server/ClientHandler.java";

(Um dos membros do grupo teve problemas de compilação, e apenas conseguiu compilar o código com o auxílio do IntelliJ IDEA)

*Instruções de execução*
Passo 1: Abrir um terminal e correr "$ java MyHttpServer <port>" (onde <port> corresponde à port que o Utilizador pretende utilizar"

Passo 2: Abrir um outro terminal e correr "$ java TestMp1 localhost <port>" (o port terá de ser o mesmo utilizado no Passo 1, para existir conexão entre Client e Server)

*Funcionalidade*

O projeto implementa todas as funcionalidades requeridas no enunciado do Projeto

 