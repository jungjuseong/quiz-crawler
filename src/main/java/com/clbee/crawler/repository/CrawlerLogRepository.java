package com.clbee.crawler.repository;

import com.clbee.crawler.model.CrawlerLog;
import com.clbee.crawler.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrawlerLogRepository extends JpaRepository<CrawlerLog,Integer> {
//    public String findByTitle(String title);
    public boolean existsByTitle(String title);
}
