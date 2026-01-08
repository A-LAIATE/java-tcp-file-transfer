import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * LogUtility provides static methods to log server requests and actions to a
 * log file.
 * It supports logging both simple requests and more detailed action
 * information, including completion status and messages.
 */
public class LogUtility {
    // Path to the log file where all log entries will be recorded.
    private static final String LOG_FILE = "log.txt";

    /**
     * Logs a request received from a client. This method is for simplicity and
     * backward compatibility.
     * It automatically marks the request as completed without a specific message.
     *
     * @param clientIP The IP address of the client making the request.
     * @param request  The request made by the client.
     */
    public static void logRequest(String clientIP, String request) {
        // Directly invoke the detailed logging method assuming completion.
        logAction(clientIP, request, true, ""); // No specific message is attached to simple request logs.
    }

    /**
     * Overloads the logAction method to allow logging without a specific message.
     * This provides flexibility in logging actions with or without messages.
     *
     * @param clientIP  The IP address of the client related to the action.
     * @param action    The action performed or requested.
     * @param completed A flag indicating whether the action was completed
     *                  successfully.
     */
    public static void logAction(String clientIP, String action, boolean completed) {
        // Delegate to the main logging method with an empty message for actions without
        // specific messages.
        logAction(clientIP, action, completed, "");
    }

    /**
     * Logs detailed information about an action, including its completion status
     * and an optional message.
     * This is the most detailed logging method in this utility class.
     *
     * @param clientIP  The IP address of the client related to the action.
     * @param action    The action performed or requested.
     * @param completed A flag indicating whether the action was completed
     *                  successfully.
     * @param message   An optional message providing additional details about the
     *                  action or its outcome.
     */
    public static void logAction(String clientIP, String action, boolean completed, String message) {

        // Format the current time as a timestamp.
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd|HH:mm:ss"));

        // Determine the completion status as a string.
        String status = completed ? "Completed" : "Not Completed";

        // Concatenate all parts to form the complete log entry.
        String logEntry = timestamp + "|" + clientIP + "|" + action + "|" + status + "|" + message;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            // Write the log entry to the log file and start a new line.
            writer.write(logEntry);
            writer.newLine();
        } catch (IOException e) {

            // Handle potential I/O errors during logging.
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }
}
