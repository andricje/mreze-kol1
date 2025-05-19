package pogodi_rec_1;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 123 % 24 + 2000; // primer: 2003
    private static final List<String> words = Arrays.asList("vuk", "pas", "rak", "tigar", "mačka", "ptica", "žirafa", "hijena", "kamila");

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server pokrenut na portu " + PORT);

        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("Klijent se povezao: " + socket.getInetAddress());
            new ServerThread(socket, words).start();
        }
    }
}
