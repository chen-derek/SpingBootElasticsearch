package com.chj.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.chj.doc.TestDocument;

public interface TestRepository extends ElasticsearchRepository<TestDocument, String> {

	Page<TestDocument> findByTagsName(String name, Pageable pageable);
}