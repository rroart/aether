package roart.util;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.ObjectMapper;

import roart.common.model.FileObject;
import roart.common.util.JsonData;
import roart.common.util.JsonUtil;
import roart.common.util.JsonUtil.JsonResponse;
import roart.queue.TraverseQueueElement;

public class JsonUtilTest {
    @Test
    public void test() throws Exception {
       TraverseQueueElement t = new TraverseQueueElement();
       t.setFileobject(new FileObject(null, ""));
       String s = JsonUtil.convert(t);
       JsonData<TraverseQueueElement> t00 = JsonUtil.convertxx(s);
       JsonData<TraverseQueueElement> t0 = JsonUtil.convertx(s);
       TraverseQueueElement t1 = JsonUtil.convertz(s);
       TraverseQueueElement t2 = JsonUtil.converty(s);
       System.out.println("" + t.getFileobject().toString());
       System.out.println("" + t2.getFileobject().toString());
              //TraverseQueueElement t2 = JsonUtil.convertJsonToPOJO(s, getClass())
    }
    
/*
    public Json<T> void deSerialize(Class<T> clazz, String string) {
    ObjectMapper mapper = new ObjectMapper();
       return mapper.readValue(string,
      mapper.getTypeFactory().constructParametricType(Json.class, clazz));
}
*/

}
