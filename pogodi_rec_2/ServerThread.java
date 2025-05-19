package pogodi_rec_2;

import java.net.*;
import java.io.*;
import java.util.*;

public class ServerThread implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private static final List<String> LISTA1 = Arrays.asList("jabuka", "breskva", "mango", "limun", "kajsija");
    private static final List<String> LISTA2 = Arrays.asList("krastavac", "paradajz", "kupus", "rotkvica", "batat");
    private static final List<String> LISTA3 = Arrays.asList("toto", "milka", "plazma", "kinder", "snikers");

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    private String shiftText(String text, int shift) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isLowerCase(c) ? 'a' : 'A';
                int offset = (c - base + shift) % 26;
                if (offset < 0) offset += 26;
                sb.append((char)(base + offset));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);

            out.println("Dobrodošli na server!");
            out.println("Pošaljite broj n (1-5):");

            String nStr = in.readLine();
            if (nStr == null) {
                closeConnection();
                return;
            }

            int n;
            try {
                n = Integer.parseInt(nStr);
                if (n < 1 || n > 5) {
                    out.println("Neispravan broj n. Prekid.");
                    closeConnection();
                    return;
                }
            } catch (NumberFormatException e) {
                out.println("Neispravan unos. Prekid.");
                closeConnection();
                return;
            }

            out.println("Pošaljite šifrovanu poruku:");

            String encMsg = in.readLine();
            if (encMsg == null) {
                closeConnection();
                return;
            }

            // Odredi shift za dešifrovanje
            int shift;
            if (n <= 3) shift = -(n + 2);
            else if (n == 3) shift = -(n - 2);
            else shift = -(n + n);

            String decMsg = shiftText(encMsg, shift);

            List<String> wordList;
            if (decMsg.equalsIgnoreCase("Zelim da igram!")) {
                wordList = LISTA1;
            } else if (decMsg.equalsIgnoreCase("Igrao bih!")) {
                wordList = LISTA2;
            } else if (decMsg.equalsIgnoreCase("Daj mi reč!")) {
                wordList = LISTA3;
            } else {
                out.println("Nepoznata poruka: " + decMsg);
                closeConnection();
                return;
            }

            playGame(wordList, shift);

        } catch (IOException e) {
            System.out.println("Greška u komunikaciji sa klijentom: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private void playGame(List<String> wordList, int shift) throws IOException {
        Random rand = new Random();
        boolean playAgain = true;

        while (playAgain) {
            String word = wordList.get(rand.nextInt(wordList.size()));
            char[] guessed = new char[word.length()];
            Arrays.fill(guessed, '_');
            Set<Character> guessedLetters = new HashSet<>();
            int attempts = 10;

            out.println("Počinjemo igru! Reč ima " + word.length() + " slova.");
            out.println("Reč: " + Arrays.toString(guessed));

            while (attempts > 0) {
                out.println("Unesite slovo:");
                String encGuess = in.readLine();
                if (encGuess == null) {
                    playAgain = false;
                    return;
                }
                String guessStr = shiftText(encGuess, -shift).toLowerCase();

                if (guessStr.equalsIgnoreCase("Ne želim više da igram!")) {
                    out.println("Vidimo se sledeći put!");
                    playAgain = false;
                    return;
                }

                if (guessStr.length() != 1 || !Character.isLetter(guessStr.charAt(0))) {
                    out.println("Unesite jedno slovo.");
                    continue;
                }

                char ch = guessStr.charAt(0);
                if (guessedLetters.contains(ch)) {
                    out.println("Slovo već pokušano, probajte drugo.");
                    continue;
                }
                guessedLetters.add(ch);

                if (word.indexOf(ch) >= 0) {
                    for (int i = 0; i < word.length(); i++) {
                        if (word.charAt(i) == ch) {
                            guessed[i] = ch;
                        }
                    }
                    out.println("Uspešno pogođeno slovo '" + ch + "'!");
                } else {
                    attempts--;
                    out.println("Žao mi je, uneto slovo ne postoji u reči. Preostali pokušaji: " + attempts);
                }

                out.println("Trenutni izgled reči: " + Arrays.toString(guessed));

                if (new String(guessed).equals(word)) {
                    out.println("Čestitam, reč je: " + Arrays.toString(guessed));
                    out.println("Da li želite novu reč za pogađanje? (DA/NE)");

                    String odgovor = in.readLine();
                    if (odgovor == null || odgovor.equalsIgnoreCase("NE")) {
                        out.println("Vidimo se sledeći put!");
                        playAgain = false;
                        break;
                    } else {
                        out.println("Nastavljamo sa novom rečju...");
                        break;
                    }
                }
            }

            if (!new String(guessed).equals(word)) {
                out.println("Nemate više pokušaja. Reč je bila: " + word);
                out.println("Da li želite novu reč za pogađanje? (DA/NE)");

                String odgovor = in.readLine();
                if (odgovor == null || odgovor.equalsIgnoreCase("NE")) {
                    out.println("Vidimo se sledeći put!");
                    playAgain = false;
                    break;
                } else {
                    out.println("Nastavljamo sa novom rečju...");
                }
            }
        }
    }

    private void closeConnection() {
        try {
            if (socket != null) socket.close();
        } catch (IOException ignored) {}
    }
}
