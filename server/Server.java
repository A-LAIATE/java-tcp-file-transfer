import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The Server class initializes a server application that listens for
 * incoming client connections and processes them using a fixed thread pool.
 * It demonstrates basic server-side socket programming with thread pool
 * management
 * to handle multiple client connections concurrently.
 */
public class Server {
    // Server port to listen on.
    private static final int PORT = 9100;

    // The size of the thread pool to handle client connections.
    // Determines how many clients can be served concurrently.
    private static final int THREAD_POOL_SIZE = 20;

    /**
     * The main method starts the server, sets up the thread pool,
     * and continuously listens for incoming client connections.
     * For each connection, a ClientHandler instance is created and executed in a
     * separate thread,
     * allowing the server to handle multiple clients simultaneously.
     *
     * @param args Command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
        // Initialize the ExecutorService with a fixed number of threads for handling
        // client connections.
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            // Continuously listen for client connections. The accept() method blocks until
            // a connection is made.
            while (true) {
                Socket clientSocket = serverSocket.accept(); // Accept an incoming connection.
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                // For each connection, submit a new task (ClientHandler) to the thread pool for
                // processing.
                // The ClientHandler is responsible for handling the specifics of the
                // client-server communication.
                executor.execute(new ClientHandler(clientSocket));
            }
        } catch (Exception e) {
            // Print stack trace to the console in case of an exception.
            e.printStackTrace();
        } finally {
            // Attempt to gracefully shut down the thread pool, stopping all active tasks.
            executor.shutdown();
        }
    }
}
