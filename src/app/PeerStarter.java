package app;

import model.Peer;

/**
 * Classe que inicializa um peer
 */
public class PeerStarter {
    public static void main(String[] args) throws Exception {
        Peer peer = new Peer();
        peer.start();
    }
}