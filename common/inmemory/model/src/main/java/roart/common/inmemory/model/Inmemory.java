package roart.common.inmemory.model;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import roart.common.constants.Constants;
import roart.common.util.JsonUtil;

public abstract class Inmemory {
    
    private static final Logger log = LoggerFactory.getLogger(Inmemory.class);

    protected int getLimit() {
        return 1024 * 1024 * 1024;
    }
    
    protected abstract String getServer();
    
    public Inmemory(String server) {
            
    }
    
    public void t() {
        
    }
    
    public InmemoryMessage send(String id, Object data) {
        return send(id, data, null);
    }
    
    public InmemoryMessage send(String id, Object data, String md5) {
        InputStream inputStream;
        if (data instanceof InputStream) {
            inputStream = (InputStream) data;
        } else {
            inputStream = getInputStream(data);
        }
        try {
            int limit = getLimit();
            int count = 0;
            boolean doRead = true;
            while (doRead) {
                byte[] bytes = inputStream.readNBytes(limit);
                if (bytes.length > 0) {
                    InmemoryMessage messageKey = new InmemoryMessage(getServer(), id, count, md5);
                    String messageKeyString = JsonUtil.convert(messageKey);
                    String value = InmemoryUtil.convertWithCharset(bytes);
                    set(messageKeyString, value);               
                }
                doRead = bytes.length == limit || bytes.length == 0;
                count++;
            }
            inputStream.close();
            InmemoryMessage message = new InmemoryMessage(getServer(), id, count, md5);
            return message;
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
            return null;
        }
    }

    private InputStream getInputStream(Object data) {
        String string;
        if (data instanceof String) {
            string = (String) data;
        } else {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            string = JsonUtil.convert(data, mapper);
        }
        if (string == null) {
            string = "";
        }
        return new ByteArrayInputStream(string.getBytes());
    }

    public String read(InmemoryMessage m) {
        StringBuilder stringBuilder = new StringBuilder("");
        int count = m.getCount();
        for (int i = 0; i < count; i++) {
            InmemoryMessage messkageKey = new InmemoryMessage(m.getServer(), m.getId(), i, m.getMd5());
            String messageKeyString = JsonUtil.convert(messkageKey);
            String string = get(messageKeyString);
            stringBuilder.append(string);
        }        
        return stringBuilder.toString();
    }
    
    public InputStream getInputStream(InmemoryMessage m) {
        int count = m.getCount();
        InputStream inputStream = null;
        for (int i = 0; i < count; i++) {
            InmemoryMessage messkageKey = new InmemoryMessage(m.getServer(), m.getId(), i, m.getMd5());
            String messageKeyString = JsonUtil.convert(messkageKey);
            String string = get(messageKeyString);
            InputStream anInputStream = new ByteArrayInputStream(InmemoryUtil.convertWithCharset(string));
            if (i == 0) {
                inputStream = anInputStream;
            } else {
                inputStream = new SequenceInputStream(inputStream, anInputStream);
            }
        }        
        return inputStream;
    }
    
    public void delete(InmemoryMessage m) {
        StringBuilder stringBuilder = new StringBuilder("");
        int count = m.getCount();
        for (int i = 0; i < count; i++) {
            InmemoryMessage messkageKey = new InmemoryMessage(m.getServer(), m.getId(), i, m.getMd5());
            String messageKeyString = JsonUtil.convert(messkageKey);
            del(messageKeyString);
        }        
    }
    
    protected abstract void set(String key, String value);
    
    protected abstract String get(String key);
    
    protected abstract void del(String key);
}
