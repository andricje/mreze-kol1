import java.io.*;
import java.net.Socket;

public class ServerThread extends Thread {
    private Socket socket;
    private int playerIndex;
    private BufferedReader in;
    private PrintWriter out;

    public ServerThread(Socket socket, int playerIndex) throws IOException {
        this.socket = socket;
        this.playerIndex = playerIndex;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    @Override
    public void run() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.equalsIgnoreCase("start")) {
                    // Igra već startovana na serveru
                } else if (line.matches("\\{\\d,\\d\\}")) {
                    int row = Character.getNumericValue(line.charAt(1));
                    int col = Character.getNumericValue(line.charAt(3));
                    boolean valid = Server.makeMove(playerIndex, row, col);
                    if (!valid) {
                        sendMessage("Nevalidan potez ili nije tvoj red.");
                    }
                } else {
                    sendMessage("Neispravan unos poruke.");
                }
            }
        } catch (IOException e) {
            System.out.println("Igrač " + playerIndex + " je prekinuo konekciju.");
        }
    }
}
