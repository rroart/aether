package roart.common.collections;

/**
 * 
 * @author roart
 *
 * An abstract class for misc Collection implementations, from native Java to Hazelcast
 *
 * @param <T>
 */

public abstract class MyCollection<T> {
    
    /**
     * 
     * Get the size of the collection
     * 
     * @return collection size
     */
    
    public abstract int size();
}
