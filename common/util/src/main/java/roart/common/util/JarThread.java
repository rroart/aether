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
        params[0] = "-jar";
        params[1] = (String) parameter;
        if (args != null) {
            for (int i = 0; i < arglen; i++) {
                params[2 + i] = args[i];
            }
        }
        RunUtil.execute("/usr/bin/java", params);
    }
}
