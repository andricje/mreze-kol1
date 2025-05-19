package pogodi_rec_1;

import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 2003);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

            // Thread za čitanje serverovih poruka
            new Thread(() -> {
                try {
                    String fromServer;
                    while ((fromServer = in.readLine()) != null) {
                        System.out.println(fromServer);
                        if (fromServer.contains("Vidimo se")) System.exit(0);
                    }
                } catch (IOException e) {
                    System.out.println("Greška pri čitanju: " + e.getMessage());
                }
            }).start();

            // Glavni thread za unos od korisnika
            while (true) {
                String response = userInput.readLine();
                out.println(response);
                if (response.equalsIgnoreCase("Ne želim više da igram!")) break;
            }

        } catch (IOException e) {
            System.out.println("Greška: " + e.getMessage());
        }
    }
}
