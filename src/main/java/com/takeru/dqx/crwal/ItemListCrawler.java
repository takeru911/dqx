package com.takeru.dqx.crwal;

import org.apache.http.client.CookieStore;

import java.io.IOException;

public class ItemListCrawler {
    private CookieStore cookieStore;

    public ItemListCrawler() throws IOException{

        cookieStore = Utils.getLoggedInCookie();
    }
}
