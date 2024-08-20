package com.stock_index.stock_index.indexcontroller;

import com.stock_index.stock_index.indexentity.IndexEntity;
import com.stock_index.stock_index.indexservice.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class IndexController {

    @Autowired
    IndexService indexService;

    @GetMapping("/api/test")
    public String testEndpoint() {
        return "Test endpoint is working!";
    }

    @GetMapping("/get-index")
    public List<IndexEntity> getLatestStockInfo() {
        indexService.getlatest();
        return indexService.getall();
    }
}
