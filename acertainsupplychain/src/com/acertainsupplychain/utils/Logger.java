package com.acertainsupplychain.utils;

import java.util.Properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.BufferedWriter;

/**
 * The logger class is used to log actions from the OrderManager and ItemSuppliers.
 */
public class Logger {

    private final BufferedWriter out;

    public Logger(String filename) throws LogException {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(SupplyChainConstants.PROPERTIES_PATH));
        } catch (Exception e) {
            throw new LogException(e);

        }

        String dir = props.getProperty("logdir");

        // Create log directory if it doesn't already exist.
        File file = new File(dir);
        file.mkdir();

        // Purge the log file
        file = new File(dir + File.separator + filename);
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else {
                throw new LogException(new Exception("Log file is a directory!"));
            }
        }

        // Open the writer
        try {
            FileWriter fstream = new FileWriter(file, true);
            this.out = new BufferedWriter(fstream);
        } catch (Exception e) {
            throw new LogException(e);
        }
    }

    public void log(Object obj) throws LogException {
        try {
            out.write(SupplyChainUtility.serializeObject(obj) + "\n");
            out.flush();
        } catch (Exception e) {
            throw new LogException(e);
        }
    }

    public void close() throws LogException {
        try {
            out.close();
        } catch (Exception e) {
            throw new LogException(e);
        }
    }
}
