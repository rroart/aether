package roart.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.util.Constants;
import roart.util.MyQueue;
import roart.util.MyQueues;
import roart.dir.Traverse;
import roart.dir.TraverseFile;
import roart.filesystem.FileSystemDao;
import roart.model.FileObject;
import roart.queue.Queues;
import roart.queue.TraverseQueueElement;

public class TraverseQueueRunner implements Runnable {
	
    static Logger log = LoggerFactory.getLogger(TraverseQueueRunner.class);

    private static final java.util.Queue<Object[]> execQueue = new ConcurrentLinkedQueue<Object[]>();
   
    public void run() {

        int nThreads = 10;
        ThreadPoolExecutor /*ExecutorService*/ executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads);

        String queueid = Constants.TRAVERSEQUEUE;
        MyQueue<TraverseQueueElement> queue = MyQueues.get(queueid);

    	while (true) {
    	    TraverseQueueElement trav = queue.poll();
    		if (trav == null) {
    		    try {
                    TimeUnit.SECONDS.sleep(1);
                    continue;
                } catch (InterruptedException e) {
                    log.error(Constants.EXCEPTION, e); 
                }    		    
    		}
    	    
    		String filename = trav.getFilename();
    		log.info("trav cli " + filename);
            FileObject fo = FileSystemDao.get(filename);
    		
    		if (Traverse.isLocal(fo)) {
    		    try {
                    if (Queues.traverseQueueHeavyLoaded()) {
                                                    try {
                                                            TimeUnit.SECONDS.sleep(1);
                                                    } catch (InterruptedException e) {
                                                            // TODO Auto-generated catch block              
                                                            log.error(Constants.EXCEPTION, e);
                                                    }
                                                    continue;
                                            }
                   Callable<Object> callable = new Callable<Object>() {
                        public Object call() /* throws Exception*/ {
                                try {
                                    Queues.traverseQueue.add(trav);
                                        doHandleFo();
                                } catch (Exception e) {
                                    log.error(Constants.EXCEPTION, e);
                                } catch (Error e) {
                                    System.gc();
                                log.error("Error " + Thread.currentThread().getId());
                                    log.error(Constants.ERROR, e);
                                }
                                finally {
                                        //log.info("myend");            
                                }
                                return null; //myMethod();              
                        }
                };
                   Callable<Object> callablesimple = new Callable<Object>() {
                        public Object call() /* throws Exception*/ {
                                try {
				    TraverseFile.handleFo3(trav);
                                } catch (Exception e) {
                                    log.error(Constants.EXCEPTION, e);
                                } catch (Error e) {
                                    System.gc();
                                log.error("Error " + Thread.currentThread().getId());
                                    log.error(Constants.ERROR, e);
                                }
                                finally {
                                        //log.info("myend");            
                                }
                                return null; //myMethod();              
                        }
		   };
		   log.info("submitting " + executorService.getCompletedTaskCount() + " " + executorService.getPoolSize() + " " + executorService.getActiveCount());
                    Future<Object> task = executorService.submit(callablesimple);

                    //TraverseFile.handleFo3(trav);
                } catch (Exception e) {
                    log.error(Constants.EXCEPTION, e);
                }
    		} else {
                    queue.offer(trav);
    		}
    		
      	}
    }
    
    public static String doHandleFo() {
   class HandleFo implements Runnable {
           private TraverseQueueElement trav;
           HandleFo(TraverseQueueElement trav) {
                   this.trav = trav;
           }

        public void run() {
                try {
                        TraverseFile.handleFo3(trav);
                } catch (Exception e) {
                        log.error(Constants.EXCEPTION, e);
                }
        }
    }
log.info("here1");
        TraverseQueueElement trav = Queues.traverseQueue.poll();
        if (trav == null) {
                log.error("empty queue");
                return null;
            }
             HandleFo foRunnable = new HandleFo(trav);
            Thread foWorker = new Thread(foRunnable);
            foWorker.setName("FoWorker");
            foWorker.start();
            return null;
        }

}


