package roart.common.filesystem;

import java.util.Map;

import roart.common.inmemory.model.InmemoryMessage;
import roart.common.model.FileObject;

public class FileSystemMessageResult {
    public Map<FileObject, InmemoryMessage> message;
}
