package pogodi_rec_2;

import java.net.*;
import java.io.*;
import java.util.*;

public class Client {
    private static final int PORT = (/*tvoj broj indeksa*/ 17 % 24) + 2000;
    private static final String HOST = "localhost";

    private static String shiftText(String text, int shift) {
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

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
        Scanner scanner = new Scanner(System.in)) {

       while (true) {
           String response = in.readLine();
           if (response == null) break;
           System.out.println("SERVER: " + response);
           if (response.contains("Pošaljite broj n")) break;
       }

       System.out.print("Unesite broj n: ");
       int n = scanner.nextInt();
       scanner.nextLine(); // konzumiraj novi red
       out.println(n);

       String poruka;
       if (n <= 3) {
           poruka = "Zelim da igram!";
       } else if (n == 4) {
           poruka = "Igrao bih!";
       } else {
           poruka = "Daj mi reč!";
       }

       int shift;
       if (n <= 3) shift = n + 2;
       else if (n == 3) shift = n - 2;
       else shift = n + n;

       String sifrovana = shiftText(poruka, shift);
       out.println(sifrovana);

       while (true) {
           String line = in.readLine();
           if (line == null) break;
           System.out.println("SERVER: " + line);

           if (line.contains("Unesite slovo:")) {
               System.out.print("Vaš unos: ");
               String guess = scanner.nextLine();
               if (guess.equalsIgnoreCase("Ne želim više da igram!")) {
                   String enc = shiftText(guess, shift);
                   out.println(enc);
                   break;
               } else {
                   out.println(shiftText(guess, shift));
               }
           }
       }

   } catch (IOException e) {
       System.out.println("Greška u klijentu: " + e.getMessage());
   }
}
}
