package roart.common.util;

import org.apache.commons.lang3.ArrayUtils;

import roart.common.constants.Constants;

public class JarThread implements Runnable {
    private Object parameter;
    
    private String[] args;
    
    private String[] parameters;
    
    private String lang;
    
    public JarThread(Object parameter, String[] args, String lang) {
        this.parameter = parameter;
        this.args = args;
        this.lang = lang;
    }
    
    public JarThread(Object parameter, String[] args, String[] params) {
        this.parameter = parameter;
        this.args = args;
        this.parameters = params;
    }
    
    @Override
    public void run() {
        int arglen = 0;
        int argptr = 0;
        if (args != null) {
            arglen = args.length;
        }
        int paramlen = 0;
        if (parameters != null) {
            paramlen = parameters.length;
        }
        int extraParams = 0;
        if (System.getenv(Constants.LOCALIP) != null) {
            extraParams += 1;
        }
        if (System.getenv(Constants.ZOO) != null) {
            extraParams += 1;
        }

        String[] params = new String[2 + arglen + paramlen + extraParams];
        if (args != null) {
            for (int i = 0; i < arglen; i++) {
                params[0 + i] = args[i];
            }
            argptr = arglen;
        }
        if (System.getenv(Constants.LOCALIP) != null) {
            params[argptr++] = "-DIP=" + System.getenv(Constants.LOCALIP);
        }
        if (System.getenv(Constants.ZOO) != null) {
            params[argptr++] = "-DZOO=" + System.getenv(Constants.ZOO);
        }
        params[argptr++] = "-jar";
        params[argptr++] = (String) parameter;
        if (parameters != null) {
            for (int i = 0; i < paramlen; i++) {
                params[argptr++] = parameters[i];
            }
        }
        RunUtil.execute("/usr/bin/java", params, lang);
    }
}
