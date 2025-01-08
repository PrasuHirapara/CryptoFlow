package com.cryptoflow.controller;

import com.cryptoflow.entity.Coin;
import com.cryptoflow.service.CoinService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coins")
public class CoinController {

    @Autowired
    private CoinService coinService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<List<Coin>> getCoins(@RequestParam("page") int page) throws Exception {
        List<Coin> coins = coinService.getCoinsList(page);

        return new ResponseEntity<>(coins, HttpStatus.ACCEPTED);
    }

    @GetMapping("/{coinId}/chart")
    public ResponseEntity<JsonNode> getMarketChart(
            @PathVariable String coinId,
            @RequestParam("days") int days
    ) throws Exception {
        String res = coinService.getMarketChart(coinId, days);

        JsonNode node = objectMapper.readTree(res);

        return new ResponseEntity<>(node, HttpStatus.ACCEPTED);
    }

    @GetMapping("/search")
    public ResponseEntity<JsonNode> searchCoin(@RequestParam("q") String keyword) throws Exception {
        String coin = coinService.searchCoin(keyword);

        JsonNode node = objectMapper.readTree(coin);

        return new ResponseEntity<>(node, HttpStatus.ACCEPTED);
    }

    @GetMapping("/top50")
    public ResponseEntity<JsonNode> getTop50CoinsByMarketCapRank() throws Exception {
        String coin = coinService.getTop50CoinsByMarketCap();

        JsonNode node = objectMapper.readTree(coin);

        return new ResponseEntity<>(node, HttpStatus.ACCEPTED);
    }

    @GetMapping("/trending")
    public ResponseEntity<JsonNode> getTrendingCoin() throws Exception {
        String coin = coinService.getTrendingCoins();

        JsonNode node = objectMapper.readTree(coin);

        return new ResponseEntity<>(node, HttpStatus.ACCEPTED);
    }

    @GetMapping("/details/{coinId}")
    public ResponseEntity<JsonNode> getCoinList(@PathVariable String coinId) throws Exception {
        String coin = coinService.getCoinDetails(coinId);

        JsonNode node = objectMapper.readTree(coin);

        return new ResponseEntity<>(node, HttpStatus.ACCEPTED);
    }
}
