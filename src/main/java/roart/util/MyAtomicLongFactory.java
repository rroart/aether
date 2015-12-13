package roart.util;

public class MyAtomicLongFactory extends MyFactory {

    public MyAtomicLong create(String listid) {
        if (roart.service.ControlService.distributedtraverse) {
            return new MyHazelcastAtomicLong(listid);
        } else {
            return new MyJavaAtomicLong();
        }
    }
}
