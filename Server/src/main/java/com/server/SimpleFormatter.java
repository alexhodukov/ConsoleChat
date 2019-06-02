package com.server;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class SimpleFormatter extends Formatter {

    @Override
    public String format(LogRecord record) {
        return new Date(record.getMillis()) + " " +
        		record.getSourceClassName() + "." +
        		record.getSourceMethodName() + "()" +"\n" +
        		"INFO: " + record.getMessage()+"\n";
    }

}
