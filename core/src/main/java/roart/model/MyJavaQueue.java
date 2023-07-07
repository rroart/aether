package roart.model;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MyJavaQueue<T> extends MyQueue<T> {
    private Queue<T> queue;

    @Override
    public void offer(T o) {
        queue.add(o);
    }
    
    public MyJavaQueue() {
        queue = new ConcurrentLinkedQueue<T>();
    }

    @Override
    public T poll() {
        return (T) queue.poll();
    }
    
    @Override
    public int size() {
        return queue.size();
    }
}
