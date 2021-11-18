package com.example.search.service;


import com.example.search.config.EndpointConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Service
public class WeatherServiceImpl implements WeatherService{

    private final RestTemplate restTemplate;
    private final ExecutorService threadPool;

    @Autowired
    public WeatherServiceImpl(RestTemplate getRestTemplate, ExecutorService threadPool) {
        this.restTemplate = getRestTemplate;
        this.threadPool = threadPool;
    }


    /**
     * for study purpose only: exmaple of three parameters
     */
    @Override
    @Retryable(include = IllegalAccessError.class)
//    @LoadBalanced
    public List<Map<String, Map>> findCityDataByNames(String city, String city2, String city3) {
//        public List<Map<String, Map>> findCityDataByNames(String city, String city2, String city3) {
        List<String> cityNames = new ArrayList<>();
        cityNames.add(city);

        if(!city2.equals("null")) {cityNames.add(city2);}
        if(!city3.equals("null")) {cityNames.add(city3);}

        /** *  use stream --- can run but not really multi-threading because stream is single thread */
//        return cityNames.stream()
//                .map(cityName -> CompletableFuture.supplyAsync(()-> findCityDataByName(cityName),threadPool))
//                .map(t->t.join()).collect(Collectors.toList());


        /**  updated version  using CompletableFuture.allOf */
        List<CompletableFuture<Map<String, Map>>> completableFutureMaps =  cityNames.stream()
                .map(cityName -> CompletableFuture.supplyAsync(()-> findCityDataByName(cityName),threadPool))
                .collect(Collectors.toList());

        CompletableFuture.allOf(completableFutureMaps.toArray(new CompletableFuture[0])).join();

        List<Map<String, Map>> res = new ArrayList();
        for(CompletableFuture<Map<String, Map>> future : completableFutureMaps){
            try {
                res.add(future.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return res;


        /** use for loop */
//        List<CompletableFuture<Map<String, Map>>> completableFutureMaps =  new ArrayList<>();
//        List<Map<String, Map>> res = new ArrayList();
//        for(String name: cityNames){
//            CompletableFuture<Map<String, Map>> future = CompletableFuture.supplyAsync(()-> findCityDataByName(name), threadPool);
//            completableFutureMaps.add(future);
//        }
//
//        CompletableFuture.allOf(completableFutureMaps.toArray(new CompletableFuture[0])).join();
//        for(CompletableFuture<Map<String, Map>> future : completableFutureMaps){
//            res.add((Map<String, Map>) future);
//        }
//        return res;
    }




    public List<Map<String, Map>> findCityDataByNamess(List<String> cities) {
        List<String> cityNames = new ArrayList<>();
        cityNames.addAll(cities);

        /**  updated version  using CompletableFuture.allOf */
        List<CompletableFuture<Map<String, Map>>> completableFutureMaps =  cityNames.stream()
                .map(cityName -> CompletableFuture.supplyAsync(()-> findCityDataByName(cityName),threadPool))
                .collect(Collectors.toList());

        CompletableFuture.allOf(completableFutureMaps.toArray(new CompletableFuture[0])).join();

        List<Map<String, Map>> res = new ArrayList();
        for(CompletableFuture<Map<String, Map>> future : completableFutureMaps){
            try {
                res.add(future.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    @Override
    @Retryable(include = IllegalAccessError.class)
    public Map<String, Map> findCityDataByName(String city) {
        Integer c = restTemplate.getForObject("http://192.168.0.7:8200/detail/getID?city="+city, Integer.class);
//        Integer c = restTemplate.getForObject("http://detail-service/detail/getID?city="+city, Integer.class);
        Map<String, Map> ans = findCityDataById(c);
        return ans;
    }


    /**
     * helper + testing purpose
     */
    @Override
    public Map<String, Map> findCityDataById(int id) {
        Map<String, Map> ans = restTemplate.getForObject(EndpointConfig.queryWeatherById + id, HashMap.class);
        return ans;
    }

}

/** * * * * * * * * * * * * * * * * * * * ** * * * * * * ** * * * * * * ** * * * * * * **/
/**
 *
 * 1. 创建一个 cached thread pool。 因为我们目前就3个 city name 最多 3 个 thread 去根据 id 拿 data
 *  我们需要     city name --> id  可以把 id 存在 list 里面，
 *      for i = 0 ~ i = 3
 *          completableFuture send request
 */




/**
 *  -> gateway -> eureka
 *       |
 *   weather-search -> hystrix(thread pool) -> 3rd party weather api
 *
 *
 *  circuit breaker(hystrix)
 * *  * *  * *  * *  * *  * *  * *  * *  * *  * *  * *  * *  * *  * *
 *   weather-search service should get city id from detail service
 *   and use multi-threading to query city's weather details
 *
 *   gateway
 *     |
 *  weather-service -> 3rd party api(id <-> weather)
 *    |
 *  detail-service -> 3rd party api (city <-> id)
 *
 *  failed situations:
 *      1. 3rd party api timeout -> retry + hystrix
 *      2. 3rd party api available time / rate limit
 *      3. security verification
 *  response
 *      1. no id -> error / empty
 *      2. large response -> pagination / file download (link / email)
 *  performance
 *      1. cache / db
 *
 *   gateway
 *     |
 *  weather-service -> cache(city - id - weather) (LFU)
 *    |
 *   DB (city - id - weather) <-> service <->  message queue  <-> scheduler <-> 3rd party api(city - id)
 *                                                                  |
 *                                                         update id - weather every 30 min
 *                                                         update city - id relation once per day
 *
 *  homework :
 *      deadline -> Wednesday midnight
 *      1. update detail service
 *          a. send request to 3rd party api -> get id by city
 *      2. update search service
 *          a. add ThreadPool
 *          b. send request to detail service -> get id by city
 *          c. use CompletableFuture send request to 3rd party api -> get weather by ids
 *          d. add retry feature
 */