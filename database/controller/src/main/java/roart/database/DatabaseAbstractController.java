package roart.database;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.web.bind.annotation.*;

import roart.config.NodeConfig;
import roart.util.EurekaConstants;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
//@EnableAutoConfiguration
@SpringBootApplication
@EnableDiscoveryClient
public abstract class DatabaseAbstractController {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	private static Map<String, DatabaseOperations> operationMap = new HashMap();

	protected abstract DatabaseOperations createOperations(String nodename, NodeConfig nodeConf);

	private DatabaseOperations getOperation(String nodename, NodeConfig nodeConf) {
		DatabaseOperations operation = operationMap.get(nodename);
		if (operation == null) {
			operation = createOperations(nodename, nodeConf);
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
			DatabaseOperations operation = getOperation(param.nodename, param.conf);
		} catch (Exception e) {
			log.error(roart.util.Constants.EXCEPTION, e);
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
		DatabaseOperations operation = operationMap.remove(param.nodename);
		String error = null;
		if (operation != null) {
			try {
				operation.destroy();
			} catch (Exception e) {
				log.error(roart.util.Constants.EXCEPTION, e);
				error = e.getMessage();
			}
		} else {
			error = "did not exist";
		}
		DatabaseConstructorResult result = new DatabaseConstructorResult();
		result.error = error;
		return result;
	}

	@RequestMapping(value = "/" + EurekaConstants.GETFILELOCATIONSBYMD5,
			method = RequestMethod.POST)
	public DatabaseResult processGetFilelocationsByMd5(@RequestBody DatabaseMd5Param param)
			throws Exception {
		DatabaseOperations operation = getOperation(param.nodename, param.conf);
		DatabaseFileLocationResult ret = operation.getFilelocationsByMd5(param);
		return ret;
	}

    @RequestMapping(value = "/" + EurekaConstants.GETBYFILELOCATION,
            method = RequestMethod.POST)
    public DatabaseIndexFilesResult processGetByFilelocation(@RequestBody DatabaseFileLocationParam param)
            throws Exception {
        DatabaseOperations operation = getOperation(param.nodename, param.conf);
        DatabaseIndexFilesResult ret = operation.getByFilelocation(param);
        return ret;
    }

    @RequestMapping(value = "/" + EurekaConstants.GETBYMD5,
            method = RequestMethod.POST)
    public DatabaseIndexFilesResult processGetByMd5(@RequestBody DatabaseMd5Param param)
            throws Exception {
        DatabaseOperations operation = getOperation(param.nodename, param.conf);
        DatabaseIndexFilesResult ret = operation.getByMd5(param);
        return ret;
    }

	@RequestMapping(value = "/" + EurekaConstants.GETMD5BYFILELOCATION,
			method = RequestMethod.POST)
	public DatabaseMd5Result processGetMd5ByFilelocation(@RequestBody DatabaseFileLocationParam param)
			throws Exception {
		DatabaseOperations operation = getOperation(param.nodename, param.conf);
		DatabaseMd5Result ret = operation.getMd5ByFilelocation(param);
		return ret;
	}

	@RequestMapping(value = "/" + EurekaConstants.GETALL,
			method = RequestMethod.POST)
	public DatabaseIndexFilesResult processGetAll(@RequestBody DatabaseParam param)
			throws Exception {
		DatabaseOperations operation = getOperation(param.nodename, param.conf);
		DatabaseIndexFilesResult ret = operation.getAll(param);
		return ret;
	}

    @RequestMapping(value = "/" + EurekaConstants.SAVE,
            method = RequestMethod.POST)
    public DatabaseResult processSave(@RequestBody DatabaseIndexFilesParam param)
            throws Exception {
        DatabaseOperations operation = getOperation(param.nodename, param.conf);
        DatabaseResult ret = operation.save(param);
        return ret;
    }

    @RequestMapping(value = "/" + EurekaConstants.FLUSH,
            method = RequestMethod.POST)
    public DatabaseResult processFlush(@RequestBody DatabaseParam param)
            throws Exception {
        DatabaseOperations operation = getOperation(param.nodename, param.conf);
        DatabaseResult ret = operation.flush(param);
        return ret;
    }

    @RequestMapping(value = "/" + EurekaConstants.COMMIT,
            method = RequestMethod.POST)
    public DatabaseResult processCommit(@RequestBody DatabaseParam param)
            throws Exception {
        DatabaseOperations operation = getOperation(param.nodename, param.conf);
        DatabaseResult ret = operation.commit(param);
        return ret;
    }

    @RequestMapping(value = "/" + EurekaConstants.CLOSE,
            method = RequestMethod.POST)
    public DatabaseResult processClose(@RequestBody DatabaseParam param)
            throws Exception {
        DatabaseOperations operation = getOperation(param.nodename, param.conf);
        DatabaseResult ret = operation.close(param);
        return ret;
    }

    @RequestMapping(value = "/" + EurekaConstants.GETALLMD5,
            method = RequestMethod.POST)
    public DatabaseMd5Result processGetAllMd5(@RequestBody DatabaseFileLocationParam param)
            throws Exception {
        DatabaseOperations operation = getOperation(param.nodename, param.conf);
        DatabaseMd5Result ret = operation.getAllMd5(param);
        return ret;
    }

    @RequestMapping(value = "/" + EurekaConstants.GETLANGUAGES,
            method = RequestMethod.POST)
    public DatabaseLanguagesResult processGetLanguages(@RequestBody DatabaseFileLocationParam param)
            throws Exception {
        DatabaseOperations operation = getOperation(param.nodename, param.conf);
        DatabaseLanguagesResult ret = operation.getLanguages(param);
        return ret;
    }

    @RequestMapping(value = "/" + EurekaConstants.DELETE,
            method = RequestMethod.POST)
    public DatabaseResult processDelete(@RequestBody DatabaseIndexFilesParam param)
            throws Exception {
        DatabaseOperations operation = getOperation(param.nodename, param.conf);
        DatabaseResult ret = operation.delete(param);
        return ret;
    }

	public static void main(String[] args) throws Exception {
		SpringApplication.run(DatabaseAbstractController.class, args);
	}
}
