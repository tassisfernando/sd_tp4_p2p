package model;

import javax.json.Json;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.Socket;

import static utils.Constants.*;

/**
 * Classe que representa um peer, com métodos para iniciar,
 */
public class Peer {

    /**
     * Método que starta um peer, lendo dados do usuário e criando uma serverThread para ele
     * @throws Exception
     */
    public void start() throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in)); // para leitura de dados
        System.out.println("> insira o nome e a porta para este ponto: ");
        String[] setupValues = bufferedReader.readLine().split(WHITESPACE); // lendo o nome e a porta do usuário

        ServerThread serverThread = new ServerThread(setupValues[1]);   // criando um objeto serverThread passando como parâmetro
                                                                        // o número da porta escolhido pelo usuário
        serverThread.start(); // starta a thread do server criado anteriormente

        updateListenToPeers(bufferedReader, setupValues[0], serverThread);  // chama o método que configura o chat, atualizando
                                                                            // os peers a serem ouvidos
    }

    /**
     * Método que atualiza os peers que esse peer deseja ouvir
     * @param bufferedReader
     * @param username
     * @param serverThread
     * @throws Exception
     */
    public void updateListenToPeers(BufferedReader bufferedReader, String username, ServerThread serverThread) throws Exception {
        System.out.println("> Insira (separado por espaço) o hostname:porta ");
        System.out.println("  dos peers que deseja receber mensagens (s para pular): ");
        String input = bufferedReader.readLine(); // lê os dados do usuário: hostname:porta dos peers a serem ouvidos
        String[] inputValues = input.split(WHITESPACE); // separa os peers passando pelo usuário

        if (!input.equalsIgnoreCase(SKIP)) { // se for "s" não configura as threads dos peers a serem ouvidos
            for (String inputValue : inputValues) {
                String[] address = inputValue.split(":"); // separando o hostname e a porta do peer
                Socket socket = null;

                try {
                    socket = new Socket(address[0], Integer.parseInt(address[1]));  // criando um objeto Socket, passando
                                                                                    // o hostname e a porta
                    new PeerThread(socket).start(); // criando e startando uma peerThread, passando o objeto por parâmetro
                } catch (Exception e) {
                    if (socket != null) {
                        socket.close();
                    } else {
                        System.out.println("Entrada inválida. Pulando para o próximo passo.");
                    }
                }
            }
        }
        communicate(bufferedReader, username, serverThread); // chamando o método que inicia a comunicação de fato do chat
    }

    /**
     * Método que possui a lógica de comunicação do chat
     * @param bufferedReader
     * @param username
     * @param serverThread
     */
    public void communicate(BufferedReader bufferedReader, String username, ServerThread serverThread) {
        try {
            System.out.println("> agora você pode conversar (e to exit, c to change)");

            while (true) {
                String message = bufferedReader.readLine(); // lê a mensagem enviada pelo usuário
                if (message.equalsIgnoreCase(EXIT)) {
                    break; // se a mensagem for "e", sai do chat e finaliza o programa
                } else if (message.equalsIgnoreCase(CHANGE)) {  // se a mensagem for "c" chama o método que configura
                                                                // os peers a serem ouvidos
                    updateListenToPeers(bufferedReader, username, serverThread);
                } else {    // para os outros casos, cria o JSON com a mensagem e envia para os hosts que estão ouvindo
                    StringWriter stringWriter = new StringWriter();
                    Json.createWriter(stringWriter).writeObject(Json.createObjectBuilder()
                            .add(USERNAME, username)
                            .add(MESSAGE, message)
                            .build()); // cria um JSON com as propriedades username e mensagem enviada
                    serverThread.sendMessage(stringWriter.toString()); // enviada a mensagem com o JSON criado
                }
            }

            System.exit(0); // finaliza o programa
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
