package com.stock_index.stock_index.indexservice;

import com.stock_index.stock_index.indexentity.IndexEntity;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class IndexService {

    final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    private final AtomicReference<List<IndexEntity>> indexStockInfoList = new AtomicReference<>(new ArrayList<>());

    public void getlatest() {
        try {
            String url = "https://www.google.com/finance/quote/M%26M:NSE?hl=en&gl=in";
            String response = getStockIndex(url);
            Document doc = Jsoup.parse(response);

            // Select multiple elements for each class
            Elements elementsClass1 = doc.getElementsByClass("pKBk1e"); // Adjust this class as needed
            Elements elementsClass2 = doc.getElementsByClass("lh92"); // Adjust this class as needed
            Elements elementsClass3 = doc.getElementsByClass("T7Akdb"); // Adjust this class as needed

            // Process the elements and collect their text
            List<String> stockNames = elementsClass1.stream()
                    .map(Element::text)
                    .distinct()
                    .collect(Collectors.toList());
            List<String> stockPrices = elementsClass2.stream()
                    .map(Element::text)
                    .distinct()
                    .collect(Collectors.toList());
            List<String> stockRates = elementsClass3.stream()
                    .map(Element::text)
                    .distinct()
                    .collect(Collectors.toList());

            // For demonstration purposes, print all collected values
            System.out.println("Stock Names:");
            stockNames.forEach(System.out::println);

            System.out.println("Stock Prices:");
            stockPrices.forEach(System.out::println);

            System.out.println("Stock Rates:");
            stockRates.forEach(System.out::println);

            // Create a list of StockInfo objects
            List<IndexEntity> stockInfoList = new ArrayList<>();
            for (int i = 0; i < stockNames.size(); i++) {
                String name = stockNames.get(i);
                String price = i < stockPrices.size() ? stockPrices.get(i) : "Not found";
                String rate = i < stockRates.size() ? stockRates.get(i) : "Not found";
                stockInfoList.add(new IndexEntity(name, price, rate));
            }

            indexStockInfoList.set(stockInfoList);

        } catch (IOException e) {
            // Log error here using a logging framework like SLF4J or Log4j
            e.printStackTrace(); // Optionally keep this for debugging; replace with a logger in production
        }
    }

    public List<IndexEntity> getall() {
        return indexStockInfoList.get();
    }

    private String getStockIndex(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return response.body().string();
        }
    }
}
