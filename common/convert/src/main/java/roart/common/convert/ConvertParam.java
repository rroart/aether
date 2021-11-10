package roart.common.convert;

import roart.common.config.Converter;
import roart.common.config.NodeConfig;
import roart.common.inmemory.model.InmemoryMessage;

public class ConvertParam {

    public String nodename;
    public InmemoryMessage message;
    public NodeConfig conf;
    public Converter converter;
}
