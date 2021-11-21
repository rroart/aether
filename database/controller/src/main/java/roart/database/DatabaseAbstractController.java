package roart.database;

import java.util.HashMap;
import java.util.Map;

import roart.common.config.NodeConfig;
import roart.common.constants.EurekaConstants;
import roart.common.database.DatabaseConstructorParam;
import roart.common.database.DatabaseConstructorResult;
import roart.common.database.DatabaseFileLocationParam;
import roart.common.database.DatabaseIndexFilesParam;
import roart.common.database.DatabaseIndexFilesResult;
import roart.common.database.DatabaseLanguagesResult;
import roart.common.database.DatabaseMd5Param;
import roart.common.database.DatabaseMd5Result;
import roart.common.database.DatabaseParam;
import roart.common.database.DatabaseResult;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@SpringBootApplication
@EnableDiscoveryClient
public abstract class DatabaseAbstractController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private static Map<String, DatabaseOperations> operationMap = new HashMap<>();

    protected abstract DatabaseOperations createOperations(String nodename, NodeConfig nodeConf);

    private DatabaseOperations getOperation(String nodename, String configid, NodeConfig nodeConf) {
        DatabaseOperations operation = operationMap.get(configid);
        if (operation == null) {
            operation = createOperations(configid, nodeConf);
            operationMap.put(nodename, operation);
        }
        return operation;
    }

    @RequestMapping(value = "/" + EurekaConstants.CONSTRUCTOR,
            method = RequestMethod.POST)
    public DatabaseConstructorResult processConstructor(@RequestBody DatabaseConstructorParam param)
            throws Exception {
        String error = null;
        try {
            DatabaseOperations operation = getOperation(param.getNodename(), param.getConfigid(), param.getConf());
        } catch (Exception e) {
            log.error(roart.common.constants.Constants.EXCEPTION, e);
            error = e.getMessage();
        }
        DatabaseConstructorResult result = new DatabaseConstructorResult();
        result.error = error;
        return result;
    }

    @RequestMapping(value = "/" + EurekaConstants.DESTRUCTOR,
            method = RequestMethod.POST)
    public DatabaseConstructorResult processDestructor(@RequestBody DatabaseConstructorParam param)
            throws Exception {
        DatabaseOperations operation = operationMap.remove(param.getNodename());
        String error = null;
        if (operation != null) {
            try {
                operation.destroy();
            } catch (Exception e) {
                log.error(roart.common.constants.Constants.EXCEPTION, e);
                error = e.getMessage();
            }
        } else {
            error = "did not exist";
        }
        DatabaseConstructorResult result = new DatabaseConstructorResult();
        result.error = error;
        return result;
    }

    @RequestMapping(value = "/" + EurekaConstants.CLEAR,
            method = RequestMethod.POST)
    public DatabaseConstructorResult processClear(@RequestBody DatabaseConstructorParam param)
            throws Exception {
        DatabaseOperations operation = getOperation(param.getNodename(), param.getConfigid(), param.getConf());
        return operation.clear(param);
    }

    @RequestMapping(value = "/" + EurekaConstants.DROP,
            method = RequestMethod.POST)
    public DatabaseConstructorResult processDrop(@RequestBody DatabaseConstructorParam param)
            throws Exception {
        DatabaseOperations operation = getOperation(param.getNodename(), param.getConfigid(), param.getConf());
        return operation.drop(param);
    }

    @RequestMapping(value = "/" + EurekaConstants.GETFILELOCATIONSBYMD5,
            method = RequestMethod.POST)
    public DatabaseResult processGetFilelocationsByMd5(@RequestBody DatabaseMd5Param param)
            throws Exception {
        DatabaseOperations operation = getOperation(param.getNodename(), param.getConfigid(), param.getConf());
        return operation.getFilelocationsByMd5(param);
     }

    @RequestMapping(value = "/" + EurekaConstants.GETBYFILELOCATION,
            method = RequestMethod.POST)
    public DatabaseIndexFilesResult processGetByFilelocation(@RequestBody DatabaseFileLocationParam param)
            throws Exception {
        DatabaseOperations operation = getOperation(param.getNodename(), param.getConfigid(), param.getConf());
        return operation.getByFilelocation(param);
    }

    @RequestMapping(value = "/" + EurekaConstants.GETBYMD5,
            method = RequestMethod.POST)
    public DatabaseIndexFilesResult processGetByMd5(@RequestBody DatabaseMd5Param param)
            throws Exception {
        DatabaseOperations operation = getOperation(param.getNodename(), param.getConfigid(), param.getConf());
        return operation.getByMd5(param);
    }

    @RequestMapping(value = "/" + EurekaConstants.GETMD5BYFILELOCATION,
            method = RequestMethod.POST)
    public DatabaseMd5Result processGetMd5ByFilelocation(@RequestBody DatabaseFileLocationParam param)
            throws Exception {
        DatabaseOperations operation = getOperation(param.getNodename(), param.getConfigid(), param.getConf());
        return operation.getMd5ByFilelocation(param);
    }

    @RequestMapping(value = "/" + EurekaConstants.GETALL,
            method = RequestMethod.POST)
    public DatabaseIndexFilesResult processGetAll(@RequestBody DatabaseParam param)
            throws Exception {
        DatabaseOperations operation = getOperation(param.getNodename(), param.getConfigid(), param.getConf());
        return operation.getAll(param);
    }

    @RequestMapping(value = "/" + EurekaConstants.SAVE,
            method = RequestMethod.POST)
    public DatabaseResult processSave(@RequestBody DatabaseIndexFilesParam param)
            throws Exception {
        DatabaseOperations operation = getOperation(param.getNodename(), param.getConfigid(), param.getConf());
        return operation.save(param);
    }

    @RequestMapping(value = "/" + EurekaConstants.FLUSH,
            method = RequestMethod.POST)
    public DatabaseResult processFlush(@RequestBody DatabaseParam param)
            throws Exception {
        DatabaseOperations operation = getOperation(param.getNodename(), param.getConfigid(), param.getConf());
        return operation.flush(param);
    }

    @RequestMapping(value = "/" + EurekaConstants.COMMIT,
            method = RequestMethod.POST)
    public DatabaseResult processCommit(@RequestBody DatabaseParam param)
            throws Exception {
        DatabaseOperations operation = getOperation(param.getNodename(), param.getConfigid(), param.getConf());
        return operation.commit(param);
    }

    @RequestMapping(value = "/" + EurekaConstants.CLOSE,
            method = RequestMethod.POST)
    public DatabaseResult processClose(@RequestBody DatabaseParam param)
            throws Exception {
        DatabaseOperations operation = getOperation(param.getNodename(), param.getConfigid(), param.getConf());
        return operation.close(param);
    }

    @RequestMapping(value = "/" + EurekaConstants.GETALLMD5,
            method = RequestMethod.POST)
    public DatabaseMd5Result processGetAllMd5(@RequestBody DatabaseFileLocationParam param)
            throws Exception {
        DatabaseOperations operation = getOperation(param.getNodename(), param.getConfigid(), param.getConf());
        return operation.getAllMd5(param);
    }

    @RequestMapping(value = "/" + EurekaConstants.GETLANGUAGES,
            method = RequestMethod.POST)
    public DatabaseLanguagesResult processGetLanguages(@RequestBody DatabaseFileLocationParam param)
            throws Exception {
        DatabaseOperations operation = getOperation(param.getNodename(), param.getConfigid(), param.getConf());
        return operation.getLanguages(param);
    }

    @RequestMapping(value = "/" + EurekaConstants.DELETE,
            method = RequestMethod.POST)
    public DatabaseResult processDelete(@RequestBody DatabaseIndexFilesParam param)
            throws Exception {
        DatabaseOperations operation = getOperation(param.getNodename(), param.getConfigid(), param.getConf());
        return operation.delete(param);
    }

    public static void main(String[] args) {
        SpringApplication.run(DatabaseAbstractController.class, args);
    }
}
