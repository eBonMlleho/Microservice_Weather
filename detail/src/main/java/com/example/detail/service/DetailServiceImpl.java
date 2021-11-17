package com.example.detail.service;


import com.example.detail.pojo.City;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.websocket.EndpointConfig;
import java.util.ArrayList;
import java.util.List;

@Service
public class DetailServiceImpl implements DetailService {

    private final RestTemplate restTemplate;

    @Autowired
    public DetailServiceImpl(RestTemplate getRestTemplate) {
        this.restTemplate = getRestTemplate;
    }

    @Override
    public Integer findCityIdByName(String c){
//        City city = restTemplate.getForObject("https://www.metaweather.com/api/location/search/?query="+c, City.class);
//        return city.getWoeid();

        City[] cities = restTemplate.getForObject("https://www.metaweather.com/api/location/search/?query="+c, City[].class);
//        List<Integer> ans = new ArrayList<>();
//        for(City city: cities) {
//            if(c != null && city.getWoeid() != null) {
//                ans.add(city.getWoeid());
//            }
//        }
        return cities[0].getWoeid();
    }

    @Override
    public List<Integer> findCityIdByNames(String city) {
        return null;
    }

}
