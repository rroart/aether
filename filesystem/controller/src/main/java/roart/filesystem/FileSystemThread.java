package roart.filesystem;

import java.net.InetAddress;

import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.constants.Constants;
import roart.common.constants.FileSystemConstants;
import roart.common.model.FileObject;
import roart.common.util.FsUtil;
import roart.common.zkutil.ZKUtil;

public class FileSystemThread implements Runnable {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private CuratorFramework curatorClient;

    private int port;

    private String confFs;

    public FileSystemThread(CuratorFramework curatorClient, int port, String confFs) {
        this.curatorClient = curatorClient;
        this.port = port;
        this.confFs = confFs;
    }

    @Override
    public void run() {
        String nodename = System.getProperty("NODE");
        String ip = System.getProperty("IP");
        String fs = System.getProperty("FS");
        String path = System.getProperty("PATH");
        log.info("Using {} {} {}", ip, fs, path);
        String[] paths = path.split(",");
        //int port = new MyListener().getPort();
        String address = null;
        try {
            address = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
        if (ip != null) {
            address = ip;
        }
        if (fs == null) {
            fs = confFs;
        }
        String whereami = address + ":" + port;
        System.out.println("Whereami " + whereami);
        log.info("Whereami {}", whereami);
        byte[] bytes = whereami.getBytes();
        for (String aPath : paths) {
            FileObject fo = FsUtil.getFileObject(aPath);
            if (fo.location.fs == null || fo.location.fs.isEmpty()) {
                fo.location.fs = FileSystemConstants.LOCALTYPE;
            }
            String str = ZKUtil.getAppidPath() + Constants.FS + stringOrNull(fo.location.nodename) + "/" + fo.location.fs + stringOrNull(fo.location.extra) + fo.object;
            if (str.endsWith("/")) {
                str = str.substring(0, str.length() - 1);
            }
            boolean success = false;
            while (!success) {
                try {
                    if (curatorClient.checkExists().forPath(str) != null) {
                        curatorClient.delete().deletingChildrenIfNeeded().forPath(str);
                    }
                    curatorClient.create().creatingParentsIfNeeded().forPath(str, bytes);
                    success = true;
                } catch (Exception e) {
                    log.error(Constants.EXCEPTION, e);
                }
                try {
                    Thread.sleep(10000);                        
                } catch (Exception e) {
                    log.error(Constants.EXCEPTION, e);
                }
            }
        }
        while (true) {
            try {
                Thread.sleep(10000);                        
            } catch (Exception e) {
                log.error(Constants.EXCEPTION, e);
            }
            for (String aPath : paths) {
                FileObject fo = FsUtil.getFileObject(aPath);
                if (fo.location.fs == null || fo.location.fs.isEmpty()) {
                    fo.location.fs = FileSystemConstants.LOCALTYPE;
                }
                String str = ZKUtil.getAppidPath() + Constants.FS + stringOrNull(fo.location.nodename) + "/" + fo.location.fs + stringOrNull(fo.location.extra) + fo.object;
                if (str.endsWith("/")) {
                    str = str.substring(0, str.length() - 1);
                }
                boolean success = false;
                while (!success) {
                    try {
                        curatorClient.create().creatingParentsIfNeeded().forPath(str, bytes);
                        curatorClient.setData().forPath(str, bytes);
                        success = true;
                        break;
                    } catch (Exception e) {
                        log.error(Constants.EXCEPTION, e);
                    }
                    try {
                        Thread.sleep(10000);                        
                    } catch (Exception e) {
                        log.error(Constants.EXCEPTION, e);
                    }
                }
            }
        }
    }

    private static String stringOrNull(String string) {
        if (string == null || string.isEmpty()) {
            return "";
        } else {
            return "/" + string;
        }
    }


}
