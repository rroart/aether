package roart.common.service;

import java.util.List;
import java.util.Map;

import roart.common.config.NodeConfig;
import roart.common.model.ResultItem;

public class ServiceResult {
public NodeConfig config;
public List<List<ResultItem>> list;
public String error;
public String uuid;
}

