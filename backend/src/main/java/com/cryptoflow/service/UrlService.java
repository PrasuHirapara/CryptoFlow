package com.cryptoflow.service;

public class UrlService {

    public static String getCoinsList(int page) {
        return "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&per_page=10&page=" + page;
    }

    public static String getMarketChart(String coinId, int days) {
        return "https://api.coingecko.com/api/v3/coins/" + coinId +"/market_chart?vs_currency=usd&days=" + days;
    }

    public static String getCoinDetails(String coinId) {
        return "https://api.coingecko.com/api/v3/coins/" + coinId;
    }

    public static String searchCoin(String keyword) {
        return "https://api.coingecko.com/api/v3/search?query=" + keyword;
    }

    public static String getTop50CoinsByMarketCap() {
        return "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&per_page=50&page=1";
    }

    public static String getTreadingCoins() {
        return "https://api.coingecko.com/api/v3/search/trending";
    }
}
