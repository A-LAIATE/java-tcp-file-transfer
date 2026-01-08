import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.File;
import java.io.IOException;

/**
 * Handles client requests in a multi-threaded environment,
 * processing file list and upload requests.
 */
public class ClientHandler implements Runnable {
    private Socket clientSocket; // Client connection.

    /**
     * Initializes a new ClientHandler with a client socket.
     * 
     * @param clientSocket The socket through which the client is connected.
     */
    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    /**
     * The main logic of handling client requests.
     * Reads commands from the client and responds accordingly.
     */
    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String message;
            // Process incoming messages until "EOF" signals the end.
            while ((message = in.readLine()) != null) {
                if ("EOF".equals(message)) {
                    // Skip further processing if message is the end-of-file marker.
                    continue;
                }
                // Split received message into command and potential arguments.
                String[] parts = message.split(" ", 2);
                if (parts.length == 0) {
                    // Ignore empty commands.
                    continue;
                }
                // Process commands in a case-insensitive manner.
                String command = parts[0].toLowerCase();
                switch (command) {

                    case "list":
                        // List the files available on the server.
                        handleShowCommand(out);
                        break;

                    case "put":
                        if (parts.length > 1) {
                            // Attempt to upload a file to the server.
                            boolean actionCompleted = handlePutCommand(parts[1], in, out);

                            // Log the result of the upload attempt.
                            LogUtility.logAction(clientSocket.getInetAddress().getHostAddress(), "put " + parts[1],
                                    actionCompleted, actionCompleted ? "File uploaded successfully"
                                            : "Upload failed - File already exists");
                        } else {
                            // Respond with an error if the put command lacks a filename.
                            out.println("Error: No filename specified for put command.\nEOF");
                        }
                        break;

                    default:
                        // Handle unknown commands.
                        out.println("Invalid command.\nEOF");
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a list of files in the server's directory to the client.
     * 
     * @param out The PrintWriter to respond to the client.
     * @throws IOException If an I/O error occurs.
     */
    private void handleShowCommand(PrintWriter out) throws IOException {
        File folder = new File("serverFiles");
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles != null && listOfFiles.length > 0) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    // Only list regular files (not directories).
                    out.println(file.getName());
                }
            }
        } else {
            // Inform the client if no files are found.
            out.println("No files found.");
        }

        // Log the list operation
        LogUtility.logAction(clientSocket.getInetAddress().getHostAddress(), "list", true, "Files listed successfully");

        // Mark the end of the file list.
        out.println("EOF");
    }

    /**
     * Handles uploading a file from the client to the server.
     * 
     * @param filename The name of the file being uploaded.
     * @param in       The BufferedReader to read the file content from the client.
     * @param out      The PrintWriter to send responses to the client.
     * @return true if the file was successfully uploaded, false otherwise.
     * @throws IOException If an I/O error occurs during file upload.
     */
    private boolean handlePutCommand(String filename, BufferedReader in, PrintWriter out) throws IOException {
        filename = new File(filename).getName();
        File directory = new File("serverFiles");

        if (!directory.exists()) {
            directory.mkdirs(); // Create the directory if it doesn't exist
        }

        File file = new File(directory, filename); // Correctly create the file object
        if (file.exists()) {
            // Prevent file overwrite and inform the client.
            out.println("Error: File already exists on the server.\nEOF");
            return false;

        } else {
            try (BufferedWriter fileOut = new BufferedWriter(new FileWriter(file))) {
                String line;
                while (!(line = in.readLine()).equals("EOF")) {
                    // Write each line from the client to the file.
                    fileOut.write(line);
                    fileOut.newLine();
                }
                // Acknowledge successful upload to the client.
                out.println("File " + filename + " uploaded successfully.\nEOF");
                return true;
            } catch (IOException e) {
                // Report any error that occurs during the upload.
                out.println("Error saving the file: " + e.getMessage() + "\nEOF");
                return false;
            }
        }
    }
}
