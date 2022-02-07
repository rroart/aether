package roart.function;

import java.util.List;

import roart.common.service.ServiceParam;

public class Filesystem extends AbstractFunction {

    public Filesystem(ServiceParam param) {
        super(param);
    }

    @Override
    public List doClient(ServiceParam param) {
        return clientDo(param);
    }

}
