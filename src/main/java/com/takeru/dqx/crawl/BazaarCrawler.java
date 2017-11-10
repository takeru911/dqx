package com.takeru.dqx.crawl;


import com.takeru.dqx.item.Item;
import com.takeru.dqx.item.ItemDetail;
import org.apache.http.client.CookieStore;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class BazaarCrawler {
    private CookieStore cookieStore;

    private Map<ItemDetail, List<Integer>> itemPrices;
    public static void main(String[] args){
        try{
            BazaarCrawler crawler = new BazaarCrawler();
            crawler.fetchBazaarPrices(new Item("e8370994e891fcd8c0d14ca18e6036b15b3f2416", "ブラッドカメリア"));
            Map<ItemDetail, List<Integer>> map = crawler.getItemPrices();
            Set<ItemDetail> keyset = map.keySet();
            for (ItemDetail itemDetail: keyset){
                System.out.println(itemDetail.toString());
                System.out.println(map.get(itemDetail).toString());

            }
        }catch (Exception e ){
            e.printStackTrace();
        }
    }

    public BazaarCrawler() throws IOException{
        this.cookieStore = Utils.getLoggedInCookie();
        itemPrices = new HashMap<>();
    }

    public void fetchBazaarPrices(Item item) throws IOException, InterruptedException{
        final String baseBazaarUrl = "http://hiroba.dqx.jp/sc/search/bazaar";
        final String itemBaseUrl = baseBazaarUrl + "/" + item.getItemId();
        Document topPage = Utils.convertUrl2JsoupDocument(itemBaseUrl, cookieStore);
        int maxPage = getMaxPageNumber(topPage);
        for(int i = 0; i <= maxPage; i++){
            Document itemPage = Utils.convertUrl2JsoupDocument(itemBaseUrl + "/0/page/" + i , cookieStore);
            parseBazaarPagePrices(itemPage, item);
        }
    }

    private void parseBazaarPagePrices(Document document, Item item){
        Elements table = document
                .select("div#contentArea div.cttBox div#bazaarList table tbody tr");

        int i = 0;
        for(Element element: table){
            if(i == 0){
                //gueee----
                //TODO naosu
                i++;
                continue;
            }
            int stars = element.select("td div.itemInfo p span.starArea span.star").html().length();
            String itemName = element.select("td div.itemInfo p").get(0).text();
            String exhibitNum = element.select("td")
                    .get(1)
                    .getElementsByTag("p")
                    .get(0).html().replaceAll("[^0-9]", "");
            String price = element.select("td")
                    .get(1)
                    .getElementsByTag("p")
                    .get(1).html().split("G")[0].replaceAll("[^0-9]", "");
            String alchemy = "0";
            if(itemName.contains("+")){
                alchemy = itemName.split("\\+")[1];
            }

            ItemDetail itemDetail = new ItemDetail(item, stars, Integer.parseInt(alchemy));
            if(!itemPrices.containsKey(itemDetail)){
                itemPrices.put(itemDetail, new ArrayList<>());
            }
            itemPrices.get(itemDetail).add(Integer.parseInt(price) / Integer.parseInt(exhibitNum));
        }
    }

    private int getMaxPageNumber(Document document){

        String lastPageUrl = document
                .select("div.cttBox div.pageNavi ul li.last a")
                .attr("href");
        //1pageしかない場合
        if(lastPageUrl.length() == 0){
            return 0;
        }
        String[] split = lastPageUrl.split("/");
        String lastPageNumber = split[split.length - 1];
        return Integer.parseInt(lastPageNumber);
    }

    public Map<ItemDetail, List<Integer>> getItemPrices() {
        return itemPrices;
    }

}
