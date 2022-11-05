package model;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Set;

/**
 * Classe que vai extender a classe Thread e criar um gerenciamento
 * das Threads de servers que o usuário decidiu se comunicar
 */
public class ServerThread extends Thread {

    private ServerSocket serverSocket;
    private Set<ServerThreadThread> serverThreadThreads = new HashSet<>();

    /**
     * Método construtor que inicia o ServerSocket da thread com a porta passada pelo usuário na classe Peer
     * @param portNumb
     * @throws IOException
     */
    public ServerThread(String portNumb) throws IOException {
        this.serverSocket = new ServerSocket(Integer.parseInt(portNumb));
    }

    /**
     * Reescrita do método run, contendo a lógica do gerenciamento das Threads dos
     * servers a serem ouvidos pelo peer
     */
    @Override
    public void run() {
        try {
            while (true) {
                // cria uma nova Thread do server em si, com a lógica de recebimento de mensagens que o usuario envia
                ServerThreadThread serverThreadThread = new ServerThreadThread(serverSocket.accept(), this);
                serverThreadThreads.add(serverThreadThread); // adiciona a Thread no Set
                serverThreadThread.start(); // inicia a nova thread
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que envia a mensagem digitada pelo usuário aos peers que estão conectados
     * @param message
     */
    void sendMessage(String message) {
        try {
            serverThreadThreads.forEach(t -> t.getPrintWriter().println(message));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Set<ServerThreadThread> getServerThreadThreads() {
        return serverThreadThreads;
    }
}
