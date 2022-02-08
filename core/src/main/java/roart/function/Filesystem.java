package roart.function;

import java.util.List;

import roart.common.constants.Constants;
import roart.common.service.ServiceParam;
import roart.dir.Traverse;

public class Filesystem extends AbstractFunction {

    public Filesystem(ServiceParam param) {
        super(param);
    }

    @Override
    public List doClient(ServiceParam param) {
        return clientDo(param);
    }

    @Override
    protected void traverse(String filename, Traverse traverse) {
        try {
            traverse.traverse(filename, this);
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
    }
}
