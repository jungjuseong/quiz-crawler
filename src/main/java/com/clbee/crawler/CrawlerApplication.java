package com.clbee.crawler;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@SpringBootApplication
@EnableAsync
public class CrawlerApplication implements CommandLineRunner {
	private static Logger LOG = LoggerFactory.getLogger(CrawlerApplication.class);
	@Autowired
	private Crawler crawler;

//	@Autowired
//	private ScrapService scrapService;

	public static void main(String[] args) {
		SpringApplication.run(CrawlerApplication.class, args);
	}

//	private void useScrapService(String url) throws InterruptedException {
//
//		scrapService.doScrap(url,1000);
//	}

	private final String HEAD = "https://gichulpass.com/bbs/board.php?bo_table=exam&";
	private String[] urls = new String[] {

			// 국어

			// 국가직9급 국어
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=37&rank=75&part=50&stype=2&page=1",
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=37&rank=75&part=50&stype=2&page=2",
			 //국가직7급
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=37&rank=73&part=50&stype=2&page=1",
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=37&rank=73&part=50&stype=2&page=2",
			// 지방직9급
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=37&rank=75&part=49&stype=2&page=1",
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=37&rank=75&part=49&stype=2&page=2",
			// 지방직7급
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=37&rank=73&part=49&stype=2&page=1",
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=37&rank=73&part=49&stype=2&page=2",
			// 서울시9급
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=37&rank=75&part=48&stype=2&page=1",
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=37&rank=75&part=48&stype=2&page=2",
			// 서울시7급
			"https://gichulpass.com/bbs/board.php?bo_table=exam&stype=2&subject=37&part=48&rank=73",
			// 경찰직
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=37&rank=67&part=41&stype=2&page=1",
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=37&rank=67&part=41&stype=2&page=2",
			// 법원직9급
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=37&rank=67&part=41&stype=2&page=1",
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=37&rank=67&part=41&stype=2&page=2",
			// 국회직9급
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=37&rank=74&part=44&stype=2&page=1",
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=37&rank=74&part=44&stype=2&page=2",

			//한국사

			// 국가직9급
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=34&rank=75&part=50&stype=2&page=1",
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=34&rank=75&part=50&stype=2&page=2",

			// 국가직7급
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=34&rank=73&part=50&stype=2&page=1",
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=34&rank=73&part=50&stype=2&page=2",

			// 지방직9급
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=34&rank=75&part=49&stype=2&page=1",
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=34&rank=75&part=49&stype=2&page=2",

			// 지방직7급
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=34&rank=73&part=49&stype=2&page=1",
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=34&rank=73&part=49&stype=2&page=2",

			// 서울시9급
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=34&rank=75&part=48&stype=2&page=1",
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=34&rank=75&part=48&stype=2&page=2",

			// 서울시7급
			"https://gichulpass.com/bbs/board.php?bo_table=exam&stype=2&subject=34&part=48&rank=73",

			// 경찰직
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=34&rank=67&part=41&stype=2&page=1",
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=34&rank=67&part=41&stype=2&page=2",
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=34&rank=67&part=41&stype=2&page=3",

			// 경찰간부
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=34&rank=63&part=41&stype=2&page=1",
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=34&rank=63&part=41&stype=2&page=2",

			// 법원직9급
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=34&rank=75&part=43&stype=2&page=1",
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=34&rank=75&part=43&stype=2&page=2",

			// 국회직9급
			"https://gichulpass.com/bbs/board.php?bo_table=exam&stype=2&subject=34&part=44&rank=75",

			// 영어

			// 국가직9급
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=36&rank=75&part=50&stype=2&page=1",
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=36&rank=75&part=50&stype=2&page=2",

			// 국가직7급
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=36&rank=73&part=50&stype=2&page=1",

			// 지방직9급
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=36&rank=75&part=49&stype=2&page=1",
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=36&rank=75&part=49&stype=2&page=2",

			// 지방직7급
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=36&rank=73&part=49&stype=2&page=1",
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=36&rank=73&part=49&stype=2&page=2",

			// 서울시9급
			"https://gichulpass.com/bbs/board.php?bo_table=exam&stype=2&subject=36&part=48&rank=75",

			// 서울시7급
			"https://gichulpass.com/bbs/board.php?bo_table=exam&stype=2&subject=36&part=48&rank=73",

			// 경찰직
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=36&rank=67&part=41&stype=2&page=1",
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=36&rank=67&part=41&stype=2&page=2",

			// 법원직9급
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=36&rank=75&part=43&stype=2&page=1",
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=36&rank=75&part=43&stype=2&page=2",

			// 국회직9급
			"https://gichulpass.com/bbs/board.php?bo_table=exam&stype=2&subject=36&part=44&rank=75",
			// 국회직8급
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=36&rank=74&part=44&stype=2&page=1",
			"https://gichulpass.com/bbs/board.php?bo_table=exam&subject=36&rank=74&part=44&stype=2&page=2",
	};

	@Override
	public void run(String... args) throws IOException {
		crawler.useCrawler(urls, "./exam.csv");
	}
}