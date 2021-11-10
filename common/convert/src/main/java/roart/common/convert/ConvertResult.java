package roart.common.convert;

import java.util.Map;

import roart.common.inmemory.model.InmemoryMessage;

public class ConvertResult {
    public InmemoryMessage message;
    public Map<String, String> metadata;
    public String error;
}
