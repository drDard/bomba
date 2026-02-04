import java.io.*;
import java.net.*;
import java.util.Random;

public class Server {
    public static void main(String[] args) {
        int port = 12345;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server avviato sulla porta " + port + " ... in attesa");

            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("Giocatore connesso!");
                new Thread(() -> giocaPartita(client)).start();
            }

        } catch (IOException e) {
            System.out.println("Errore server: " + e.getMessage());
        }
    }

    private static void giocaPartita(Socket client) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(), true)
        ) {
            Random rand = new Random();
            boolean continua = true;

            while (continua) {
                int bomba = rand.nextInt(21) + 5; // 5–25
                System.out.println("Nuova partita - valore bomba (solo server): " + bomba);

                out.println("Nuova partita iniziata!");
                out.println("Tocca a te! Inserisci un numero positivo:");

                int valoreAttuale = bomba;
                boolean partitaInCorso = true;

                while (partitaInCorso) {
                    String linea = in.readLine();
                    if (linea == null) return;

                    int scelta;
                    try {
                        scelta = Integer.parseInt(linea.trim());
                    } catch (NumberFormatException e) {
                        out.println("Numero non valido! Inserisci un numero intero positivo.");
                        continue;
                    }

                    if (scelta <= 0) {
                        out.println("Deve essere positivo! Riprova.");
                        continue;
                    }

                    if (scelta >= valoreAttuale) {
                        out.println("BOOM! Hai perso!");
                        System.out.println("Client ha perso");
                        partitaInCorso = false;
                    } else {
                        valoreAttuale -= scelta;
                        System.out.println("Client toglie " + scelta + " → resta " + valoreAttuale);

                        // Turno server
                        if (valoreAttuale <= 0) {
                            out.println("Non ho più mosse... HAI VINTO!");
                            System.out.println("Server perso");
                            partitaInCorso = false;
                        } else {
                            int mossaServer = rand.nextInt(5) + 1; // 1–5
                            System.out.println("Server toglie " + mossaServer);

                            if (mossaServer > valoreAttuale) {
                                out.println("Il server ha fatto esplodere la bomba! HAI VINTO!");
                                System.out.println("Server perso");
                                partitaInCorso = false;
                            } else {
                                valoreAttuale -= mossaServer;
                                out.println("Server ha tolto " + mossaServer + ". Tocca a te!");
                            }
                        }
                    }
                }

                // Fine partita → chiedi se rigiocare
                out.println("Partita finita!");
                out.println("Vuoi giocare ancora? (si/no)");

                String risposta = in.readLine();
                if (risposta == null || !risposta.trim().equalsIgnoreCase("si")) {
                    out.println("Grazie per aver giocato! Ciao ciao");
                    continua = false;
                } else {
                    out.println("Ok! Nuova partita...");
                }
            }

        } catch (IOException e) {
            System.out.println("Connessione persa con il client");
        } finally {
            try {
                client.close();
            } catch (IOException ignored) {}
        }
    }
}