package roart.convert;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.any;

public class ConvertUtilTest {

    @Test
    public void test() {
        String result = ConvertUtil.executeTimeout("/bin/sleep", new String[] { "3600" }, null, new String[1], 15);
        System.out.println(result);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
        }
        
    }
}
