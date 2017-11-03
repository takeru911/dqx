package com.takeru.dqx.crawl;

import com.takeru.dqx.crwal.UserAuth;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
public class UserAuthTest {


    @Test
    public void parseCisSessidTest(){
        String htmlFilePath = UserAuthTest
                .class
                .getClassLoader()
                .getResource("com/takeru/dqx/crawl/Cis_SSid_Test.html").getPath();
        try {
            Document document = Jsoup.parse(new File(htmlFilePath), "UTF-8");
            UserAuth userAuth = UserAuth.getInstance();
            Method method = UserAuth.class.getDeclaredMethod("parseCisSessid", String.class);
            method.setAccessible(true);
            String cis_Ssid = (String)method.invoke(userAuth, document.html());
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
        String htmlFilePath = UserAuthTest
                .class
                .getClassLoader()
                .getResource("com/takeru/dqx/crawl/StoredValueTest.html").getPath();
        try {
            Document document = Jsoup.parse(new File(htmlFilePath), "UTF-8");
            UserAuth userAuth = UserAuth.getInstance();
            Method method = UserAuth.class.getDeclaredMethod("parseStoredValue", String.class);
            method.setAccessible(true);
            String storedValue = (String)method.invoke(userAuth, document.html());
            assertEquals("1234", storedValue);
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

