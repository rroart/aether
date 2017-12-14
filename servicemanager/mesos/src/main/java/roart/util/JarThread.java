package roart.util;

public class JarThread implements Runnable {
    Object parameter;
    
    public JarThread(Object parameter) {
        this.parameter = parameter;
    }
    
    public void run() {
        String[] params = new String[2];
        params[0] = "-jar";
        params[1] = (String) parameter;
        RunUtil.execute("/usr/bin/java", params);
    }
}
