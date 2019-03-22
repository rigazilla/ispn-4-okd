package org.infinispan.tutorial.okddnsping;

import java.io.IOException;

import org.infinispan.Cache;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;

public class Main {

	public static void main(String[] args) throws IOException, InterruptedException {
		System.setProperty("java.net.preferIPv4Stack", "true");
		//GlobalConfigurationBuilder gcb = GlobalConfigurationBuilder.defaultClusteredBuilder();
		DefaultCacheManager manager = new DefaultCacheManager("cluster-dns-ping.xml");
		manager.start();		
		Cache<String, String> defaultCache = manager.getCache();
		String s = defaultCache.get("first");
		System.out.println("got: "+s);
		defaultCache.put("first","first");
		System.out.println("put");
		while (true) {
		System.out.println(manager.getMembers().toString());
		Thread.sleep(1000);
		}
	}

}
