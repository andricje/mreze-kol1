package pogodi_rec_1.najjace;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class ServerThread implements Runnable {

    private Socket socket;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("Dobrodošli na server!");

            String[] reci = {"vuk", "pas", "rak", "tigar", "mačka", "ptica", "žirafa", "hijena", "kamila"};
            boolean igraURadu = true;

            while (igraURadu) {
                int duzina = Integer.parseInt(in.readLine());
                String rec = randomRecSaDuzinom(reci, duzina);
                String pogodjeno = "_".repeat(duzina);
                int pokusaji = duzina * 2;
                out.println(pogodjeno);

                while (pokusaji-- > 0) {
                    String unos = in.readLine();
                    if (unos.contains("exit")) break;

                    char slovo = unos.charAt(0);
                    List<Integer> pozicije = nadjiPozicije(rec, slovo);

                    char[] temp = pogodjeno.toCharArray();
                    for (int i : pozicije) temp[i] = slovo;
                    String novo = new String(temp);

                    if (novo.equals(pogodjeno)) {
                        out.println("Slovo nije u reči. Pokušajte ponovo.");
                    } else if (!novo.contains("_")) {
                        out.println("Čestitam, reč je: " + novo + ". Nova reč? (DA/NE)");
                        break;
                    } else {
                        out.println("Pogođeno slovo: " + novo);
                        pogodjeno = novo;
                    }

                    if (pokusaji == 0) {
                        out.println("Nemate više pokušaja.");
                        igraURadu = false;
                    }
                }

                if (igraURadu) {
                    String odgovor = in.readLine();
                    if (!odgovor.equalsIgnoreCase("DA")) {
                        out.println("Vidimo se sledeći put!");
                        igraURadu = false;
                    } else {
                        out.println("Unesite dužinu reči:");
                    }
                }
            }

            socket.close();
            Server.clientDisconnected();

        } catch (IOException e) {
            e.printStackTrace();
            Server.clientDisconnected();
        }
    }

    private String randomRecSaDuzinom(String[] lista, int duzina) {
        List<String> filtrirane = new ArrayList<>();
        for (String rec : lista) {
            if (rec.length() == duzina) filtrirane.add(rec);
        }
        return filtrirane.isEmpty() ? "" : filtrirane.get(new Random().nextInt(filtrirane.size()));
    }

    private List<Integer> nadjiPozicije(String rec, char c) {
        List<Integer> pozicije = new ArrayList<>();
        for (int i = 0; i < rec.length(); i++) {
            if (rec.charAt(i) == c) pozicije.add(i);
        }
        return pozicije;
    }
}
