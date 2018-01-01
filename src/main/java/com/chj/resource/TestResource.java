package com.chj.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chj.doc.Tag;
import com.chj.doc.TestDocument;
import com.chj.repository.TestRepository;
import com.chj.utils.JsonUtil;

@RestController
@RequestMapping("test")
public class TestResource {

	@Autowired
	TestRepository testRepository;

	@Autowired
	ElasticsearchTemplate elasticsearchTemplate;

	@RequestMapping(value = "create", method = RequestMethod.PUT)
	public ResponseEntity<?> create() {
		List<Tag> tags = new ArrayList<Tag>();
		tags.add(new Tag("1", "tag1"));
		tags.add(new Tag("2", "tag2"));
		List<TestDocument> list = new ArrayList<TestDocument>();
		TestDocument test = null;
		for (int i = 0; i < 100; i++) {
			test = new TestDocument();
			test.setTitle("this is a title-" +(i + 1) + " -> " + UUID.randomUUID().toString());
			test.setTags(tags);
			list.add(test);
			try {
				Thread.sleep(500l);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		testRepository.save(list);
		return ResponseEntity.ok().build();
	}

	@RequestMapping(value = "update", method = RequestMethod.POST)
	public ResponseEntity<?> update(@RequestBody TestDocument test) {
		test = testRepository.save(test);
		return new ResponseEntity<TestDocument>(test, HttpStatus.OK);
	}

	@RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> delete(@PathVariable("id") String id) {
		testRepository.delete(id);
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@RequestMapping("select/{id}")
	public ResponseEntity<?> selectById(@PathVariable("id") String id) {
		TestDocument test = testRepository.findOne(id);
		return new ResponseEntity<TestDocument>(test, HttpStatus.OK);
	}

	@RequestMapping("select1")
	public ResponseEntity<?> selectPage1(@RequestParam(value = "ps", defaultValue = "10") int pageSize, @RequestParam(value = "pn", defaultValue = "1") int pageNumber) {
		PageRequest pageRequest = new PageRequest(pageNumber, pageSize);
		Page<TestDocument> pageResult = testRepository.findAll(pageRequest);
		return new ResponseEntity<Page<TestDocument>>(pageResult, HttpStatus.OK);
	}

	@RequestMapping("select2")
	public ResponseEntity<?> selectPage2(@RequestParam(value = "ps", defaultValue = "10") int pageSize, @RequestParam(value = "pn", defaultValue = "1") int pageNumber) {
		PageRequest pageRequest = new PageRequest(pageNumber, pageSize);
		SearchQuery searchQuery = new NativeSearchQueryBuilder().withPageable(pageRequest).withSort(new FieldSortBuilder("createDate").order(SortOrder.DESC)).build();
		Page<TestDocument> pageResult = elasticsearchTemplate.queryForPage(searchQuery, TestDocument.class);
		System.out.println(JsonUtil.getJson(pageResult));
		return new ResponseEntity<Page<TestDocument>>(pageResult, HttpStatus.OK);
	}

}
