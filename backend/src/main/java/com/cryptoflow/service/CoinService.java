package com.cryptoflow.service;

import com.cryptoflow.entity.Coin;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CoinService {

    List<Coin> getCoinsList(int page) throws Exception;

    String getMarketChart(String coinId, int days) throws Exception;

//    from api
    String getCoinDetails(String coinId) throws Exception;

//    from local database
    Coin findById(String id) throws Exception;

    String searchCoin(String keyword) throws Exception;

    String getTop50CoinsByMarketCap() throws Exception;

    String getTrendingCoins() throws Exception;
}
