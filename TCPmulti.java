import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
    public static void main(String[] args) {
        final int MAX_CLIENTS = 10;
        ServerSocket serverSocket = null;

        try {
            // Création d'un ServerSocket écoutant sur le port 9999
            serverSocket = new ServerSocket(9999);
            System.out.println("Serveur démarré.");

            while (true) {
                // Attente de la connexion d'un client
                Socket clientSocket = serverSocket.accept(); // Acceptation d'une connexion client et création d'un
                                                             // Socket client.
                System.out.println("Nouvelle connexion entrante."); // Affichage d'un message pour indiquer qu'un
                                                                    // nouvelle connexion est établie.

                // Vérification du nombre maximal de clients
                if (Thread.activeCount() - 1 > MAX_CLIENTS) { // Vérification si le nombre de threads actifs dépasse le
                                                              // nombre maximal de clients
                    System.out.println("Nombre maximal de clients atteint. Connexion refusée.");
                    clientSocket.close(); // Fermeture de la connexion avec le client
                    continue; // Passage à la prochaine itération de la boucle
                }

                // Création d'un thread pour gérer la connexion du client
                ClientHandler clientHandler = new ClientHandler(clientSocket); // Création d'une instance de
                                                                               // ClientHandler avec le Socket client
                clientHandler.start(); // Démarrage du thread ClientHandler pour gérer la connexion
            }
        } catch (IOException e) { // Capture des exceptions liées aux entrées/sorties
            e.printStackTrace(); // Affichage de la trace de la pile d'exécution en cas d'erreur
        } finally { // Bloc de code exécuté quelle que soit l'issue du bloc try
            if (serverSocket != null) { // Vérification si le ServerSocket a été initialisé
                try { // Bloc try pour gérer les exceptions liées à la fermeture du ServerSocket
                    serverSocket.close(); // Fermeture du ServerSocket
                } catch (IOException e) { // Capture des exceptions liées aux entrées/sorties
                    e.printStackTrace(); // Affichage de la trace de la pile d'exécution en cas d'erreur
                }
            }
        }
    }

    // Définition d'une classe interne ClientHandler étendant Thread pour gérer les
    // connexions clients
    static class ClientHandler extends Thread {
        private final Socket clientSocket;

        // Constructeur prenant en paramètre un Socket client
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override // Annotation indiquant que la méthode redéfinie hérite de la classe parente
        // Méthode run() pour exécuter les opérations du thread
        public void run() {
            try {
                // Initialisation des flux de lecture et écriture
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                // Création d'un PrintWriter pour envoyer des données au client
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                // Lecture de la première ligne envoyée par le client
                String inputLine = in.readLine();

                // Simulation du traitement avec un délai avc pause de 1 seconde.
                Thread.sleep(1000);

                // Inversion de la chaîne de caractères
                StringBuilder reversedString = new StringBuilder(inputLine).reverse();

                // Envoi de la chaîne inversée au client via PrintWriter
                out.println(reversedString.toString());

                // Fermeture des flux et de la connexion
                in.close(); // Fermeture du BufferedReader
                out.close(); // Fermeture du PrintWriter
                clientSocket.close(); // Fermeture du Socket client
                // Capture des exceptions liées aux entrées/sorties ou à l'interruption du
                // thread .

            } catch (IOException | InterruptedException e) {
                e.printStackTrace(); // Affichage de la trace de la pile d'exécution en cas d'erreur
            }
        }
    }
}
