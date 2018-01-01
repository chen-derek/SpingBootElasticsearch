package com.chj.config;

import java.net.InetSocketAddress;

import javax.annotation.Resource;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@PropertySource(value = "classpath:elasticsearch properties")
@EnableElasticsearchRepositories(basePackages = "com.chj.repository")
public class ElasticsearchConfiguration {
	@Resource
	private Environment environment;

	@Bean
	public Client client() {
		Settings esSettings = Settings.settingsBuilder()
				.put("cluster.name", environment.getProperty("elasticsearch.clustername"))
				.put("node.name", environment.getProperty("elasticsearch.nodename"))
				.put("client.transport.sniff", true)
				.build();

		TransportClient client = TransportClient.builder().settings(esSettings)
		            .build()
		            .addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress(environment.getProperty("elasticsearch.host"), Integer.valueOf(environment.getProperty("elasticsearch.port")))));
		return client;
	}

	@Bean
	public ElasticsearchOperations elasticsearchTemplate() {
		return new ElasticsearchTemplate(client());
	}
}
