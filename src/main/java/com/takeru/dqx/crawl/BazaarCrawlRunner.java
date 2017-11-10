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
        if(args.length != 1){
            System.out.println("Usage: 引数にcrawlしたいカテゴリーを入れてくだせい");
            System.exit(1);
        }
        String category = args[0];
        BazaarCrawlRunner runner = new BazaarCrawlRunner(category);
        runner.run();
        List<Map<ItemDetail, List<Integer>>> list = runner.itemPrices;
        ObjectMapper mapper = new ObjectMapper();
        String str = mapper.writeValueAsString(list.get(0));
        System.out.println(str);
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
