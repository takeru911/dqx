package com.takeru.dqx.crawl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemListCrawlerTest {

    @Test
    public void getMaxPageNumberTest(){
        String htmlFilePath = UserAuthTest
                .class
                .getClassLoader()
                .getResource("com/takeru/dqx/crawl/categoryTopPage.html").getPath();
        try {
            Document document = Jsoup.parse(new File(htmlFilePath), "UTF-8");
            ItemListCrawler crawler = new ItemListCrawler();
            Method method = ItemListCrawler.class.getDeclaredMethod("getMaxPageNumber", Document.class);
            method.setAccessible(true);
            int maxPageNumber = (Integer)method.invoke(crawler, document);
            assertEquals(3, maxPageNumber);
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