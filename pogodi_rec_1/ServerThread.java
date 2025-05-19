package pogodi_rec_1;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerThread extends Thread {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private List<String> words;
    private Random rand = new Random();

    public ServerThread(Socket socket, List<String> words) {
        this.socket = socket;
        this.words = words;
    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);

            out.println("Dobrodošli na server! Unesite koju dužinu reči želite da pogađate:");

            while (true) {
                String input = in.readLine();
                if (input == null || input.equalsIgnoreCase("Ne želim više da igram!")) {
                    out.println("Vidimo se sledeći put!");
                    break;
                }

                int length;
                try {
                    length = Integer.parseInt(input);
                } catch (Exception e) {
                    out.println("Unesite validnu dužinu (3,5,6):");
                    out.flush();
                    continue;
                }

                String word = getRandomWordOfLength(length);
                if (word == null) {
                    out.println("Nema reči te dužine. Pokušajte ponovo:");
                    out.flush();
                    continue;
                }

                int attempts = getAttempts(length);
                char[] guessed = new char[length];
                Arrays.fill(guessed, '_');
                Set<Character> guessedLetters = new HashSet<>();

                while (attempts > 0) {
                    // Šaljemo sve informacije odjednom
                    out.println("TRENUTNO_STANJE|" + Arrays.toString(guessed) + "|" + attempts);
                    out.flush();
                    
                    String guess = in.readLine();
                    if (guess == null || guess.equalsIgnoreCase("Ne želim više da igram!")) {
                        out.println("Vidimo se sledeći put!");
                        return;
                    }
                
                    if (guess.length() != 1 || !Character.isLetter(guess.charAt(0))) {
                        out.println("Unesite jedno slovo:");
                        out.flush();
                        continue;
                    }
                
                    char ch = guess.toLowerCase().charAt(0);
                    if (guessedLetters.contains(ch)) {
                        out.println("Slovo već pokušano. Pokušajte drugo:");
                    } else {
                        guessedLetters.add(ch);
                        if (word.indexOf(ch) != -1) {
                            for (int i = 0; i < word.length(); i++) {
                                if (word.charAt(i) == ch) {
                                    guessed[i] = ch;
                                }
                            }
                            out.println("Uspešno pogođeno slovo '" + ch + "'!");
                        } else {
                            out.println("Žao mi je, uneto slovo ne postoji u reči. Pokušajte ponovo!");
                            out.flush();
                            attempts--;
                        }
                    }
                
                    out.println("Reč: " + Arrays.toString(guessed) + " | Preostali pokušaji: " + attempts);
                
                    if (new String(guessed).equals(word)) {
                        out.println("Čestitam, reč je: " + Arrays.toString(guessed));
                        out.println("Da li želite novu reč za pogađanje? (DA/NE)");
                        String again = in.readLine();
                        if (again == null || again.equalsIgnoreCase("NE")) {
                            out.println("Vidimo se sledeći put!");
                            return;
                        } else {
                            out.println("Unesite dužinu reči za pogađanje:");
                            break;
                        }
                    }
                }
                

                if (!new String(guessed).equals(word)) {
                    out.println("Nemate više pokušaja. Više sreće sledeći put! Reč je bila: " + word);
                    out.println("Da li želite novu reč za pogađanje? (DA/NE)");
                    String again = in.readLine();
                    if (again == null || again.equalsIgnoreCase("NE")) {
                        out.println("Vidimo se sledeći put!");
                        return;
                    } else {
                        out.println("Unesite dužinu reči za pogađanje:");
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Greška u komunikaciji: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }

    private String getRandomWordOfLength(int length) {
        List<String> filtered = new ArrayList<>();
        for (String word : words) {
            if (word.length() == length) filtered.add(word);
        }
        if (filtered.isEmpty()) return null;
        return filtered.get(rand.nextInt(filtered.size()));
    }

    private int getAttempts(int length) {
        return switch (length) {
            case 3 -> 6;
            case 5 -> 10;
            case 6 -> 12;
            default -> 8;
        };
    }
}
