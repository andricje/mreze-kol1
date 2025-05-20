package pogodi_rec_1.najjace;

import java.io.*;
import java.net.*;

public class Server {

    private static int clientCount = 0;
    private static int activeClients = 0;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(2025);
        serverSocket.setSoTimeout(1000); // omogući timeout da ne blokira zauvek

        BufferedReader terminal = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Server pokrenut na portu 2025...");

        while (true) {
            if (clientCount > 0 && activeClients == 0) {
                System.out.print("Zatvoriti server? (da/ne): ");
                if (terminal.readLine().equalsIgnoreCase("da")) break;
            }

            try {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ServerThread(clientSocket)).start();
                clientCount++;
                activeClients++;
            } catch (SocketTimeoutException e) {
                // ignorisi, koristi se za provere iz petlje
            }
        }

        serverSocket.close();
        terminal.close();
    }

    public static synchronized void clientDisconnected() {
        activeClients--;
    }
}
