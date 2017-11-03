package com.takeru.dqx.crwal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.CookieStore;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ItemListCrawler {
    private CookieStore cookieStore;

    public ItemListCrawler() throws IOException{

        cookieStore = Utils.getLoggedInCookie();
    }

    public static void main(String[] args){
        try {
            new ItemListCrawler().fetchItemUrls();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void fetchItemUrls() throws IOException{
        Map<String, String> urls = fetchItemCategoryUrls();
        Set<String> keyset = urls.keySet();
        keyset
                .stream()
                .map(key -> {
                    String url = urls.get(key);
                    Map<String, String> itemUrlList = null;
                    try {
                        Document document =  Utils.convertUrl2JsoupDocument(url, cookieStore);
                        itemUrlList = parseCategoryPage(document);
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                    return new HashMap<String, Map>().put(key, itemUrlList);
                }
        ).collect(Collectors.toList());
    }

    private Map<String, String> parseCategoryPage(Document document){
        int maxPage =
    }

    private int getMaxPageNumber(Document document){

    }

    private Map<String, String> fetchItemCategoryUrls() throws IOException{
        URL path = ItemListCrawler.class.getClassLoader().getResource("conf/ItemCategoryUrls.json");
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(path, Map.class);
    }
}
