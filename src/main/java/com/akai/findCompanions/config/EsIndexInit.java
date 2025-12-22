package com.akai.findCompanions.config;

import com.akai.findCompanions.mapper.es.UserDocumentMapper;
import com.akai.findCompanions.model.domain.Es.UserDocument;
import org.dromara.easyes.annotation.IndexName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

public class EsIndexInit implements CommandLineRunner {
    @Autowired
    private UserDocumentMapper userDocumentMapper;

    @Override
    public void run(String... args) throws Exception {
        String indexName = UserDocument.class
                .getAnnotation(IndexName.class)
                .value();
        if (!userDocumentMapper.existsIndex(indexName)) {
            userDocumentMapper.createIndex(indexName);
        }
    }
}
