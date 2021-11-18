package com.example.search.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface WeatherService {
    Map<String, Map> findCityDataByName(String city);

    /** for study purpose only: multiple parameters
     */
    List<Map<String, Map>> findCityDataByNames(String city, String city2, String city3);
    Map<String, Map> findCityDataById(int id);

    /** for list of parameters
     */
    List<Map<String, Map>> findCityDataByNamess(List<String> cities); //
}
