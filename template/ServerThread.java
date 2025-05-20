package template;

import java.io.*;
import java.net.Socket;

public class ServerThread implements Runnable {

    private Socket socket;

    public ServerThread(Socket socket) {
        this.socket = socket; // socket koji predstavlja konekciju sa jednim klijentom
    }

    public void run() {
        try {
            // Omogućavamo prijem i slanje poruka ka klijentu
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Pošaljemo uvodnu poruku klijentu
            out.println("Dobrodošli! Unesite komandu (npr: zbir 5 10, pozdrav Ana, exit)");

            String porukaOdKlijenta;

            // Glavna petlja – čeka i obrađuje sve poruke koje klijent pošalje
            while ((porukaOdKlijenta = in.readLine()) != null) {

                // === SVA LOGIKA KOMUNIKACIJE I OBRADA KORISNIČKOG UNOSA JE OVDE ===

                // Ako klijent pošalje 'exit', završavamo komunikaciju
                if (porukaOdKlijenta.equalsIgnoreCase("exit")) {
                    out.println("Kraj komunikacije. Pozdrav!");
                    break;
                }

                // Parsiramo poruku ako je u obliku komande
                String[] delovi = porukaOdKlijenta.split(" ");

                // U zavisnosti od prve reči, odlučujemo šta da radimo
                switch (delovi[0]) {
                    case "zbir":
                        // Komanda: zbir 5 10 → odgovor: Zbir je: 15
                        int a = Integer.parseInt(delovi[1]);
                        int b = Integer.parseInt(delovi[2]);
                        out.println("Zbir je: " + (a + b));
                        break;

                    case "pozdrav":
                        // Komanda: pozdrav Ime → odgovor: Ćao, Ime!
                        out.println("Ćao, " + delovi[1] + "!");
                        break;

                    case "duzina":
                        // Komanda: duzina rec → vraća dužinu reči
                        out.println("Dužina reči je: " + delovi[1].length());
                        break;

                    default:
                        // Ako nije prepoznata komanda
                        out.println("Nepoznata komanda. Probajte opet.");
                        break;
                }

                // === OVDJE MOŽEŠ DODAVATI NOVU LOGIKU — npr. igra, validacija, itd. ===
            }

            // Zatvaranje socket-a (prekid veze sa klijentom)
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
