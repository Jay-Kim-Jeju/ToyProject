package toy.com.egov;

import org.apache.log4j.Logger;
import toy.com.util.EgovWebUtil;

import java.io.*;
import java.net.URL;
import java.util.*;

public class EgovPropertiesUtils {
    public static final String ERR_CD = " EXCEPTION OCCURRED";
    public static final String ERR_CD_FNFE = " EXCEPTION(FNFE) OCCURRED";
    public static final String ERR_CD_IOE = " EXCEPTION(IOE) OCCURRED";
    static final char FILE_SEPARATOR;
    public static final String RELATIVE_PATH_PREFIX;
    public static final String RELATIVE_OPTIONAL_PATH_PREFIX;
    public static final String GLOBALS_PROPERTIES_FILE;
    public static final String OPTIONAL_PROPERTIES_FILE;

    public static String getPathProperty(String keyName) {
        String value = " EXCEPTION OCCURRED";
        value = "99";
        FileInputStream fis = null;

        try {
            Properties props = new Properties();
            fis = new FileInputStream(EgovWebUtil.filePathBlackList(GLOBALS_PROPERTIES_FILE));
            props.load(new BufferedInputStream(fis));
            value = props.getProperty(keyName).trim();
            String var10000 = RELATIVE_PATH_PREFIX;
            value = var10000 + "egovProps" + System.getProperty("file.separator") + value;
        } catch (FileNotFoundException fne) {
            Logger.getLogger(EgovPropertiesUtils.class).debug(fne);
        } catch (IOException ioe) {
            Logger.getLogger(EgovPropertiesUtils.class).debug(ioe);
        } catch (Exception e) {
            Logger.getLogger(EgovPropertiesUtils.class).debug(e);
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(EgovPropertiesUtils.class).debug("IGNORED: " + ex.getMessage());
            }

        }

        return value;
    }

    public static String getProperty(String keyName) {
        String value = " EXCEPTION OCCURRED";
        value = "99";
        FileInputStream fis = null;

        try {
            Properties props = new Properties();
            fis = new FileInputStream(EgovWebUtil.filePathBlackList(GLOBALS_PROPERTIES_FILE));
            props.load(new BufferedInputStream(fis));
            value = props.getProperty(keyName).trim();
        } catch (FileNotFoundException fne) {
            Logger.getLogger(EgovPropertiesUtils.class).debug(fne);
        } catch (IOException ioe) {
            Logger.getLogger(EgovPropertiesUtils.class).debug(ioe);
        } catch (Exception e) {
            Logger.getLogger(EgovPropertiesUtils.class).debug(e);
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(EgovPropertiesUtils.class).debug("IGNORED: " + ex.getMessage());
            }

        }

        return value;
    }

    public static String getPathProperty(String fileName, String key) {
        FileInputStream fis = null;

        String value;
        try {
            Properties props = new Properties();
            fis = new FileInputStream(EgovWebUtil.filePathBlackList(fileName));
            props.load(new BufferedInputStream(fis));
            fis.close();
            value = props.getProperty(key);
            String var10000 = RELATIVE_PATH_PREFIX;
            value = var10000 + "egovProps" + System.getProperty("file.separator") + value;
            String var5 = value;
            return var5;
        } catch (FileNotFoundException var17) {
            value = " EXCEPTION(FNFE) OCCURRED";
        } catch (IOException var18) {
            value = " EXCEPTION(IOE) OCCURRED";
            return value;
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(EgovPropertiesUtils.class).debug(ex);
            }

        }

        return value;
    }

    public static String getProperty(String fileName, String key) {
        FileInputStream fis = null;

        String value;
        try {
            Properties props = new Properties();
            fis = new FileInputStream(EgovWebUtil.filePathBlackList(fileName));
            props.load(new BufferedInputStream(fis));
            fis.close();
            value = props.getProperty(key);
            String var5 = value;
            return var5;
        } catch (FileNotFoundException var17) {
            value = " EXCEPTION(FNFE) OCCURRED";
        } catch (IOException var18) {
            value = " EXCEPTION(IOE) OCCURRED";
            return value;
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(EgovPropertiesUtils.class).debug("IGNORED: " + ex.getMessage());
            }

        }

        return value;
    }

    public static List<Map<String, String>> loadPropertyFile(String property) {
        List<Map<String, String>> keyList = new ArrayList<>();
        String src = property.replace('\\', FILE_SEPARATOR).replace('/', FILE_SEPARATOR);
        FileInputStream fis = null;

        try {
            File srcFile = new File(EgovWebUtil.filePathBlackList(src));
            if (srcFile.exists()) {
                Properties props = new Properties();
                fis = new FileInputStream(src);
                props.load(new BufferedInputStream(fis));

                for (String key : props.stringPropertyNames()) {
                    Map<String, String> map = new HashMap<>();
                    map.put(key, props.getProperty(key));
                    keyList.add(map);
                }
            }
        } catch (IOException ex) {
            debug(ex);
        } finally {
            if (fis != null) {
                try { fis.close(); } catch (IOException ex) {
                    Logger.getLogger(EgovPropertiesUtils.class).debug("IGNORED: " + ex.getMessage());
                }
            }
        }
        return keyList;
    }


    private static void debug(Object obj) {
        if (obj instanceof Exception) {
            Logger.getLogger(EgovPropertiesUtils.class).debug("IGNORED: " + ((Exception)obj).getMessage());
        }

    }

    public static String getOptionalProp(String keyName) {
        String value = " EXCEPTION OCCURRED";
        value = "99";
        Logger.getLogger(EgovPropertiesUtils.class).debug(OPTIONAL_PROPERTIES_FILE + " : " + keyName);
        FileInputStream fis = null;

        try {
            Properties props = new Properties();
            fis = new FileInputStream(EgovWebUtil.filePathBlackList(OPTIONAL_PROPERTIES_FILE));
            props.load(new BufferedInputStream(fis));
            value = props.getProperty(keyName).trim();
        } catch (FileNotFoundException fne) {
            Logger.getLogger(EgovPropertiesUtils.class).debug(fne);
        } catch (IOException ioe) {
            Logger.getLogger(EgovPropertiesUtils.class).debug(ioe);
        } catch (Exception e) {
            Logger.getLogger(EgovPropertiesUtils.class).debug(e);
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception ex) {
                Logger.getLogger(EgovPropertiesUtils.class).debug("IGNORED: " + ex.getMessage());
            }

        }

        return value;
    }

    static {
        FILE_SEPARATOR = File.separatorChar;

        // *** 여기부터가 문제였던 부분 ***
        URL url = EgovPropertiesUtils.class.getResource("");
        String path = url != null ? url.getPath() : "";

        //  "toy" 로 변경
        int idx = path.lastIndexOf("toy");
        if (idx < 0) {
            // 안전장치: 못 찾으면 그냥 현재 경로 기준으로 처리
            idx = path.length();
        }

        String root = path.substring(0, idx);

        // 예: /.../WEB-INF/classes/egovframework/egovProps/
        RELATIVE_PATH_PREFIX = root + "egovframework" + FILE_SEPARATOR + "egovProps";
        RELATIVE_OPTIONAL_PATH_PREFIX = root;

        GLOBALS_PROPERTIES_FILE = RELATIVE_PATH_PREFIX + FILE_SEPARATOR + "globals.properties";
        OPTIONAL_PROPERTIES_FILE = RELATIVE_OPTIONAL_PATH_PREFIX + FILE_SEPARATOR + "prop.properties";
    }
}