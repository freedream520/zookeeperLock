package com.meiyang.zookeeper.distributedlock;

public class Main {
	
	private static final String HOST = "192.168.10.40:2181";
	private static final int    SESSION_TIMEOUT = 2000;

	public static void main(String[] args) {
		DistributedLock lock = new DistributedLock(HOST ,SESSION_TIMEOUT , "buy" );
		boolean isGetLock = lock.tryLock();
        System.out.println("==========="+isGetLock);
	}

}
