/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.worph.testvfs;

/**
 *
 * @author Worph
 */
public class Utils {

    public static String generate(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder pass = new StringBuilder("");
        for (int x = 0; x < length; x++) {
            int i = (int) Math.floor(Math.random() * 62);
            pass.append(chars.charAt(i));
        }
        System.out.println(pass);
        return pass.toString();
    }
    
}
