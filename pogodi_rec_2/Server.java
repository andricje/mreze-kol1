package pogodi_rec_2;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {
    private static final int PORT = (/*tvoj broj indeksa*/ 17 % 24) + 2000;
    private static ServerSocket serverSocket;
    private static ExecutorService pool = Executors.newFixedThreadPool(10); // podrška za više klijenata

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server startovan na portu " + PORT);

            Scanner scanner = new Scanner(System.in);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Klijent povezan: " + clientSocket.getInetAddress());
                pool.execute(new ServerThread(clientSocket));
            }

        } catch (IOException e) {
            System.out.println("Greška u serveru: " + e.getMessage());
        } finally {
            try {
                if (serverSocket != null)
                    serverSocket.close();
                pool.shutdown();
            } catch (IOException ignored) {}
        }
    }
}
