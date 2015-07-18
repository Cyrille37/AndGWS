/**
 * 
 */
package fr.funlab.util;

import java.security.MessageDigest;

import android.util.Base64;

/**
 * @author cyrille
 *
 */
public class Crypto {

    public static String SHA1(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(text.getBytes("iso-8859-1"), 0, text.length());
            return Base64.encodeToString(md.digest(), Base64.NO_WRAP);
        }
        catch (Exception ex) {
            return null;
        }
    }

}
