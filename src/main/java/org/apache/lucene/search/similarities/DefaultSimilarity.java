package org.apache.lucene.search.similarities;

public class DefaultSimilarity {
    ClassicSimilarity sim;
    DefaultSimilarity() {
        sim = new ClassicSimilarity();
    }
    
    float tf(float freq) {
        return sim.tf(freq);
    }
}
