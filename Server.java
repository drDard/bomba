import java.io.*;
import java.net.*;
import java.util.Random;

public class Server {
    public static void main(String[] args) {
        final int PORT = 7777;

        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("[SERVER] In ascolto sulla porta " + PORT);

            try (Socket client = server.accept();
                    PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))) {

                System.out.println("[SERVER] Client connesso");

                Random r = new Random();
                int miccia = r.nextInt(6) + 5; // 5..10
                System.out.println("Bomba armata: miccia = " + miccia);

                out.println(miccia);

                String line;
                while ((line = in.readLine()) != null) {
                    int val = Integer.parseInt(line.trim());
                    System.out.println("  Ricevuto: " + val);

                    if (val <= 0) {
                        System.out.println("!!! BOOM !!! Server fa esplodere la bomba!");
                        break;
                    }

                    val--;
                    System.out.println("  Invio: " + val);
                    out.println(val);
                    out.flush();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}