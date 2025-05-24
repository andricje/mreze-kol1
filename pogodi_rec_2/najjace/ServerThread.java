package pogodi_rec_2.najjace;

import java.io.*;
import java.net.*;
import java.util.*;


public class ServerThread extends Thread {
    private Socket socket;
    private List<Socket> clients;

    public ServerThread(Socket socket, List<Socket> clients) {
        this.socket = socket;
        this.clients = clients;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            out.println("Povezani ste na server.");
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("Klijent kaže: " + line);

                if (line.equalsIgnoreCase("Ne želim više da igram!")) {
                    out.println("Vidimo se sledeći put!");
                    break;
                } else {
                    out.println("Poruka primljena: " + line);
                }
            }

        } catch (IOException e) {
            System.out.println("Greška u komunikaciji: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {}

            clients.remove(socket);
            System.out.println("Klijent diskonektovan. Aktivni: " + clients.size());

            if (clients.isEmpty()) {
                System.out.println("Nema više klijenata.");
                Scanner scanner = new Scanner(System.in);
                System.out.println("Želite li da nastavite sa radom servera? (želim / ne želim)");

                String odgovor = scanner.nextLine();
                if (odgovor.equalsIgnoreCase("ne želim")) {
                    System.out.println("Server se gasi.");
                    System.exit(0);
                } else {
                    System.out.println("Server nastavlja sa radom.");
                }
            }
        }
    }
}
