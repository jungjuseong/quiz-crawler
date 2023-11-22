package com.clbee.crawler;

import com.clbee.crawler.model.CrawlerLog;
import com.clbee.crawler.model.Quiz;
import com.clbee.crawler.repository.CrawlerLogRepository;
import com.clbee.crawler.repository.QuizRepository;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.json.JsonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.NoSuchElementException;

@Slf4j
@Component
public class Crawler {
    private final static Logger LOG = LoggerFactory.getLogger(CrawlerApplication.class);

    @Autowired
    QuizRepository quizRepository;

    @Autowired
    CrawlerLogRepository crawlerLogRepository;

    private WebDriver driver;
    public final static String WEB_DRIVER_ID = "webdriver.chrome.driver";
    public final static String WEB_DRIVER_PATH = "./mac-x64/chromedriver";

    public Crawler() {

    }

    public void useCrawler(String[] urls, String fileName) throws IOException, NoSuchElementException {

        System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);

        // webDriver 옵션 설정.
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--lang=ko");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");

        // weDriver 생성.
        driver = new ChromeDriver(options);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(100));

        quizRepository.deleteAll();
        crawlerLogRepository.deleteAll();

        FileOutputStream fos = new FileOutputStream(fileName);
        DataOutputStream outStream = new DataOutputStream(new BufferedOutputStream(fos));

        for (String url : urls) {
            driver.get(url);

            Map<String, Object> metaInfo = getMetaInfo();
            List<String> pageLinks = getPageLink();

            for (String pageLink : pageLinks) {
                System.out.println(pageLink);

                try {
                    getQuestions(pageLink, outStream);
                }
                catch (WebDriverException wd) {
                    System.out.println("reconnect chromedriver");
                    driver = new ChromeDriver(options);
                }
            }
        }
        outStream.close();
        driver.close();
        driver.quit();
    }

    private List<String> getQnum(WebElement pr, DataOutputStream outStream) throws IOException {
        List<String> qnum = new ArrayList<>();
        List<WebElement> questions = pr.findElements(By.tagName("label"));

        for (WebElement qElement : questions) {
            String question = qElement.getAttribute("innerHTML").replace("&nbsp;"," ") + "\n";
            qnum.add(question);
        }
        return qnum;
    }

    public void getQuestions(String pageUrl, DataOutputStream outStream) throws IOException {
        final By ByAnswerExplan = By.className("answer_explan");
        final By ByAnswerNum = By.className("answer_num");

        final By ByExamList = By.xpath("//ul[@id='examList']");
        final By ByPrs = By.xpath("//li[starts-with(@style,'background-color:')]");
        final By ByProblem = By.className("pr_problem");
        final By ByShowAnswerButton = By.xpath("//button[@class='ctr_btn all_viewer']");

        driver.get(pageUrl);
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));

        String title = driver.getTitle(); // getMetaInfo();
        System.out.println("-------" + title);

        if (crawlerLogRepository.existsByTitle(title)) {
            System.out.printf("skip -  %s\n", title);
            return;
        }

        WebElement examLocator;

        try {
            examLocator = driver.findElement(ByExamList);
        }
        catch (NotFoundException s) {
            System.out.println("examList - NoSuchElementException");
            return;
        }

        outStream.write((title+"\n").getBytes());
        WebElement show = driver.findElement(ByShowAnswerButton);
        show.click();

        List<WebElement> prs = examLocator.findElements(ByPrs);

        for (WebElement pr : prs) {
            String problem = pr.findElement(ByProblem).getText() + "\n";
            List<String> qnums = getQnum(pr, outStream);
            String example2 = "";
            String answerExplain = "";
            String answerNum = pr.findElement(ByAnswerNum).getText() + "\n";
            try {
                answerExplain = pr.findElement(ByAnswerExplan).getAttribute("innerHTML") + "\n";
                example2 = pr.findElement(By.className("example2")).getAttribute("innerHTML") + "\n";
            }
            catch (NotFoundException e) {
                //System.out.println("Example2 NotFound");
                //e.printStackTrace();
            }
            catch (JsonException je) {
                System.out.println("JsonException - unable to parse");
            }
            catch (WebDriverException wd) {
                System.out.println("WebDriverException - unable to connect");
                throw wd;
            }
            finally {
                Quiz quiz = new Quiz();

                // 2017년 국가직 7급 국어 「가」
                String[] s = title.split(" ");

                String effectiveDate = s[0]; // 2015년 경찰직(순경1차) 영어 > 기출문제 | 공무원 기출문제은행
                String qualification = s[1] + s[2];
                String subject = s[3] + s[4];

                if (title.contains("경찰")) {
                    qualification = s[1];
                    subject = s[2];
                }

                quiz.setSubject(subject);
                quiz.setEffectiveDate(effectiveDate); // 2017년
                quiz.setQualification(qualification); // 국가직7급

                quiz.setProblem(problem);
                quiz.setExample2(example2.getBytes());
                quiz.setQnums(qnums);
                quiz.setAnswerExplain(answerExplain.getBytes());
                quiz.setAnswerNum(answerNum);
                quizRepository.save(quiz);
            }
        }
        CrawlerLog clog =  new CrawlerLog();
        clog.setTitle(title);
        crawlerLogRepository.save(clog);
    }

    private void writeQuiz(Quiz quiz, DataOutputStream outStream) throws IOException {
        outStream.write(quiz.getProblem().getBytes(StandardCharsets.UTF_8));
        outStream.write(quiz.getExample2());
        for (String qnum : quiz.getQnums()) {
            outStream.write(qnum.getBytes(StandardCharsets.UTF_8));
        }
        outStream.write(quiz.getAnswerExplain());
        outStream.write(quiz.getAnswerNum().getBytes(StandardCharsets.UTF_8));
        outStream.flush();
    }

    private String getMetaXPath(String og) {
        return String.format("//meta[@property='%s']", og);
    }
    public Map<String, Object> getMetaInfo() {
        final String OG_IMAGE = "og:image";
        final String OG_TITLE = "og:image:title";
        final String OG_DESC = "og:image:description";

        String image = driver.findElement(By.xpath(getMetaXPath(OG_IMAGE))).getAttribute("content");
        String title = driver.findElement(By.xpath(getMetaXPath(OG_TITLE))).getAttribute("content");
        String desc = driver.findElement(By.xpath(getMetaXPath(OG_DESC))).getAttribute("content");

        Map<String,Object> metaInfo = new HashMap<>();
        metaInfo.put(OG_IMAGE, image);
        metaInfo.put(OG_TITLE, title);
        metaInfo.put(OG_DESC, desc);

        return metaInfo;
    }

    private String getLinkPath() {
        return "//a[@class='list_subject']";
    }
    public List<String>getPageLink() {
        List<String> pageLinks = new ArrayList<>();
        List<WebElement> pageElements = driver.findElements(By.xpath(getLinkPath()));

        for (WebElement pageElement : pageElements) {
            pageLinks.add(pageElement.getAttribute("href"));
        }
        return pageLinks;
    }
}

