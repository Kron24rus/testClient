package CryptoTest;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;


/**
 * Created by kron on 13.10.15.
 */
public class RSA {
    public static void main(String[] args) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException {
        String text  =
                "kron";

        Cipher cipher = Cipher.getInstance("RSA");
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair kp = keyGen.genKeyPair();

        PublicKey publicKey = kp.getPublic();
        PrivateKey privateKey = kp.getPrivate();

        String publicString = com.sun.org.apache.xerces.internal.impl.dv.util.Base64.encode(publicKey.getEncoded());
      //  System.out.println(publicString);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(com.sun.org.apache.xerces.internal.impl.dv.util.Base64.decode(publicString));
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey pkS = kf.generatePublic(spec);

        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] x = cipher.doFinal(text.getBytes());

        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] y = cipher.doFinal(x);
        System.out.println(new String(y));


        System.out.println();
        cipher.init(Cipher.ENCRYPT_MODE, pkS);
        x = cipher.doFinal(text.getBytes());

        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        y = cipher.doFinal(x);
        System.out.println(new String(y));
        System.out.println(new String(x));
      //  System.out.println(new String(y));
    }
}
