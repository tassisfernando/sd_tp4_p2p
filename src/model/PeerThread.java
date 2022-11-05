package model;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import static utils.Constants.MESSAGE;
import static utils.Constants.USERNAME;

/**
 * Classe que extende a classe Thread, que gerencia a lógica de impressão das mensagens recebidas
 */
public class PeerThread extends Thread {
    private BufferedReader bufferedReader;

    /**
     * Método construtor, que inicializar o bufferedReader com o inputStream do socket criado no Peer
     * @param socket
     * @throws IOException
     */
    public PeerThread(Socket socket) throws IOException {
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    /**
     * Reescrita do método run da thread com a lógica de exibição das mensagens recebidas
     */
    @Override
    public void run() {
        boolean flag = true;

        // enquanto não houver alguma exception, o bufferedReader vai ficar lendo o JSON recebido pelos outros peers
        while (flag) {
            try {
                JsonObject jsonObject = Json.createReader(bufferedReader).readObject();
                if (jsonObject.containsKey(USERNAME)) {
                    // exibe a mensagem recebida de outro usuário, indicando o nome dele
                    System.out.printf("[%s]: %s \n", jsonObject.getString(USERNAME), jsonObject.getString(MESSAGE));
                }
            } catch (Exception e) {
                flag = false; // setando false pra interromper o while
                interrupt(); // interrompe a thread
            }
        }
    }
}
