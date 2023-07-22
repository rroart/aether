package roart.common.collections.impl;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import roart.common.collections.MyQueue;

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
    
    @Override
    public void clear() {
        queue.clear();
    }

    @Override
    public T poll(Class<T> clazz) {
        return poll();
    }
}
