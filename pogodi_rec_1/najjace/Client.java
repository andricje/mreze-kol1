package pogodi_rec_1.najjace;
import java.io.*;
import java.net.Socket;

public class Client {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 2025);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.println(in.readLine()); // Dobrodošlica
            System.out.print("Odaberite dužinu reči (3,5,6): ");
            out.println(keyboard.readLine()); // dužina

            System.out.println(in.readLine()); // _ _ _ _ _

            while (true) {
                System.out.print("Unesite slovo: ");
                String slovo = keyboard.readLine();
                out.println(slovo);

                if (slovo.contains("exit")) break;

                String odgovor = in.readLine();
                System.out.println(odgovor);

                if (odgovor.contains("Čestitam")) {
                    System.out.print("Nova reč? (DA/NE): ");
                    out.println(keyboard.readLine());
                    System.out.println(in.readLine());
                    break;
                }

                if (odgovor.contains("Nemate")) {
                    return;
                }
            }
        }
    }
}
