package app;

import model.Peer;

public class PeerStarter {
    public static void main(String[] args) throws Exception {
        Peer peer = new Peer();
        peer.start();
    }
}