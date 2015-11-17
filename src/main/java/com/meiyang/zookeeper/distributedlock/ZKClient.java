package com.meiyang.zookeeper.distributedlock;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * @author chenmeiyang
 * zookeeper客户端
 * 
 */
public class ZKClient implements Watcher{
	
	private ZooKeeper zookeeper;
	private CountDownLatch coundDonwLatch = new CountDownLatch(1);
	
	/**
	 * @param host
	 * @param sessionTimeout
	 * 链接
	 */
	public ZKClient(String host , int sessionTimeout){
		try {
			zookeeper = new ZooKeeper(host , sessionTimeout , this);
			System.out.println("=====connecting zkserver ok========");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * chenmeiyang
	 * 关闭
	 */
	public void close(){
		try {
			zookeeper.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * chenmeiyang
	 * 创建节点
	 */
	public void createPathIfAbsent(String path , boolean isPersistent){
		CreateMode mode = isPersistent?CreateMode.PERSISTENT:CreateMode.EPHEMERAL;
		if(!this.exists(path)){
			try {
				this.zookeeper.create(path, null, Ids.OPEN_ACL_UNSAFE, mode);
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * chenmeiyang
	 * 获取节点数据
	 */
	public String getData(String path){
		try{
			byte[] data = this.zookeeper.getData(path, false, null);
			return new String(data);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * chenmeiyang
	 * 写入数据
	 */
	public void setData(String path , byte[] data){
		try {
			this.zookeeper.setData(path, data, this.getVersion(path));
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public int getVersion(String path){
		try{
			Stat stat = this.zookeeper.exists(path, null);
			return stat.getVersion();
		}catch(Exception e){
			e.printStackTrace();
		}
		return -1;
	}
	
	/**
	 * chenmeiyang
	 * 判断该节点是否存在
	 */
	public boolean exists(String path){
		try {
			Stat stat = this.zookeeper.exists(path, null);
			return stat != null;
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/* 
	 * chenmeiyang
	 * 处理
	 */
	public void process(WatchedEvent event) {
		if(event == null) return;
		//连接状态
		KeeperState state = event.getState();
		//事件类型
		EventType type = event.getType();
		if(state == KeeperState.SyncConnected){//已经成功连接zookeeper服务器
			System.out.println("=======zookeepr connect success================");
		}
	}

}
