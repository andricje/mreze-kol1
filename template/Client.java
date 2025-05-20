package template;

import java.io.*;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        // Konektovanje na server na localhost i port 2025
        Socket socket = new Socket("localhost", 2025);

        // Ulazni tok (čitanje sa servera)
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Izlazni tok (slanje serveru)
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        // Čitanje sa tastature
        BufferedReader tastatura = new BufferedReader(new InputStreamReader(System.in));

        String porukaSaServera;

        // Glavna petlja – sve dok server šalje poruke
        while ((porukaSaServera = in.readLine()) != null) {
            // Prikažemo poruku korisniku
            System.out.println("SERVER: " + porukaSaServera);

            // Uzimamo unos korisnika sa tastature
            String unos = tastatura.readLine();

            // Šaljemo unos serveru
            out.println(unos);

            // Ako korisnik kuca 'exit', završavamo
            if (unos.equalsIgnoreCase("exit")) {
                break;
            }
        }

        // Zatvaranje konekcije
        socket.close();
    }
}
