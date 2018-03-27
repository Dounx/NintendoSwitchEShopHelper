package Util;

import java.util.HashMap;

public class Localization {
    private static HashMap<String, String> sChineseMap = new HashMap<>();

    private static String getChinese(String word) {
        if (sChineseMap.containsKey(word)) {
            return  sChineseMap.get(word);
        }
        return null;
    }
}
