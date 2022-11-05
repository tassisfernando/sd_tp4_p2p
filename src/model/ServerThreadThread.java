package model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Classe que extende a Thread, com a lógica de envio das mensagens dos usuários
 */
public class ServerThreadThread extends Thread {

    private ServerThread serverThread;
    private Socket socket;
    private PrintWriter printWriter;

    /**
     * Método construtor que recebe o Socket do ServerThread e a própria Thread do server
     * @param socket
     * @param serverThread
     */
    public ServerThreadThread(Socket socket, ServerThread serverThread) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    public PrintWriter getPrintWriter() {
        return printWriter;
    }

    /**
     * Reescrita do método run que possui a lógica de recebimento e envio das mensagens
     */
    @Override
    public void run() {
        try {
            // Criação do reader com o inputStream do socket
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            // Criação do writer com o outputStream do socket, utilizado na ServerThread para enviar mensagens
            this.printWriter = new PrintWriter(socket.getOutputStream(), true);
            while (true) {
                // enviando a mensagem lida
                serverThread.sendMessage(bufferedReader.readLine());
            }
        } catch (Exception e) {
            serverThread.getServerThreadThreads().remove(this); //remove a thread atual do Set em caso de exception
        }
    }
}
