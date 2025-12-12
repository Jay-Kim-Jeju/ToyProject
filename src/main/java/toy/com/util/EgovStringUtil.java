package toy.com.util;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class EgovStringUtil {
    public static final String EMPTY = "";

    public static String cutString(String source, String output, int slength) {
        String returnVal = null;
        if (source != null) {
            if (source.length() > slength) {
                String var10000 = source.substring(0, slength);
                returnVal = var10000 + output;
            } else {
                returnVal = source;
            }
        }

        return returnVal;
    }

    public static String cutString(String source, int slength) {
        String result = null;
        if (source != null) {
            if (source.length() > slength) {
                result = source.substring(0, slength);
            } else {
                result = source;
            }
        }

        return result;
    }

    public static boolean isEmpty(Object obj) {
        if (obj instanceof String) {
            return obj == null || "".equals(obj.toString().trim());
        } else if (obj instanceof List) {
            return obj == null || ((List)obj).isEmpty();
        } else if (obj instanceof Map) {
            return obj == null || ((Map)obj).isEmpty();
        } else if (!(obj instanceof Object[])) {
            return obj == null;
        } else {
            return obj == null || Array.getLength(obj) == 0;
        }
    }

    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    public static String remove(String str, char remove) {
        if (!isEmpty(str) && str.indexOf(remove) != -1) {
            char[] chars = str.toCharArray();
            int pos = 0;

            for(int i = 0; i < chars.length; ++i) {
                if (chars[i] != remove) {
                    chars[pos++] = chars[i];
                }
            }

            return new String(chars, 0, pos);
        } else {
            return str;
        }
    }

    public static String removeCommaChar(String str) {
        return remove(str, ',');
    }

    public static String removeMinusChar(String str) {
        return remove(str, '-');
    }

    public static String replace(String source, String subject, String object) {
        StringBuffer rtnStr = new StringBuffer();
        String preStr = "";
        String nextStr = source;
        String srcStr = source;

        while(srcStr.indexOf(subject) >= 0) {
            preStr = srcStr.substring(0, srcStr.indexOf(subject));
            nextStr = srcStr.substring(srcStr.indexOf(subject) + subject.length(), srcStr.length());
            srcStr = nextStr;
            rtnStr.append(preStr).append(object);
        }

        rtnStr.append(nextStr);
        return rtnStr.toString();
    }

    public static String replaceOnce(String source, String subject, String object) {
        StringBuffer rtnStr = new StringBuffer();
        String preStr = "";
        if (source.indexOf(subject) >= 0) {
            preStr = source.substring(0, source.indexOf(subject));
            String nextStr = source.substring(source.indexOf(subject) + subject.length(), source.length());
            rtnStr.append(preStr).append(object).append(nextStr);
            return rtnStr.toString();
        } else {
            return source;
        }
    }

    public static String replaceChar(String source, String subject, String object) {
        StringBuffer rtnStr = new StringBuffer();
        String preStr = "";
        String srcStr = source;

        for(int i = 0; i < subject.length(); ++i) {
            char chA = subject.charAt(i);
            if (srcStr.indexOf(chA) >= 0) {
                preStr = srcStr.substring(0, srcStr.indexOf(chA));
                String nextStr = srcStr.substring(srcStr.indexOf(chA) + 1, srcStr.length());
                srcStr = rtnStr.append(preStr).append(object).append(nextStr).toString();
            }
        }

        return srcStr;
    }

    public static int indexOf(String str, String searchStr) {
        return str != null && searchStr != null ? str.indexOf(searchStr) : -1;
    }

    public static String decode(String sourceStr, String compareStr, String returnStr, String defaultStr) {
        if (sourceStr == null && compareStr == null) {
            return returnStr;
        } else if (sourceStr == null && compareStr != null) {
            return defaultStr;
        } else {
            return sourceStr.trim().equals(compareStr) ? returnStr : defaultStr;
        }
    }

    public static String decode(String sourceStr, String compareStr, String returnStr) {
        return decode(sourceStr, compareStr, returnStr, sourceStr);
    }

    public static String isNullToString(Object object) {
        String string = "";
        if (object != null) {
            string = object.toString().trim();
        }

        return string;
    }

    public static String nullConvert(Object src) {
        if (src != null && src instanceof BigDecimal) {
            return ((BigDecimal)src).toString();
        } else {
            return src != null && !src.equals("null") ? ((String)src).trim() : "";
        }
    }

    public static String nullConvert(String src) {
        return src != null && !src.equals("null") && !"".equals(src) && !" ".equals(src) ? src.trim() : "";
    }

    public static int zeroConvert(Object src) {
        return src != null && !src.equals("null") ? Integer.parseInt(((String)src).trim()) : 0;
    }

    public static int zeroConvert(String src) {
        return src != null && !src.equals("null") && !"".equals(src) && !" ".equals(src) ? Integer.parseInt(src.trim()) : 0;
    }

    public static String removeWhitespace(String str) {
        if (isEmpty(str)) {
            return str;
        } else {
            int sz = str.length();
            char[] chs = new char[sz];
            int count = 0;

            for(int i = 0; i < sz; ++i) {
                if (!Character.isWhitespace(str.charAt(i))) {
                    chs[count++] = str.charAt(i);
                }
            }

            if (count == sz) {
                return str;
            } else {
                return new String(chs, 0, count);
            }
        }
    }

    public static String checkHtmlView(String strString) {
        String strNew = "";

        try {
            StringBuffer strTxt = new StringBuffer("");
            int len = strString.length();

            for(int i = 0; i < len; ++i) {
                char chrBuff = strString.charAt(i);
                switch (chrBuff) {
                    case '\n':
                        strTxt.append("<br>");
                        break;
                    case ' ':
                        strTxt.append("&nbsp;");
                        break;
                    case '"':
                        strTxt.append("&quot;");
                        break;
                    case '<':
                        strTxt.append("&lt;");
                        break;
                    case '>':
                        strTxt.append("&gt;");
                        break;
                    default:
                        strTxt.append(chrBuff);
                }
            }

            strNew = strTxt.toString();
            return strNew;
        } catch (Exception var6) {
            return null;
        }
    }

    public static String[] split(String source, String separator) throws NullPointerException {
        String[] returnVal = null;
        int cnt = 1;
        int index = source.indexOf(separator);

        int index0;
        for(index0 = 0; index >= 0; index = source.indexOf(separator, index + 1)) {
            ++cnt;
        }

        returnVal = new String[cnt];
        cnt = 0;

        for(int var8 = source.indexOf(separator); var8 >= 0; ++cnt) {
            returnVal[cnt] = source.substring(index0, var8);
            index0 = var8 + 1;
            var8 = source.indexOf(separator, var8 + 1);
        }

        returnVal[cnt] = source.substring(index0);
        return returnVal;
    }

    public static String lowerCase(String str) {
        return str == null ? null : str.toLowerCase();
    }

    public static String upperCase(String str) {
        return str == null ? null : str.toUpperCase();
    }

    public static String stripStart(String str, String stripChars) {
        int strLen;
        if (str != null && (strLen = str.length()) != 0) {
            int start = 0;
            if (stripChars == null) {
                while(start != strLen && Character.isWhitespace(str.charAt(start))) {
                    ++start;
                }
            } else {
                if (stripChars.length() == 0) {
                    return str;
                }

                while(start != strLen && stripChars.indexOf(str.charAt(start)) != -1) {
                    ++start;
                }
            }

            return str.substring(start);
        } else {
            return str;
        }
    }

    public static String stripEnd(String str, String stripChars) {
        int end;
        if (str != null && (end = str.length()) != 0) {
            if (stripChars == null) {
                while(end != 0 && Character.isWhitespace(str.charAt(end - 1))) {
                    --end;
                }
            } else {
                if (stripChars.length() == 0) {
                    return str;
                }

                while(end != 0 && stripChars.indexOf(str.charAt(end - 1)) != -1) {
                    --end;
                }
            }

            return str.substring(0, end);
        } else {
            return str;
        }
    }

    public static String strip(String str, String stripChars) {
        if (isEmpty(str)) {
            return str;
        } else {
            String srcStr = stripStart(str, stripChars);
            return stripEnd(srcStr, stripChars);
        }
    }

    public static String[] split(String source, String separator, int arraylength) throws NullPointerException {
        String[] returnVal = new String[arraylength];
        int cnt = 0;
        int index0 = 0;

        for(int index = source.indexOf(separator); index >= 0 && cnt < arraylength - 1; ++cnt) {
            returnVal[cnt] = source.substring(index0, index);
            index0 = index + 1;
            index = source.indexOf(separator, index + 1);
        }

        returnVal[cnt] = source.substring(index0);
        if (cnt < arraylength - 1) {
            for(int i = cnt + 1; i < arraylength; ++i) {
                returnVal[i] = "";
            }
        }

        return returnVal;
    }

    public static String getRandomStr(char startChr, char endChr) {
        String randomStr = null;
        int startInt = Integer.valueOf(startChr);
        int endInt = Integer.valueOf(endChr);
        if (startInt > endInt) {
            throw new IllegalArgumentException("Start String: " + startChr + " End String: " + endChr);
        } else {
            SecureRandom rnd = new SecureRandom();

            int randomInt;
            do {
                randomInt = rnd.nextInt(endInt + 1);
            } while(randomInt < startInt);

            randomStr = "" + (char)randomInt;
            return randomStr;
        }
    }

    public static String getEncdDcd(String srcString, String srcCharsetNm, String cnvrCharsetNm) {
        String rtnStr = null;
        if (srcString == null) {
            return null;
        } else {
            try {
                rtnStr = new String(srcString.getBytes(srcCharsetNm), cnvrCharsetNm);
            } catch (UnsupportedEncodingException var5) {
                rtnStr = null;
            }

            return rtnStr;
        }
    }

    public static String getSpclStrCnvr(String srcString) {
        String rtnStr = null;
        StringBuffer strTxt = new StringBuffer("");
        int len = srcString.length();

        for(int i = 0; i < len; ++i) {
            char chrBuff = srcString.charAt(i);
            switch (chrBuff) {
                case '&':
                    strTxt.append("&amp;");
                    break;
                case '<':
                    strTxt.append("&lt;");
                    break;
                case '>':
                    strTxt.append("&gt;");
                    break;
                default:
                    strTxt.append(chrBuff);
            }
        }

        rtnStr = strTxt.toString();
        return rtnStr;
    }

    public static String getTimeStamp() {
        String rtnStr = null;
        String pattern = "yyyyMMddhhmmssSSS";
        SimpleDateFormat sdfCurrent = new SimpleDateFormat(pattern, Locale.KOREA);
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        rtnStr = sdfCurrent.format(ts.getTime());
        return rtnStr;
    }

    public static String getHtmlStrCnvr(String srcString) {
        String tmpString = srcString.replaceAll("&lt;", "<");
        tmpString = tmpString.replaceAll("&gt;", ">");
        tmpString = tmpString.replaceAll("&amp;", "&");
        tmpString = tmpString.replaceAll("&nbsp;", " ");
        tmpString = tmpString.replaceAll("&apos;", "'");
        tmpString = tmpString.replaceAll("&quot;", "\"");
        tmpString = tmpString.replaceAll("amp;", "");
        return tmpString;
    }

    public static String addMinusChar(String date) {
        return date.length() == 8 ? date.substring(0, 4).concat("-").concat(date.substring(4, 6)).concat("-").concat(date.substring(6, 8)) : "";
    }

    public static String subStringByte(String strInput, int nStart, int nEnd, int bytesForDB) {
        if (strInput == null) {
            return "";
        } else {
            String strRtn = strInput;
            int slen = 0;
            int blen = 0;
            if (strInput.getBytes().length > nEnd - 1) {
                while(blen + 1 < nEnd - 1) {
                    char c = strRtn.charAt(slen);
                    ++blen;
                    ++slen;
                    if (c > 127) {
                        blen += bytesForDB - 1;
                    }
                }

                strRtn = strRtn.substring(nStart, slen);
            }

            return strRtn;
        }
    }

    public static String subStringByte(String strInput, int nStart, int nEnd) {
        return subStringByte(strInput, nStart, nEnd, 2);
    }

    public static String subStringByte(String strInput, int nEnd) {
        return subStringByte(strInput, 0, nEnd, 2);
    }

    public static String subStringNotice(String strSubject, int nMaxLen, int nViewLen) throws Exception {
        String strRtnSubject = "";
        int nSubjectLen = strSubject.getBytes("EUC-KR").length;
        if (nSubjectLen > nMaxLen) {
            String var10000 = subStringByte(strSubject, nViewLen);
            strRtnSubject = var10000 + "…";
        } else {
            strRtnSubject = strSubject;
        }

        return strRtnSubject;
    }

    public static String getStarImgIndex(float fGpaAvg) {
        String strRtn = "";
        if ((double)0.0F == (double)fGpaAvg) {
            strRtn = "00";
        } else if ((double)0.0F < (double)fGpaAvg && (double)fGpaAvg <= (double)0.5F) {
            strRtn = "01";
        } else if ((double)0.5F < (double)fGpaAvg && (double)fGpaAvg <= (double)1.0F) {
            strRtn = "02";
        } else if ((double)1.0F < (double)fGpaAvg && (double)fGpaAvg <= (double)1.5F) {
            strRtn = "03";
        } else if ((double)1.5F < (double)fGpaAvg && (double)fGpaAvg <= (double)2.0F) {
            strRtn = "04";
        } else if ((double)2.0F < (double)fGpaAvg && (double)fGpaAvg <= (double)2.5F) {
            strRtn = "05";
        } else if ((double)2.5F < (double)fGpaAvg && (double)fGpaAvg <= (double)3.0F) {
            strRtn = "06";
        } else if ((double)3.0F < (double)fGpaAvg && (double)fGpaAvg <= (double)3.5F) {
            strRtn = "07";
        } else if ((double)3.5F < (double)fGpaAvg && (double)fGpaAvg <= (double)4.0F) {
            strRtn = "08";
        } else if ((double)4.0F < (double)fGpaAvg && (double)fGpaAvg <= (double)4.5F) {
            strRtn = "09";
        } else if ((double)4.5F < (double)fGpaAvg) {
            strRtn = "10";
        }

        return strRtn;
    }

    public static String getDateFormat(Calendar cal) {
        String rtnStr = String.valueOf(cal.get(1));
        rtnStr = rtnStr + (String.valueOf(cal.get(2) + 1).length() == 1 ? "0" + String.valueOf(cal.get(2) + 1) : String.valueOf(cal.get(2) + 1));
        rtnStr = rtnStr + (String.valueOf(cal.get(5)).length() == 1 ? "0" + String.valueOf(cal.get(5)) : String.valueOf(cal.get(5)));
        return rtnStr;
    }

    public static String getDateFormatDash(Calendar cal) {
        String rtnStr = String.valueOf(cal.get(1));
        rtnStr = rtnStr + "-";
        rtnStr = rtnStr + (String.valueOf(cal.get(2) + 1).length() == 1 ? "0" + String.valueOf(cal.get(2) + 1) : String.valueOf(cal.get(2) + 1));
        rtnStr = rtnStr + "-";
        rtnStr = rtnStr + (String.valueOf(cal.get(5)).length() == 1 ? "0" + String.valueOf(cal.get(5)) : String.valueOf(cal.get(5)));
        return rtnStr;
    }

    public static String getDateFormatDash(String dt) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse(dt);
        return sdf2.format(date);
    }

    public static String getTimeFormatCol(String tm) {
        String tmStr = "";
        if (tm.length() == 4) {
            String var10000 = tm.substring(0, 2);
            tmStr = var10000 + ":" + tm.substring(2);
        } else {
            String var3 = tm.substring(0, 2);
            tmStr = var3 + ":" + tm.substring(2, 4) + ":" + tm.substring(4);
        }

        return tmStr;
    }

    public static boolean checkDate(String date) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
            Date result = formatter.parse(date);
            String resultStr = formatter.format(result);
            return resultStr.equalsIgnoreCase(date);
        } catch (Exception var4) {
            return false;
        }
    }
}
