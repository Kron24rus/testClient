package server;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.security.*;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * Created by Арсений on 03.02.2015.
 */
public class Server {
    public static final int PORT = 12555;
    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    public static BufferedReader in;
    private static PrintWriter out;
    private static String LOG;
    private static String PASS;
    private static boolean authorized = false;

    public static void main(String[] args) throws Exception {
        ServerDao.Connect();
    //    ServerDao.dropDatabase();
        ServerDao.CreateDB();
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            clientSocket = serverSocket.accept();
            System.out.println("Client connected from IP: "+ clientSocket.getInetAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }

        in  = new BufferedReader(new
                InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream(),true);
        String         input,output;

        System.out.println("Wait for messages");

        while (true) {
            input = in.readLine();
            if (input.equalsIgnoreCase("exit")) {
                ServerDao.ReadDB();
                ServerDao.CloseDB();
                authorized = false;
                break;
            }
            else if (input.equalsIgnoreCase("authorize")) authorize();
            else if (input.equalsIgnoreCase("register")) register();
            else if (!authorized) {
                out.println("Server response:: Type 'authorize' to login, or 'register' to register." );
                System.out.println("MAIN :: " + input);
            } else {
                out.println("Server response:: Your command: " + input);
                System.out.println("MAIN :: " + input);
            }
        }

        out.close();
        in.close();
        clientSocket.close();
        serverSocket.close();
    }

    public static void register() throws Exception {
        LOG = "";
        PASS = "";
        KeyPair kp = null;
        while (kp == null) {
            kp = getRSAkeys();
        }
        PublicKey publicKey = kp.getPublic();
        PrivateKey privateKey = kp.getPrivate();
        String publicString = Base64.encode(publicKey.getEncoded());

        out.println(publicString);

        out.println("Enter your login:");

        int countLines = Integer.parseInt(in.readLine());
        for (int i = 0; i < countLines; i++) LOG = LOG + in.readLine();

        LOG = decryptRSA(LOG, privateKey);
        int isMember = ServerDao.isMember(LOG);

        if (isMember == -1) {
            out.println("Access denied:: USER ALREADY EXISTS:: type 'authorize' or 'register'");
            return;
        }
        else out.println("Enter your password");

        countLines = Integer.parseInt(in.readLine());
        for (int i = 0; i < countLines; i++) PASS = PASS + in.readLine();

        PASS = decryptRSA(PASS, privateKey);

        ServerDao.addUser(LOG, PASS, clientSocket.getInetAddress().toString());

        authorized = true;
        out.println("Done continue work :: ");
    }

    public static void authorize() throws Exception {
        LOG = "";
        PASS = "";
        KeyPair kp = null;
        while (kp == null) {
            kp = getRSAkeys();
        }
        PublicKey publicKey = kp.getPublic();
        PrivateKey privateKey = kp.getPrivate();
        String publicString = Base64.encode(publicKey.getEncoded());

        out.println(publicString);
        out.println("Enter your login:");

        int countLines = Integer.parseInt(in.readLine());
        for (int i = 0; i < countLines; i++) LOG = LOG + in.readLine();

        LOG = decryptRSA(LOG, privateKey);

        out.println("Enter your password");

        countLines = Integer.parseInt(in.readLine());
        for (int i = 0; i < countLines; i++) PASS = PASS + in.readLine();

        PASS = decryptRSA(PASS, privateKey);

        ServerDao.updateUser(LOG, PASS, clientSocket.getInetAddress().toString());
        int memberStatus = ServerDao.isMember(LOG, PASS);

        if (memberStatus == 1) {
            authorized = true;
            out.println("Access granted:: type commands:: ");
        } else if (memberStatus == -1) {
            out.println("Access denied:: USER NOT FOUND:: type 'authorize' or 'register'");
        } else if (memberStatus == 0) {
            out.println("Access denied:: WRONG PASSWORD:: type 'authorize' or 'register'");
        }
    }

    private static KeyPair getRSAkeys() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            KeyPair kp = kpg.genKeyPair();
            return kp;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String decryptRSA(String tmp, PrivateKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");

        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] y = new BASE64Decoder().decodeBuffer(tmp);
        y = cipher.doFinal(y);
        return new String(y);
    }
}