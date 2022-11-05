package model;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import static utils.Utils.MESSAGE;
import static utils.Utils.USERNAME;

public class PeerThread extends Thread {
    private BufferedReader bufferedReader;

    public PeerThread(Socket socket) throws IOException {
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {
        boolean flag = true;

        while (flag) {
            try {
                JsonObject jsonObject = Json.createReader(bufferedReader).readObject();
                if (jsonObject.containsKey(USERNAME)) {
                    System.out.printf("[%s]: %s \n", jsonObject.getString(USERNAME), jsonObject.getString(MESSAGE));
                }
            } catch (Exception e) {
                flag = false;
                interrupt();
            }
        }
    }
}
