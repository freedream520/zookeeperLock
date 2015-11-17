package com.meiyang.zookeeper.distributedlock.share;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * @author chenmeiyang
 * 分布式共享锁
 * 
 */
public class DistributedLock implements Lock , Watcher{
	
	private ZooKeeper zookeeper;
	private String root = "/locks";
	private String lockName;
	private String waitNode;
	private String myZnode;
	private CountDownLatch latch;//计数器
	private int sessionTimeout = 30000;
	private List<Exception> exception = new ArrayList<Exception>();
	
	public DistributedLock(String host , String lockName){
		this.lockName = lockName;
		try{
			zookeeper = new ZooKeeper(host , sessionTimeout , this);
			Stat stat = zookeeper.exists(root, false);
			if(null == stat){
				//创建根节点
				zookeeper.create(root, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
		}catch(Exception e){
//			e.printStackTrace();
			exception.add(e);
		}
		
	}

	public void process(WatchedEvent arg0) {
		if(null != this.latch){
			this.latch.countDown();
		}
	}

	public void lock() {
        if(exception.size() > 0){
        	throw new LockException(exception.get(0));
        }
        try{
        	if(this.tryLock()){//成功获取到锁
        		System.out.println("Thread" + Thread.currentThread().getId() + " " +myZnode+"get lock true");
        		return;
        	}else{//等待获取锁
        		waitForLock(waitNode , sessionTimeout);
        	}
        	
        }catch(Exception e){
        	e.printStackTrace();
        }
	}

	public void lockInterruptibly() throws InterruptedException {
		// TODO Auto-generated method stub
		this.lock();
	}

	/* 
	 * chenmeiyang
	 * 尝试获取锁
	 */
	public boolean tryLock() {
		try{
			String splitStr = "_lock_";
			if(lockName.contains(splitStr)){
				throw new LockException("lock name cannot contains "+splitStr);
			}
			//创建临时子节点
			myZnode = zookeeper.create(root + "/" +lockName + splitStr , new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		    System.out.println(myZnode+ "is created");
		    //取出所有子节点
		    List<String> subNodes = zookeeper.getChildren(root, false);
		    //取出所有lockName的锁
		    List<String> lockObjNodes = new ArrayList<String>();
		    for(String node:subNodes){
		    	String _node = node.split(splitStr)[0];
		    	if(_node.equals(lockName)){
		    		lockObjNodes.add(node);
		    	}
		    }
		    //节点排序
		    Collections.sort( lockObjNodes );
		    System.out.println("===节点排序：==="+lockObjNodes);
		    System.out.println("===当前节点为：======"+myZnode +"=====最小的节点为：======" + lockObjNodes.get(0));
		    if(myZnode.equals(root +"/" + lockObjNodes.get(0))){
		    	//如果是最小的节点，则取得锁
		    	return true;
		    }
		    //如果不是最小的节点，找到比自己小1的节点
		    String subMyZnode = myZnode.substring(myZnode.lastIndexOf("/") + 1);
		    waitNode = lockObjNodes.get( Collections.binarySearch(lockObjNodes, subMyZnode) - 1);
		    System.out.println("==========当前正在等待这个节点=============="+waitNode+"====释放==");
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}

	/* 
	 * chenmeiyang
	 * 尝试获得锁
	 */
	public boolean tryLock(long time, TimeUnit unit)
			throws InterruptedException {
		try{
			if(this.tryLock()){
				return true;
			}
			return waitForLock(waitNode , time);
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * chenmeiyang
	 * 等待获得锁
	 */
	public boolean waitForLock(String lower , long waitTime)throws Exception{
		Stat stat = zookeeper.exists(root + "/" +lower, true);
		//判断比自己小一个数的节点是否存在,如果不存在则无需等待锁,同时注册监听
		if(stat != null){
			System.out.println("Thread"+ Thread.currentThread().getId()+"waiting for" + root + "/"+lower);
			this.latch = new CountDownLatch(1);
			this.latch.await(waitTime , TimeUnit.MILLISECONDS);
			this.latch = null;
		}
		return true;
	}

	/* 
	 * chenmeiyang
	 * 解锁
	 */
	public void unlock() {
		System.out.println("unlock"+myZnode);
		try{
			zookeeper.delete(myZnode, -1);
			myZnode = null;
			zookeeper.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public Condition newCondition() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public class LockException extends RuntimeException{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 4364683174697675620L;
		public LockException(String e){
			super(e);
		}
		public LockException(Exception e){
			super(e);
		}
	}

}
