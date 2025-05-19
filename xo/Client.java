import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static final String HOST = "localhost";
    public static final int PORT = 2000 + (123 % 24); // isti broj kao kod servera

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(HOST, PORT);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        Scanner scanner = new Scanner(System.in);

        // Šaljemo start
        out.println("start");

        Thread listener = new Thread(() -> {
            try {
                String msg;
                while ((msg = in.readLine()) != null) {
                    System.out.println(msg);
                    if (msg.equals("Vas potez. Unesite potez u formatu {i,j}:")||msg.equals("Your move")) {
                        System.out.print("Unesi potez u formatu {i,j}: ");
                        String move = scanner.nextLine();
                        out.println(move);
                    } else if (msg.contains("Nevalidan potez ili nije tvoj red.") || msg.contains("Neispravan unos poruke")) {
                        System.out.println("Potez nije validan. Pokušajte ponovo.");
                        System.out.print("Unesi potez u formatu {i,j}: ");
                        String move = scanner.nextLine();
                        out.println(move);
                    }
                }
            } catch (IOException e) {
                System.out.println("Veza sa serverom prekinuta.");
            }
        });

        listener.start();
    }
}
