package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Created by Арсений on 03.02.2015.
 */
public class Server {
    public static final int PORT = 12555;
    private static ServerSocket serverSocket;
    private static Socket clientSocket;

    public static void main(String[] args) throws IOException {
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            clientSocket = serverSocket.accept();
            System.out.println("ITS THE CLIENT, MAZAFUCKERS! EVERYONE WORKS!!! Always in service, sir!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader in  = new BufferedReader(new
                InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),true);
        String         input,output;

        System.out.println("Wait for messages");
        while ((input = in.readLine()) != null) {
            if (input.equalsIgnoreCase("exit")) break;
            out.println("/root/"+input);
            System.out.println(input);
        }

        out.close();
        in.close();
        clientSocket.close();
        serverSocket.close();
    }
}