import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private Socket socket;
    private BufferedReader br;
    private PrintWriter out;

    public Client() {
        try {
            System.out.println("Sending request to server...");
            // Corrected IP address format and ensured no additional spaces
            socket = new Socket("192.168.196.38", 7777);
            System.out.println("Connection established.");

            // Initialize input and output streams
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true); // Enable auto-flush

            startReading();
            startWriting();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startReading() {
        // Thread to continuously read data from the server
        Runnable r1 = () -> {
            System.out.println("Reader started...");
            try {
                while (true) {
                    String msg = br.readLine();
                    if (msg == null || msg.equalsIgnoreCase("exit")) {
                        System.out.println("Server terminated the chat.");
                        break;
                    }
                    System.out.println("Server: " + msg);
                }
            } catch (Exception e) {
                System.out.println("Connection closed.");
            } finally {
                closeResources();
            }
        };
        new Thread(r1).start();
    }

    public void startWriting() {
        // Thread to continuously take input from the user and send it to the server
        Runnable r2 = () -> {
            System.out.println("Writer started...");
            try (BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in))) {
                while (!socket.isClosed()) {
                    String content = br1.readLine();
                    out.println(content); // Send the message to the server
                    if (content.equalsIgnoreCase("exit")) {
                        System.out.println("You terminated the chat.");
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println("Connection closed.");
            } finally {
                closeResources();
            }
        };
        new Thread(r2).start();
    }

    private void closeResources() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("This is a client.");
        new Client();
    }
}
