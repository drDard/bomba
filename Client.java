import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 12345;
        Scanner tastiera = new Scanner(System.in);

        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("Connesso al server! Inizia la partita...");

            boolean giocaAncora = true;

            while (giocaAncora) {
                boolean inPartita = true;

                while (inPartita) {
                    String messaggio = in.readLine();

                    if (messaggio == null) {
                        System.out.println("Connessione chiusa dal server.");
                        giocaAncora = false;
                        break;
                    }

                    System.out.println(messaggio);

                    // Riconoscimento fine partita
                    if (messaggio.contains("BOOM") ||
                        messaggio.contains("Hai perso") ||
                        messaggio.contains("HAI VINTO") ||
                        messaggio.contains("perso") ||
                        messaggio.contains("vinto")) {
                        inPartita = false;
                        continue;
                    }

                    // Domanda rigioca
                    if (messaggio.contains("Vuoi giocare ancora") ||
                        messaggio.contains("giocare di nuovo")) {
                        inPartita = false;

                        System.out.print("→ ");
                        String risposta = tastiera.nextLine().trim().toLowerCase();

                        out.println(risposta);

                        if (risposta.equals("si") || risposta.equals("si")) {
                            System.out.println("Nuova partita...");
                        } else {
                            System.out.println("Ok, esco. Ciao!");
                            giocaAncora = false;
                        }
                        continue;
                    }

                    // Input durante il turno del giocatore
                    if (messaggio.toLowerCase().contains("tocca a te") ||
                        messaggio.toLowerCase().contains("inserisci un numero")) {
                        System.out.print("→ ");
                        String scelta = tastiera.nextLine();
                        out.println(scelta);
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Errore: " + e.getMessage());
        } finally {
            tastiera.close();
            System.out.println("Client terminato.");
        }
    }
}