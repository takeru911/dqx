package com.takeru.dqx.crawl;

import com.takeru.dqx.crwal.Crawler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
public class CrawlerTest {


    @Test
    public void parseCisSessidTest(){
        String htmlFilePath = CrawlerTest
                .class
                .getClassLoader()
                .getResource("com/takeru/dqx/crawl/Cis_SSid_Test.html").getPath();
        try {
            Document document = Jsoup.parse(new File(htmlFilePath), "UTF-8");
            Crawler crawler = new Crawler();
            Method method = Crawler.class.getDeclaredMethod("parseCisSessid", String.class);
            method.setAccessible(true);
            String cis_Ssid = (String)method.invoke(crawler, document.html());
            assertEquals("aaaaaaaaaaaaaaaa", cis_Ssid);
        }catch (IOException e){
            e.printStackTrace();
        }catch (NoSuchMethodException e){
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void parseStoredValueTest(){
        String htmlFilePath = CrawlerTest
                .class
                .getClassLoader()
                .getResource("com/takeru/dqx/crawl/StoredValueTest.html").getPath();
        try {
            Document document = Jsoup.parse(new File(htmlFilePath), "UTF-8");
            Crawler crawler = new Crawler();
            Method method = Crawler.class.getDeclaredMethod("parseStoredValue", String.class);
            method.setAccessible(true);
            String storedValue = (String)method.invoke(crawler, document.html());
            assertEquals("3df06e5f47798101ac62bce3c650e276ef93bf147f79411483c2d3fcd790cbb49ecb2d0243ff0f295717fd1c29c94a512a68ed86f8b11af045f5bfe419922d10ef0ea1798fa8b184b39170e7df9f3757e480023aa2c99a68aa9b2ce40554b9571c0d7f2f93cba8c9cdf40e3bd7237113e6", storedValue);
        }catch (IOException e){
            e.printStackTrace();
        }catch (NoSuchMethodException e){
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}

