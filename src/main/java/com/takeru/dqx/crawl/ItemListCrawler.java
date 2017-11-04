package com.takeru.dqx.crawl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.CookieStore;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.omg.PortableInterceptor.ServerRequestInfo;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ItemListCrawler {
    private CookieStore cookieStore;

    public ItemListCrawler() throws IOException{

        cookieStore = Utils.getLoggedInCookie();
    }

    public static void main(String[] args){
        try {
            Map<String, Map> itemUrls = new ItemListCrawler().fetchItemUrls();
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(new File("ItemUrls.json"), itemUrls);
        }catch(IOException e){
            e.printStackTrace();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    private Map<String, Map> fetchItemUrls() throws IOException, InterruptedException{
        Map<String, String> urls = fetchItemCategoryUrls();
        Set<String> keyset = urls.keySet();
        Map<String, Map> categoriesItemUrls = new HashMap<>();
        for(String key: keyset){
            String url = urls.get(key);
            Map<String, String> itemUrlList = fetchItemList(url);
            categoriesItemUrls.put(key, itemUrlList);
        }

        return categoriesItemUrls;
    }

    private Map<String, String> fetchItemList(String categoryBaseUrl) throws IOException, InterruptedException{
        Document document = Utils.convertUrl2JsoupDocument(categoryBaseUrl, cookieStore);
        int maxPage = getMaxPageNumber(document);
        Map<String, String> itemUrlPair = new HashMap<>();

        for(int i = 0; i <= maxPage; i++){
            Document itemPage = Utils.convertUrl2JsoupDocument(categoryBaseUrl + "/page/" + i, cookieStore);
            Map pageItemList = parseCategoryPage(itemPage);
            itemUrlPair.putAll(pageItemList);
        }

        return itemUrlPair;
    }

    private Map<String, String> parseCategoryPage(Document document){
        Elements elements = document.select("div#contentArea")
                .select("div.cttBox")
                .select("table.searchItemTable")
                .select("tbody");
        Map<String, String> itemMap = new HashMap<>();
        int i = 0;
        for(Element element: elements){
            if(i == 0){
                i++;
                //最初はskipしたい・・・
                //TODO: きっともっとよいやりかたあるよ・・・
                continue;
            }
            Elements itemTag = element.select("tr")
                    .select("td")
                    .select("a.strongLnk");
            String url = itemTag.attr("href");
            String[] split = url.split("/");
            // /sc/game/item/${ItemId}/
            String itemId = split[split.length - 1];
            String itemName = itemTag.text();
            itemMap.put(itemName, itemId);
        }

        return itemMap;
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
