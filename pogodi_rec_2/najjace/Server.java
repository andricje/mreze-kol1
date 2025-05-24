package pogodi_rec_2.najjace;
import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 2001; // ili indeks % 24 + 2000
    public static List<Socket> connectedClients = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server pokrenut na portu " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                connectedClients.add(clientSocket);
                new ServerThread(clientSocket, connectedClients).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
