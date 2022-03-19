package roart.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.common.constants.Constants;

public class ExecCommand {

    private static Logger log = LoggerFactory.getLogger(ExecCommand.class);

    private Semaphore outputSem;
    private String output;
    private Semaphore errorSem;
    private String error;
    private Process p;

    private class InputWriter extends Thread {
        private String input;

        public InputWriter(String input) {
            this.input = input;
        }

        @Override
        public void run() {
            PrintWriter pw = new PrintWriter(p.getOutputStream());
            pw.println(input);
            pw.flush();
        }
    }

    private class OutputReader extends Thread {
        public OutputReader() {
            try {
                outputSem = new Semaphore(1);
                outputSem.acquire();
            } catch (InterruptedException e) {
                log.error(Constants.EXCEPTION, e);
                Thread.currentThread().interrupt();
            }
        }

        @Override
        public void run() {
            try {
                StringBuilder readBuffer = new StringBuilder();
                BufferedReader isr = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String buff;
                while ((buff = isr.readLine()) != null) {
                    readBuffer.append(buff);
                    log.info(buff);
                }
                output = readBuffer.toString();
                outputSem.release();
            } catch (IOException e) {
                log.error(Constants.EXCEPTION, e);
            }
        }
    }

    private class ErrorReader extends Thread {
        public ErrorReader() {
            try {
                errorSem = new Semaphore(1);
                errorSem.acquire();
            } catch (InterruptedException e) {
                log.warn(Constants.EXCEPTION, e);
                Thread.currentThread().interrupt();
            }
        }

        @Override
        public void run() {
            try {
                StringBuilder readBuffer = new StringBuilder();
                BufferedReader isr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                String buff;
                while ((buff = isr.readLine()) != null) {
                    readBuffer.append(buff);
                }
                error = readBuffer.toString();
                errorSem.release();
            } catch (IOException e) {
                log.error(Constants.EXCEPTION, e);
            }
            if (error.length() > 0)
                log.warn(error);
        }
    }

    public String getOutput() {
        try {
            outputSem.acquire();
        } catch (InterruptedException e) {
            log.error(Constants.EXCEPTION, e);
            Thread.currentThread().interrupt();
        }
        String value = output;
        outputSem.release();
        return value;
    }

    public String getError() {
        try {
            errorSem.acquire();
        } catch (InterruptedException e) {
            log.error(Constants.EXCEPTION, e);
            Thread.currentThread().interrupt();
        }
        String value = error;
        errorSem.release();
        return value;
    }

    public String execute(String filename, String[] arg, long[] pid, String lang) {
        String res = null;
        Process proc = null;
        try {
            String[] cmdarray = new String[arg.length + 1];
            cmdarray[0] = filename;
            for (int i = 0; i < arg.length; i++) {
                cmdarray[i + 1] = arg[i];
            }
            String[] envarray = new String[4];
            envarray[0] = "CALIBRE_WORKER_TEMP_DIR=/tmp";
            envarray[1] = "CALIBRE_TEMP_DIR=/tmp";
            if (lang == null) {
                lang = "en_US.UTF-8";
            }
            envarray[2] = "LANG=" + lang;
            envarray[3] = "APPID=" + (System.getenv(Constants.APPID) != null ? System.getenv(Constants.APPID) : "");
            proc = Runtime.getRuntime().exec(cmdarray, envarray);
            p = proc;
            if (pid != null) {
                pid[0] = p.pid();
            }
            log.info("proc {} {} {}", filename, arg[0], proc);
            OutputReader or = null;
            ErrorReader er = null;
            if (proc != null) {
                or = new OutputReader();
                or.start();
                er = new ErrorReader();
                er.start();
                proc.waitFor();
            }
        } catch (Exception e) {
            log.info(Constants.EXCEPTION + e);
            log.error(Constants.EXCEPTION, e);
        }
        return res;
    }

}