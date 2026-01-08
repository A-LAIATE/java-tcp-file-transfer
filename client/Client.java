import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.File;
import java.io.IOException;

/**
 * The Client class implements a simple client application capable of
 * interacting with a server to list server files or upload a new file.
 */
public class Client {
    // Server connection settings. Utilizing localhost and a specific port
    // adheres to the requirement for both client and server to run on the same
    // host.
    private static final String HOST = "localhost";
    private static final int PORT = 9100;

    public static void main(String[] args) {
        // Validate command-line arguments to ensure at least one command is provided.
        if (args.length < 1) {
            System.out.println("Usage: java Client <command> [filepath]");
            System.exit(1);
        }
        String command = args[0];
        // Handle the 'put' command, ensuring a file path is provided.
        if ("put".equalsIgnoreCase(command) && args.length > 1) {
            File file = new File(args[1]);
            if (!file.exists()) {
                // Inform the user if the specified file does not exist, then exit.
                System.out.println("Error: File " + args[1] + " does not exist.");
                System.exit(1);
            }
            sendFileToServer(command, args[1]);

        } else if ("list".equalsIgnoreCase(command)) {
            // Process the 'list' command to retrieve files from the server.
            sendCommandToServer(command);
        } else {
            // Handle invalid commands or missing file path for 'put' command.
            System.out.println("Invalid command or missing filepath for 'put'.");
            System.exit(1);
        }
    }

    /**
     * Sends a command to the server and prints the server's response.
     * This method is used for simple commands like 'list'.
     * 
     * @param command The command to send to the server.
     */
    private static void sendCommandToServer(String command) {
        try (Socket socket = new Socket(HOST, PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(command); // Send command to server
            receiveResponseFromServer(in); // Print server's response
        } catch (IOException e) {

            System.err.println("Connection error: " + e.getMessage());
        }
    }

    /**
     * Sends a file to the server using the 'put' command.
     * This method handles the file transfer process including notifying the server
     * about the file being sent and streaming the file content.
     * 
     * @param command  The command (should be 'put').
     * @param filePath The path of the file to send.
     */
    private static void sendFileToServer(String command, String filePath) {
        try (Socket socket = new Socket(HOST, PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            sendFileContent(out, filePath); // Send the file content to the server
            receiveResponseFromServer(in); // Print server's response
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        }
    }

    /**
     * Sends the content of a specified file to the server.
     * This method streams the file line by line to the server,
     * followed by an 'EOF' signal indicating the end of the file content.
     * 
     * @param out      The PrintWriter object for sending data to the server.
     * @param filePath The path of the file to send.
     * @throws IOException If an I/O error occurs.
     */
    private static void sendFileContent(PrintWriter out, String filePath) throws IOException {
        File file = new File(filePath);
        out.println("put " + file.getName()); // Notify server about the file

        try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = fileReader.readLine()) != null) {
                out.println(line); // Stream file content
            }
            out.println("EOF"); // End of file content signal
        }
    }

    /**
     * Receives and prints the server's response to the sent command or file
     * content.
     * This method reads the server's response until the 'EOF' signal is
     * encountered,
     * indicating the end of the message.
     * 
     * @param in The BufferedReader object for reading the server's response.
     * @throws IOException If an I/O error occurs.
     */
    private static void receiveResponseFromServer(BufferedReader in) throws IOException {
        String serverResponse;
        while (!(serverResponse = in.readLine()).equals("EOF")) {
            System.out.println(serverResponse); // Print each line of response
        }
    }
}
