package com.meiyang.zookeeper.meiyang_zk;

import java.util.Arrays;
import java.util.List;

/**
 * @author chenmeiyang
 * 客户端
 * 
 */
public class Client {
	
	public static void main(String[] args){
		
		BaseZookeeper baseZookeeper = new BaseZookeeper();
		
		String host = "192.168.10.40:2181";
		
		baseZookeeper.connectZookeeper(host);
		System.out.println("=====connect zookeeper ok===");
		
		//创建节点
//		byte[] data = {1 , 2 , 3 , 4 ,5};
//		String result = baseZookeeper.createNode("/test", data);
//		System.out.println(result);
//		System.out.println("=====create node ok===");
		
		//获取某路径下所有节点
		List<String> children = baseZookeeper.getChildren("/");
		for(String s : children){
			System.out.println("==节点==="+s);
		}
		System.out.println("========get children ok===========");
		
		//获取节点数据
		byte[] nodeData = baseZookeeper.getData("/test");
		System.out.println(Arrays.toString(nodeData));
		System.out.println("=======get node data OK==========");
		
		
	}

}
