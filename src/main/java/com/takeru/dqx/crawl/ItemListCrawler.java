package com.takeru.dqx.crawl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.CookieStore;
import org.jsoup.nodes.Document;

import javax.print.Doc;
import javax.rmi.CORBA.Util;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
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
        List<Map> categoryUrlList = keyset.stream()
                .map(key -> {
                    String url = urls.get(key);
                    System.out.println(key);
                    Map<String, String> itemUrlList = null;
                    try {
                        itemUrlList = fetchItemList(url);
                    }catch(IOException e) {
                        e.printStackTrace();
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    return new HashMap<String, Map>().put(key, itemUrlList);
                }
        ).collect(Collectors.toList());

    }

    private Map<String, String> fetchItemList(String categoryBaseUrl) throws IOException, InterruptedException{
        Document document = Utils.convertUrl2JsoupDocument(categoryBaseUrl, cookieStore);
        int maxPage = getMaxPageNumber(document);
        Map<String, String> itemUrlPair = new HashMap<>();

        for(int i = 0; i <= maxPage; i++){
            Document itemPage = Utils.convertUrl2JsoupDocument(categoryBaseUrl + "/page/" + i, cookieStore);

        }

        return itemUrlPair;
    }

    private Map<String, String> parseCategoryPage(Document document){
        return null;
    }

    private int getMaxPageNumber(Document document){

        String lastPageUrl = document.select("div.pageNavi")
                .select("ul")
                .select("li.last")
                .select("a")
                .attr("href");
        if(document.html().contains("ルアー") && lastPageUrl.equals("")){
            return 0;
        }
        String[] splits = lastPageUrl.split("/");
        return Integer.parseInt(splits[splits.length - 1]);
    }

    private Map<String, String> fetchItemCategoryUrls() throws IOException{
        URL path = ItemListCrawler.class.getClassLoader().getResource("conf/ItemCategoryUrls.json");
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(path, Map.class);
    }
}
