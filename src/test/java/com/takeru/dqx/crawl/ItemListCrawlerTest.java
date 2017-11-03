package com.takeru.dqx.crawl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

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

    @Test
    public void parseCategoryPageTest(){
        String htmlFilePath = UserAuthTest
                .class
                .getClassLoader()
                .getResource("com/takeru/dqx/crawl/itemListPage.html").getPath();
        try {
            Document document = Jsoup.parse(new File(htmlFilePath), "UTF-8");
            ItemListCrawler crawler = new ItemListCrawler();
            Method method = ItemListCrawler.class.getDeclaredMethod("parseCategoryPage", Document.class);
            method.setAccessible(true);
            Map itemList = (Map)method.invoke(crawler, document);
            Map<String, String> expectedMaps = new HashMap<>();
            expectedMaps.put("銅の鍛冶ハンマー", "94d0a520d7ec5cdd8e15202dfeb6e00da3d26cad");
            expectedMaps.put("鉄の鍛冶ハンマー", "74d02cc8d0dce0b8251583d7392371083e9849c0");
            expectedMaps.put("銀の鍛冶ハンマー", "198f83ffecdc8bc2d5a3cdafe7d15660cd911c1a");
            expectedMaps.put("銀の鍛冶ハンマー小", "c2a5a16d002307f71f599fb68fdd12d78001198e");
            expectedMaps.put("プラチナ鍛冶ハンマ", "7e46fba3408d2fa19fc6b1cdba573cb3d1ad735a");
            expectedMaps.put("プラチナ鍛冶ハン小", "8358196bba30b5067cf68f41f8a999ba01db5c3a");
            expectedMaps.put("超鍛冶ハンマー", "c4b6e3dd686b96302b98048e80c2d730bb93a439");
            expectedMaps.put("超鍛冶ハンマー小", "fedc6090abf9885d520038b0111073215843fb3b");
            expectedMaps.put("奇跡の鍛冶ハンマー", "bb9dac5c992d242288f8482eff69c303150a23d8");
            expectedMaps.put("奇跡の鍛冶ハンマ小", "e96a83decf8c7d530fcdec8903e62c6517f1c053");
            assertEquals(expectedMaps, itemList);
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