package roart;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import roart.classification.spark.ml.SparkMLController;
import roart.classification.spark.ml.SparkMLClassify;

@SpringBootTest(classes = SparkMLController.class)
public class SparkIT {

    //@Autowired
    //public IclijConfig iclijConfig;    
    
    @Test
    public void test() {
        try {
            SparkMLClassify a = new SparkMLClassify(null, null, null);
            System.out.println("here");
        //a.clean();
        } catch (Exception e) {
            e.printStackTrace();
            }
    }
}
