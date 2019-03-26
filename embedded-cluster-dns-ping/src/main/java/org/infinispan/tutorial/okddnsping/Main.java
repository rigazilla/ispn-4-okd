package org.infinispan.tutorial.okddnsping;

import java.io.IOException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
import org.infinispan.Cache;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;

public class Main {

	public static void main(String[] args) throws IOException, InterruptedException {
		Configurator.setLevel(LogManager.getLogger("org.jgroups").getName(), Level.DEBUG);
		System.setProperty("java.net.preferIPv4Stack", "true");
		DefaultCacheManager manager = new DefaultCacheManager("cluster-dns-ping.xml");
		manager.start();		
		while (true) {
		System.out.println("Cluster members list: "+manager.getMembers().toString());
		Thread.sleep(1000);
		}
	}

}
