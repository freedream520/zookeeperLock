package com.meiyang.zookeeper.meiyang_zk;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

/**
 * @author chenmeiyang
 * zookeeper基础类
 * 
 */
public class BaseZookeeper implements Watcher{
	
	public ZooKeeper zookeeper;
	private static final int SESSION_TIME_OUT = 2000;
	private CountDownLatch countDownLatch = new CountDownLatch( 1 );
	
	
	/**
	 * chenmeiyang
	 * 连接
	 */
	public void connectZookeeper(String host){
		try{
		    zookeeper = new ZooKeeper(host , SESSION_TIME_OUT , this);
		    countDownLatch.await();
		    System.out.println("zookeeper connect OK");
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/* 
	 * chenmeiyang
	 * 处理
	 */
	public void process(WatchedEvent event) {
		// TODO Auto-generated method stub
		if(event.getState() == KeeperState.SyncConnected){
			System.out.println("watcher received event");
			countDownLatch.countDown();
		}
	}
	
	/**
	 * chenmeiyang
	 * 关闭链接
	 */
	public void closeConnect(){
		if(this.zookeeper != null){
			try {
				zookeeper.close();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * chenmeiyang
	 * 创建节点
	 */
	public String createNode(String path , byte[] data){
		try {
			return this.zookeeper.create(path, data, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * chenmeiyang
	 * 获取孩子节点
	 */
	public List<String>  getChildren(String path){
		try {
			return this.zookeeper.getChildren(path, false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public byte[]  getData(String path){
		try {
			return this.zookeeper.getData(path, false, null);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
