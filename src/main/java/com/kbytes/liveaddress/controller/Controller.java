package com.kbytes.liveaddress.controller;

import com.kbytes.liveaddress.persistence.elasticsearch.models.ESLiveAddress;
import com.kbytes.liveaddress.persistence.sqldb.models.RateLimit;
import com.kbytes.liveaddress.service.MigrateService;
import com.kbytes.liveaddress.service.RateLimitService;
import com.kbytes.liveaddress.service.SearchService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class Controller {
    @Autowired
    private MigrateService migrateService;
    
    @Autowired
    private SearchService searchService;
    
    @Autowired
    private RateLimitService rateLimitService;

    @GetMapping("/search")
    public ResponseEntity<List<ESLiveAddress>> searchAsYouType(@RequestParam String input, HttpServletRequest request) {
        List<ESLiveAddress> results = searchService.searchAsYouType(input);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/duplicate/{duplicate}")
    public ResponseEntity<String> migrate(@PathVariable int duplicate) {
        return ResponseEntity.ok(migrateService.migrate(duplicate));
    }

    @GetMapping("/sync")
    public ResponseEntity<String> migrate() {
        return ResponseEntity.ok(migrateService.migrate());
    }

    @PostMapping("/ratelimit/{clientId}/{rateLimit}")
    public ResponseEntity<RateLimit> rateLimit(@PathVariable String clientId, @PathVariable int rateLimit) {
        return ResponseEntity.ok(rateLimitService.update(clientId,rateLimit));
    }

    private String getClientIpAddress(HttpServletRequest request) {
        // The X-Forwarded-For header is used to capture the original client IP when the request passes through proxies or load balancers.
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");

        if (xForwardedForHeader != null && !xForwardedForHeader.isEmpty()) {
            // The client IP is the first IP in the list when multiple proxies are involved
            return xForwardedForHeader.split(",")[0].trim();
        }
        // If X-Forwarded-For header is not present, fall back to the standard method
        return request.getRemoteAddr();
    }
}
