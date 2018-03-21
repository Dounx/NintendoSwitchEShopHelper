package Util;

public class ConvertBetweenStringAndStringArray {
    private static final String strSeparator = ",";

    public static String convertArrayToString(String[] array){
        if (array != null) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0;i < array.length; i++) {
                builder.append(array[i]);
                // Do not append blank space at the end of last element
                if(i < array.length - 1){
                    builder.append(strSeparator);
                }
            }
            return builder.toString();
        }
        return null;
    }
    public static String[] convertStringToArray(String str){
        if (str != null) {
            String[] arr = str.split(strSeparator);
            return arr;
        }
        return null;
    }
}
