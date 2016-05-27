package com.zethratech.getfile;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Test {
    public static void main(String[] args) throws UnsupportedEncodingException {
        String test = "IMG 2788.JPG";
        System.out.println(URLEncoder.encode(test, "ASCII"));
    }
}
