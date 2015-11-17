package com.meiyang.zookeeper.distributedlock;

/**
 * @author chenmeiyang
 * 分布式锁
 * 
 */
public class DistributedLock {
	
	private ZKClient client;
	private static final String LOCK_ROOT = "/lock";
	private String lockName;
	private String localIP = "192.168.10.100";
	
	public DistributedLock(String connectString , int sessionTimeout , String lockName){
		this.createConnection(connectString, sessionTimeout);
		this.lockName = lockName;
	}
	
	public boolean tryLock(){
		String path = LOCK_ROOT + "/" +lockName;
		try{
			if(client.exists(path)){
				String ownerip = client.getData(path);
				if(localIP.equals(ownerip)){
					return true;
				}else{
					client.setData(path, localIP.getBytes());
				}
			}else{
				client.createPathIfAbsent(path, false);
				if(client.exists(path)){
					String ownerip = client.getData(path);
					if(localIP.equals(ownerip)){
						return true;
					}else{
						client.setData(path, localIP.getBytes());
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * chenmeiyang
	 * 创建连接
	 */
	public void createConnection(String connectString , int sessionTimeout){
		if(client != null){
			this.releaseConnection();
		}
		client = new ZKClient(connectString , sessionTimeout);
		client.createPathIfAbsent(LOCK_ROOT, true);
	}
	
	/**
	 * chenmeiyang
	 * 释放连接
	 */
	public void releaseConnection(){
		if(client != null){
			client.close();
		}
	}

}
