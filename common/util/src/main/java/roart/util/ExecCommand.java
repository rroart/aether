package roart.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public String execute(String filename, String[] arg) {
        String res = null;
        Process proc = null;
        try {
            String[] cmdarray = new String[3];
            cmdarray[0] = filename;
            cmdarray[1] = arg[0];
            cmdarray[2] = arg[1];
            String[] envarray = new String[2];
            envarray[0] = "CALIBRE_WORKER_TEMP_DIR=/tmp";
            envarray[1] = "CALIBRE_TEMP_DIR=/tmp";
            proc = Runtime.getRuntime().exec(cmdarray, envarray);
            p = proc;
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