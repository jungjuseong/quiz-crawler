package com.clbee.crawler;

import com.clbee.crawler.model.Scrap;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class ScrapService {
    // initializing the list of Java object to store the scraped data
    final private List<Scrap> scraped;
    final private List<String> pagesToScrape;
    public ScrapService() {
        scraped = Collections.synchronizedList(new ArrayList<>());
        pagesToScrape = Collections.synchronizedList(new ArrayList<>());
    }

    public List<Scrap> getScraped() {
        return scraped;
    }

    private void getMeta(@NotNull Document doc) {
        Elements metaTags = doc.select("meta");
        for(Element metaTag: metaTags) {
            if(metaTag.attr("property").equals("og:image")) {
                System.out.printf("title: %s\n", metaTag.attr("content"));
            }
            if(metaTag.attr("property").equals("og:image:title")){
                System.out.printf("image: %s\n", metaTag.attr("content"));
            }
            if(metaTag.attr("property").equals("og:image:description")){
                System.out.printf("desc: %s\n", metaTag.attr("content"));
            }
        }
    }
    private void scrapPage(List<String> pagesToScrape, int maxCount) {
        if (!pagesToScrape.isEmpty()) {
            // the current web page is about to be scraped and
            // should no longer be part of the scraping queue
            String url = pagesToScrape.remove(0);

            try {
                Document doc = Jsoup
                        .connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36")
                        .get();

                getMeta(doc);

                // Elements products = doc.select("li.product");
                // Elements products = doc.select("img[src~=(?i)\\.(png|jpe?g|gif)]");
                Elements products = doc.select("div");

                int count = 0;
                // iterating over the list of HTML products
                for (Element product : products) {
                    if (count++ > maxCount) {
                        break;
                    }
                    String imageUrl = product.selectFirst("img").attr("src");
                    if (imageUrl.startsWith("//")) {
                        imageUrl = "https:" + imageUrl;
                    }
                    System.out.println(imageUrl);

                    // adding scraped to the list of the scraped products
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void doScrap(String url, int maxCount) throws InterruptedException {

        final int IterationLimit = 2;
        pagesToScrape.add(url);

        // initializing the ExecutorService to run the
        // web scraping process in parallel on 4 pages at a time
        ExecutorService executorService = Executors.newFixedThreadPool(1) ;

        // launching the web scraping process to discover some
        // urls and take advantage of the parallelization process
        // scrapeProductPage(pokemonProducts, pagesDiscovered, pagesToScrape);

        // the number of iteration executed
        int iteration = 1;
        while (!pagesToScrape.isEmpty() && iteration < IterationLimit) {
            // registering the web scraping task
            executorService.execute(() -> scrapPage(pagesToScrape, maxCount));

            // adding a 200ms delay for avoid overloading the server
            TimeUnit.MILLISECONDS.sleep(500L);

            // incrementing the iteration number
            iteration++;
        }
    }
}