import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Server {
    public static final int PORT = 2000 + (123 % 24); // Zameni 123 sa svojim brojem indeksa
    private static ServerThread[] players = new ServerThread[2];
    private static char[][] board = new char[3][3];
    private static int currentPlayerIndex;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server pokrenut na portu: " + PORT);

        // Inicijalizuj tablu
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                board[i][j] = ' ';

        int playerCount = 0;
        while (playerCount < 2) {
            Socket socket = serverSocket.accept();
            ServerThread thread = new ServerThread(socket, playerCount);
            players[playerCount] = thread;
            thread.start();
            playerCount++;
        }

        // Nasumično odredi ko prvi igra
        currentPlayerIndex = new Random().nextInt(2);
        players[currentPlayerIndex].sendMessage("start x");
        players[1 - currentPlayerIndex].sendMessage("start o");
        players[currentPlayerIndex].sendMessage("Your move");
    }

    public static synchronized boolean makeMove(int playerIndex, int row, int col) {
        if (playerIndex != currentPlayerIndex || row < 0 || row > 2 || col < 0 || col > 2 || board[row][col] != ' ')
            return false;

        board[row][col] = (playerIndex == 0) ? 'x' : 'o';
        broadcastBoard();

        if (checkWinner()) {
            players[playerIndex].sendMessage("Pobeda!");
            players[1 - playerIndex].sendMessage("Poraz.");
            return true;
        } else if (isFull()) {
            players[0].sendMessage("Nereseno.");
            players[1].sendMessage("Nereseno.");
            return true;
        }

        currentPlayerIndex = 1 - currentPlayerIndex;
        players[currentPlayerIndex].sendMessage("Your move");
        return true;
    }

    private static void broadcastBoard() {
        StringBuilder sb = new StringBuilder("Board:\n");
        for (char[] row : board) {
            for (char cell : row) {
                sb.append(cell == ' ' ? "-" : cell).append(" ");
            }
            sb.append("\n");
        }
        String boardState = sb.toString();
        for (ServerThread player : players)
            player.sendMessage(boardState);
    }

    private static boolean checkWinner() {
        for (int i = 0; i < 3; i++) {
            if (same(board[i][0], board[i][1], board[i][2])) return true;
            if (same(board[0][i], board[1][i], board[2][i])) return true;
        }
        return same(board[0][0], board[1][1], board[2][2]) || same(board[0][2], board[1][1], board[2][0]);
    }

    private static boolean same(char a, char b, char c) {
        return a != ' ' && a == b && b == c;
    }

    private static boolean isFull() {
        for (char[] row : board)
            for (char cell : row)
                if (cell == ' ') return false;
        return true;
    }
}
