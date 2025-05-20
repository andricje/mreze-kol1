package template;

import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) throws IOException {
        // Otvara se serverski socket na portu 2025
        ServerSocket serverSocket = new ServerSocket(2025);
        System.out.println("Server je pokrenut... Čeka klijente.");

        while (true) {
            // Čekamo da se neki klijent poveže
            Socket socket = serverSocket.accept();

            // Kada se klijent poveže, pokrećemo novu nit koja ga obrađuje
            new Thread(new ServerThread(socket)).start();
        }
    }
}
