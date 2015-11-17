package com.meiyang.zookeeper.distributedlock.share;

import com.meiyang.zookeeper.distributedlock.share.ConcurrentTest.ConcurrentTask;

/**
 * @author chenmeiyang
 * 启动一个名为test1和test2的锁，其中test1只有一个线程竞争，test2有十个线程竞争
 * 
 */
public class ZkTest {
	
	
    public static void main(String[] args) {
    	
        Runnable task1 = new Runnable(){
            public void run() {
                DistributedLock lock = null;
                try {
                    lock = new DistributedLock("192.168.10.40:2181","test1");
                    lock.lock();
                    Thread.sleep(3000);
                    System.out.println("===Thread " +"  "+ Thread.currentThread().getId() + " running");
                } catch (Exception e) {
                    e.printStackTrace();
                } 
                finally {
                    if(lock != null)
                        lock.unlock();
                }
                 
            }
             
        };
        new Thread(task1).start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        ConcurrentTask[] tasks = new ConcurrentTask[10];
        for(int i=0;i<tasks.length;i++){
            ConcurrentTask task3 = new ConcurrentTask(){
                public void run() {
                    DistributedLock lock = null;
                    try {
                        lock = new DistributedLock("192.168.10.40:2181","test2");
                        lock.lock();
                        System.out.println("Thread " +"  "+ Thread.currentThread().getId() + " running");
                    } catch (Exception e) {
                        e.printStackTrace();
                    } 
                    finally {
                        lock.unlock();
                    }
                     
                }
            };
            tasks[i] = task3;
        }
        new ConcurrentTest(tasks);
    }
}
