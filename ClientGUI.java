import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ClientGUI extends JFrame {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private JLabel lblStato;
    private JTextField txtNumero;
    private JButton btnInvia;
    private JPanel pannelloCentrale;

    public ClientGUI() {
        super("Gioco della Bomba - versione base");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
        connectToServer();

        setVisible(true);
    }

    private void initComponents() {
        pannelloCentrale = new JPanel(new BorderLayout(20, 20));
        pannelloCentrale.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        pannelloCentrale.setBackground(new Color(30, 30, 50));

        // Messaggio grande al centro
        lblStato = new JLabel("Connessione in corso...", SwingConstants.CENTER);
        lblStato.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblStato.setForeground(Color.WHITE);
        lblStato.setPreferredSize(new Dimension(600, 120));

        // Pannello inferiore per input
        JPanel pannelloInput = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        pannelloInput.setOpaque(false);

        txtNumero = new JTextField(12);
        txtNumero.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        txtNumero.setHorizontalAlignment(JTextField.CENTER);
        txtNumero.setEnabled(false);

        btnInvia = new JButton("Invia");
        btnInvia.setFont(new Font("Segoe UI", Font.BOLD, 22));
        btnInvia.setPreferredSize(new Dimension(140, 60));
        btnInvia.setEnabled(false);

        btnInvia.addActionListener(e -> inviaMossa());
        txtNumero.addActionListener(e -> inviaMossa());

        pannelloInput.add(txtNumero);
        pannelloInput.add(btnInvia);

        // Layout principale
        pannelloCentrale.add(lblStato, BorderLayout.CENTER);
        pannelloCentrale.add(pannelloInput, BorderLayout.SOUTH);

        add(pannelloCentrale);
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 12345);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            lblStato.setText("Connesso! Attendo inizio partita...");
            new Thread(this::leggiMessaggiServer).start();

        } catch (Exception ex) {
            lblStato.setText("Impossibile connettersi al server");
            lblStato.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this,
                    "Errore connessione:\n" + ex.getMessage(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void leggiMessaggiServer() {
        try {
            String messaggio;
            while ((messaggio = in.readLine()) != null) {
                final String msg = messaggio;
                SwingUtilities.invokeLater(() -> aggiornaInterfaccia(msg));
            }
        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> {
                lblStato.setText("Connessione persa");
                lblStato.setForeground(Color.ORANGE);
                disabilitaInput();
            });
        }
    }

    private void aggiornaInterfaccia(String msg) {
        lblStato.setText("<html><div style='text-align:center;'>" + msg.replace("\n", "<br>") + "</div></html>");

        if (msg.toLowerCase().contains("tocca a te") ||
                msg.toLowerCase().contains("inserisci un numero") ||
                msg.contains("Tocca a te!")) {
            abilitaInput();
        } else {
            disabilitaInput();
        }

        // Fine partita - effetti minimi per ora
        if (msg.contains("BOOM") || msg.toLowerCase().contains("perso")) {
            lblStato.setForeground(Color.RED);
            pannelloCentrale.setBackground(new Color(80, 0, 0));
        } else if (msg.toLowerCase().contains("vinto") || msg.contains("HAI VINTO")) {
            lblStato.setForeground(new Color(0, 220, 0));
            pannelloCentrale.setBackground(new Color(0, 60, 0));
        } else if (msg.contains("Vuoi giocare ancora") || msg.contains("giocare di nuovo")) {
            int scelta = JOptionPane.showConfirmDialog(this,
                    "Partita terminata.\nVuoi giocare ancora?",
                    "Fine partita",
                    JOptionPane.YES_NO_OPTION);

            String risposta = (scelta == JOptionPane.YES_OPTION) ? "si" : "no";
            out.println(risposta);

            if (scelta != JOptionPane.YES_OPTION) {
                chiudiTutto();
            } else {
                // reset colori per nuova partita
                pannelloCentrale.setBackground(new Color(30, 30, 50));
                lblStato.setForeground(Color.WHITE);
            }
        }
    }

    private void inviaMossa() {
        String testo = txtNumero.getText().trim();
        if (testo.isEmpty())
            return;

        try {
            int numero = Integer.parseInt(testo);
            if (numero <= 0) {
                JOptionPane.showMessageDialog(this, "Inserisci un numero positivo!", "Errore",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            out.println(numero);
            txtNumero.setText("");
            disabilitaInput();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Inserisci un numero valido!", "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abilitaInput() {
        txtNumero.setEnabled(true);
        btnInvia.setEnabled(true);
        txtNumero.requestFocus();
    }

    private void disabilitaInput() {
        txtNumero.setEnabled(false);
        btnInvia.setEnabled(false);
    }

    private void chiudiTutto() {
        try {
            if (socket != null)
                socket.close();
        } catch (Exception ignored) {
        }
        System.exit(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientGUI::new);
    }
}