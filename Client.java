import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        final String HOST = "localhost";
        final int PORT = 7777;

        try (Socket socket = new Socket(HOST, PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("[CLIENT] Connesso!");

            String line;
            while ((line = in.readLine()) != null) {
                int val = Integer.parseInt(line.trim());
                System.out.println("  Ricevuto: " + val);

                if (val <= 0) {
                    System.out.println("!!! BOOM !!! Client fa esplodere la bomba!");
                    break;
                }

                val--;
                System.out.println("  Invio: " + val);
                out.println(val);
                out.flush();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}