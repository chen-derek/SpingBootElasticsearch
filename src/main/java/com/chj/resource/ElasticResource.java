package com.chj.resource;

import java.util.Map;

import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chj.doc.TestDocument;

@RestController
@RequestMapping("elastic")
public class ElasticResource {

	@Autowired
	private ElasticsearchOperations elasticsearchOperations;

	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;

	@GetMapping("details")
	public ResponseEntity<Map<String, String>> getElasticInformation() {
		Client client = elasticsearchOperations.getClient();
		Map<String, String> asMap = client.settings().getAsMap();
		return ResponseEntity.ok(asMap);
	}

	@PutMapping("clear-indices")
	public void clearIndices() {
		elasticsearchTemplate.deleteIndex(TestDocument.class);
		elasticsearchTemplate.createIndex(TestDocument.class);
		elasticsearchTemplate.putMapping(TestDocument.class);
		elasticsearchTemplate.refresh(TestDocument.class);
	}

}
