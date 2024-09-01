package roart.thread;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import roart.common.filesystem.MyFile;

public class ListQueueRunnerTest {

    @Test
    public void test() throws Exception {
        List<MyFile> list = new ArrayList<>();
        Collection<MyFile> c = list;
        for (int i = 0; i < 10; i++) {
            MyFile file = new MyFile();
            file.ctime = i;
            list.add(file);
        }
        list = (List<MyFile>) ListQueueRunner.shuffle(c);
        for (int i = 0; i < 10; i++) {
            System.out.println(list.get(i).ctime);
        }
        
    }
}
