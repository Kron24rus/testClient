package client;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import server.Server;
import server.ServerDao;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.sql.SQLException;

/**
 * Created by kron on 12.10.15.
 */
public class Client {

    private static BufferedReader in;
    private static PrintWriter out;
    private static BufferedReader inu;

    public static void main(String[] args) throws Exception {
        System.out.println("Welcome to CLient side");
        Socket fromserver = null;

        fromserver = new Socket("localhost", 12555);

        in  = new BufferedReader(new InputStreamReader(fromserver.getInputStream()));
        out = new PrintWriter(fromserver.getOutputStream(),true);
        inu = new BufferedReader(new InputStreamReader(System.in));

        String fuser,fserver;

        while ((fuser = inu.readLine())!=null) {
            if (fuser.equalsIgnoreCase("register")) {
                out.println(fuser);
                register();
            }
            else if (fuser.equalsIgnoreCase("authorize")) {
                out.println(fuser);
                authorize();
            }
            else {
                out.println(fuser);
                fserver = in.readLine();
                System.out.println(fserver);
            }
            if (fuser.equalsIgnoreCase("exit")) break;
        }

        fromserver.close();
    }

    private static void register() throws Exception {
        String publicString = in.readLine();
        System.out.println(in.readLine());
        String Login = inu.readLine();
        Login = encryptRSA(Login, publicString);
        out.println(Login.split("\n").length);
        out.println(Login);

        String response = in.readLine();
        if (response.equalsIgnoreCase("Access denied:: USER ALREADY EXISTS:: type 'authorize' or 'register'")) {
            System.out.println(response);
            return;
        } else {
            System.out.println(response);
        }

        String Pass = inu.readLine();
        Pass = encryptRSA(Pass, publicString);
        out.println(Pass.split("\n").length);
        out.println(Pass);

        System.out.println(in.readLine());
    }

    private static void authorize() throws Exception {
        String publicString = in.readLine();
        System.out.println(in.readLine());
        String Login = inu.readLine();
        Login = encryptRSA(Login, publicString);
        out.println(Login.split("\n").length);
        out.println(Login);

        System.out.println(in.readLine());

        String Pass = inu.readLine();
        Pass = encryptRSA(Pass, publicString);
        out.println(Pass.split("\n").length);
        out.println(Pass);

        System.out.println(in.readLine());
    }

    private static String encryptRSA(String tmp, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");

        X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64.decode(key));
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey pkS = kf.generatePublic(spec);

        cipher.init(Cipher.ENCRYPT_MODE, pkS);
        byte[] x = cipher.doFinal(tmp.getBytes());
        tmp = new BASE64Encoder().encode(x);
       // System.out.println(tmp);
        return tmp;
    }
}
