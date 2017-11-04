package com.takeru.dqx.crawl;

import com.takeru.dqx.item.Item;
import com.takeru.dqx.item.ItemDetail;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BazaarCrawlerTest {

    @Test
    public void getMaxPageNumberTest(){
        String htmlFilePath = UserAuthTest
                .class
                .getClassLoader()
                .getResource("com/takeru/dqx/crawl/bazaarItemTop.html").getPath();
        try {
            Document document = Jsoup.parse(new File(htmlFilePath), "UTF-8");
            BazaarCrawler crawler = new BazaarCrawler();
            Method method = BazaarCrawler.class.getDeclaredMethod("getMaxPageNumber", Document.class);
            method.setAccessible(true);
            int maxPageNumber = (Integer)method.invoke(crawler, document);
            assertEquals(7, maxPageNumber);
        }catch (IOException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e){
            e.printStackTrace();
        }
    }

    @Test
    public void parseBazaarPagePricesTest(){
        String htmlFilePath = UserAuthTest
                .class
                .getClassLoader()
                .getResource("com/takeru/dqx/crawl/bazaarItemDetail.html").getPath();
        try {
            Document document = Jsoup.parse(new File(htmlFilePath), "UTF-8");
            BazaarCrawler crawler = new BazaarCrawler();
            Item item = new Item("18215d7cc856cec55d789973bc207f192cd01741", "よるのパピヨン");
            Method method = BazaarCrawler.class.getDeclaredMethod("parseBazaarPagePrices", Document.class, Item.class);
            method.setAccessible(true);
            method.invoke(crawler, document, item);
            Map actualObj = crawler.getItemPrices();

            Map<ItemDetail, List<Integer>> expectedObj = new HashMap<>();

            expectedObj.put(
                    new ItemDetail(item, 0,0),
                    Arrays.asList(5900)
            );
            expectedObj.put(
                    new ItemDetail(item, 2,0),
                    Arrays.asList(5900, 5999, 6000, 6000, 6000)
            );
            expectedObj.put(
                    new ItemDetail(item, 1,1),
                    Arrays.asList(6000, 7000)
            );
            expectedObj.put(
                    new ItemDetail(item, 3,0),
                    Arrays.asList(9990, 10000)
            );
            assertEquals(expectedObj, actualObj);
        }catch (IOException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
        }
    }
}