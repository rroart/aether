package roart.common.util;

public class JarThread implements Runnable {
    private Object parameter;
    
    private String[] args;
    
    private String[] parameters;
    
    public JarThread(Object parameter, String[] args) {
        this.parameter = parameter;
        this.args = args;
    }
    
    public JarThread(Object parameter, String[] args, String[] params) {
        this.parameter = parameter;
        this.args = args;
        this.parameters = params;
    }
    
    @Override
    public void run() {
        int arglen = 0;
        if (args != null) {
            arglen = args.length;
        }
        int paramlen = 0;
        if (parameters != null) {
            paramlen = parameters.length;
        }
        String[] params = new String[2 + arglen + paramlen];
        if (args != null) {
            for (int i = 0; i < arglen; i++) {
                params[0 + i] = args[i];
            }
        }
        params[0 + arglen] = "-jar";
        params[1 + arglen] = (String) parameter;
        if (parameters != null) {
            for (int i = 0; i < paramlen; i++) {
                params[2 + arglen + i] = parameters[i];
            }
        }
        RunUtil.execute("/usr/bin/java", params);
    }
}
