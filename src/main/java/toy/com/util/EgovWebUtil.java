package toy.com.util;


import java.util.regex.Pattern;

public class EgovWebUtil {
    public static String clearXSSMinimum(String value) {
        if (value != null && !value.trim().equals("")) {
            String returnValue = value.replaceAll("&", "&amp;");
            returnValue = returnValue.replaceAll("<", "&lt;");
            returnValue = returnValue.replaceAll(">", "&gt;");
            returnValue = returnValue.replaceAll("\"", "&#34;");
            returnValue = returnValue.replaceAll("'", "&#39;");
            return returnValue;
        } else {
            return "";
        }
    }

    public static String clearXSSMaximum(String value) {
        String returnValue = clearXSSMinimum(value);
        returnValue = returnValue.replaceAll("%00", (String)null);
        returnValue = returnValue.replaceAll("%", "&#37;");
        returnValue = returnValue.replaceAll("\\.\\./", "");
        returnValue = returnValue.replaceAll("\\.\\.\\\\", "");
        returnValue = returnValue.replaceAll("\\./", "");
        returnValue = returnValue.replaceAll("%2F", "");
        return returnValue;
    }

    public static String clearXSSMaximumAndBr(String value) {
        String returnValue = clearXSSMaximum(value);
        returnValue = returnValue.replaceAll("\n", "<br>\n");
        return returnValue;
    }

    public static String changeBr(String value) {
        return value.replaceAll("\n", "<br>\n");
    }

    public static String filePathBlackList(String value) {
        if (value != null && !value.trim().equals("")) {
            String returnValue = value.replaceAll("\\.\\./", "");
            returnValue = returnValue.replaceAll("\\.\\.\\\\", "");
            return returnValue;
        } else {
            return "";
        }
    }

    public static String filePathReplaceAll(String value) {
        if (value != null && !value.trim().equals("")) {
            String returnValue = value.replaceAll("/", "");
            returnValue = returnValue.replaceAll("\\.", "");
            returnValue = returnValue.replaceAll("\\.\\.", "");
            returnValue = returnValue.replaceAll("&", "");
            return returnValue;
        } else {
            return "";
        }
    }

    public static String filePathWhiteList(String value) {
        return value;
    }

    public static boolean isIPAddress(String str) {
        Pattern ipPattern = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
        return ipPattern.matcher(str).matches();
    }

    public static String removeCRLF(String parameter) {
        return parameter.replaceAll("\r", "").replaceAll("\n", "");
    }

    public static String removeSQLInjectionRisk(String parameter) {
        return parameter.replaceAll("\\p{Space}", "").replaceAll("\\*", "").replaceAll("%", "").replaceAll(";", "").replaceAll("-", "").replaceAll("\\+", "").replaceAll(",", "");
    }

    public static String removeOSCmdRisk(String parameter) {
        return parameter.replaceAll("\\p{Space}", "").replaceAll("\\*", "").replaceAll("|", "").replaceAll(";", "");
    }

    public static String clearSQLReservedWord(String value) {
        if (value != null && !value.trim().equals("")) {
            String returnValue = value.toUpperCase();
            returnValue = returnValue.replaceAll("SELECT ", "");
            returnValue = returnValue.replaceAll("UPDATE ", "");
            returnValue = returnValue.replaceAll("DELETE ", "");
            returnValue = returnValue.replaceAll("WHERE ", "");
            returnValue = returnValue.replaceAll("ORDER BY ", "");
            returnValue = removeOSCmdRisk(returnValue);
            return returnValue;
        } else {
            return "";
        }
    }
}
