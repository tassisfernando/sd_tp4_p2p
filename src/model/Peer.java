package model;

import javax.json.Json;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.Socket;

import static utils.Utils.*;

public class Peer {

    public void start() throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("> insira o nome e a porta para este ponto: ");
        String[] setupValues = bufferedReader.readLine().split(WHITESPACE);

        ServerThread serverThread = new ServerThread(setupValues[1]);
        serverThread.start();

        new Peer().updateListenToPeers(bufferedReader, setupValues[0], serverThread);
    }

    public void updateListenToPeers(BufferedReader bufferedReader, String username, ServerThread serverThread) throws Exception {
        System.out.println("> Insira (separado por espaço) o hostname:porta ");
        System.out.println("  dos peers que deseja receber mensagens (s para pular): ");
        String input = bufferedReader.readLine();
        String[] inputValues = input.split(WHITESPACE);

        if (!input.equalsIgnoreCase(SKIP)) {
            for (String inputValue : inputValues) {
                String[] address = inputValue.split(":");
                Socket socket = null;

                try {
                    socket = new Socket(address[0], Integer.parseInt(address[1]));
                    new PeerThread(socket).start();
                } catch (Exception e) {
                    if (socket != null) {
                        socket.close();
                    } else {
                        System.out.println("Entrada inválida. Pulando para o próximo passo.");
                    }
                }
            }
        }
        communicate(bufferedReader, username, serverThread);
    }

    public void communicate(BufferedReader bufferedReader, String username, ServerThread serverThread) {
        try {
            System.out.println("> agora você pode conversar (e to exit, c to change)");

            while (true) {
                String message = bufferedReader.readLine();
                if (message.equalsIgnoreCase(EXIT)) {
                    break;
                } else if (message.equalsIgnoreCase(CHANGE)) {
                    updateListenToPeers(bufferedReader, username, serverThread);
                } else {
                    StringWriter stringWriter = new StringWriter();
                    Json.createWriter(stringWriter).writeObject(Json.createObjectBuilder()
                            .add(USERNAME, username)
                            .add(MESSAGE, message)
                            .build());
                    serverThread.sendMessage(stringWriter.toString());
                }
            }

            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
