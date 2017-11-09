package com.takeru.dqx.crawl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.takeru.dqx.item.Item;
import com.takeru.dqx.item.ItemDetail;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BazaarCrawlRunner implements Runnable{
    private List<Item> categoryItems;
    private List<Map<ItemDetail, List<Integer>>> itemPrices;

    public static void main(String[] args) throws Exception{
        BazaarCrawlRunner runner = new BazaarCrawlRunner("ルアー");
        runner.run();
        List<Map<ItemDetail, List<Integer>>> list = runner.itemPrices;
        for(Map<ItemDetail, List<Integer>> itemPrice: list){
            Set<ItemDetail> details = itemPrice.keySet();
            for(ItemDetail itemDetail: details){
                System.out.println(itemDetail.toString());
                System.out.println(itemPrice.get(itemDetail).toString());
            }
        }
    }

    public BazaarCrawlRunner(String category) throws IOException{
        this.categoryItems = fetchCategoryItems(category);
    }

    private List<Item> fetchCategoryItems(String category) throws IOException{
        final String confFilePath = Utils.class.getClassLoader().getResource("conf/ItemUrls.json").getPath();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Map> map = mapper.readValue(new File(confFilePath),Map.class);
        Map<String, String> itemUrls = map.get(category);
        Set<String> itemNames = itemUrls.keySet();
        List<Item> items = itemNames.stream()
                .map(k ->
                        {
                            System.out.println(k);
                            String url = itemUrls.get(k);
                            return new Item(url, k);
                        }
                ).collect(Collectors.toList());
        return items;
    }

    @Override
    public void run(){
        try {
            BazaarCrawler crawler = new BazaarCrawler();
            itemPrices = categoryItems.
                    stream()
                    .map(item ->
                            {
                                try {
                                    crawler.fetchBazaarPrices(item);
                                    return crawler.getItemPrices();
                                }catch (InterruptedException | IOException e){
                                    e.printStackTrace();
                                    return null;
                                }
                            }
                    ).collect(Collectors.toList());
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
