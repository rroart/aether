package roart.common.util;

public class JarThread implements Runnable {
    private Object parameter;
    
    private String[] args;
    
    public JarThread(Object parameter, String[] args) {
        this.parameter = parameter;
        this.args = args;
    }
    
    @Override
    public void run() {
        int arglen = 0;
        if (args != null) {
            arglen = args.length;
        }
        String[] params = new String[2 + arglen];
        if (args != null) {
            for (int i = 0; i < arglen; i++) {
                params[0 + i] = args[i];
            }
        }
        params[0 + arglen] = "-jar";
        params[1 + arglen] = (String) parameter;
        RunUtil.execute("/usr/bin/java", params);
    }
}
