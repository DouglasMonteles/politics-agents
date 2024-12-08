package org.fga.tcc.utils;

public class StringUtils {

    public static String removeSpecialCharacters(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }

        return str.replaceAll(" ", "")
                .replaceAll("é", "e")
                .replaceAll("É", "E")
                .replaceAll("á", "a")
                .replaceAll("Á", "A")
                .replaceAll("ã", "a")
                .replaceAll("ú", "u")
                .replaceAll("ô", "o")
                .replaceAll("\\.", "");
    }

}
