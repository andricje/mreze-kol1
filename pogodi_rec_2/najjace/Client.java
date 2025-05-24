package pogodi_rec_2.najjace;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final String HOST = "localhost";
    private static final int PORT = 2001;

    public static void main(String[] args) {
        try (
            Socket socket = new Socket(HOST, PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println(in.readLine());

            String message;
            while (true) {
                System.out.print("Unesite poruku za server: ");
                message = scanner.nextLine();
                out.println(message);

                if (message.equalsIgnoreCase("Ne želim više da igram!")) {
                    break;
                }

                String response = in.readLine();
                System.out.println("Server: " + response);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
