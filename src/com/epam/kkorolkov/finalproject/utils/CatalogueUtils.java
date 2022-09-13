package com.epam.kkorolkov.finalproject.utils;

public class CatalogueUtils {
    public static boolean validateIsbn(String isbn) {
        if (isbn.length() != 13) {
            return false;
        }
        int s = 0;
        for (int i = 0; i < 13; i++) {
            char ch = isbn.charAt(i);
            if (ch > '9' || ch < '0') {
                return false;
            }
            s += (i % 2 != 0) ? (ch - '0') * 3 : ch - '0';
        }
        return s % 10 == 0;
    }
}
