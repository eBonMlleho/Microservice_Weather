package com.example.search.controller;

import com.example.search.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class WeatherController {

    private final WeatherService weatherService;

    @Value("${server.port}")
    private int randomServerPort;

    @Autowired
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    /**
     * this is for single city.
     */
//    @GetMapping("/weather")
//    public ResponseEntity<?> queryWeatherByCity(@RequestParam(required = true) String city) {
//        return new ResponseEntity<>(weatherService.findCityDataByName(city), HttpStatus.OK);
//    }

    /**
     * this is for three cities
     * test: http://localhost:8200/weather?city=london&city2=beijing&city3=shanghai
     */
//    @GetMapping("/weather")
//    public ResponseEntity<?> queryWeatherByCity(@RequestParam(required = true) String city,
//                                                @RequestParam(required = false, defaultValue = "null") String city2,
//                                                @RequestParam(required = false, defaultValue = "null") String city3) {
//        return new ResponseEntity<>(weatherService.findCityDataByNames(city, city2, city3), HttpStatus.OK);
//    }

    /**
     * this is for list of city
     * test: http://localhost:8200/weather?cities=london&cities=beijing&cities=shanghai
     */
    @GetMapping("/weather")
    public ResponseEntity<?> queryWeatherByCity(@RequestParam(required = true) List<String> cities) {
        return new ResponseEntity<>(weatherService.findCityDataByNamess(cities), HttpStatus.OK);
    }



    @GetMapping("/weather/{id}")
    public ResponseEntity<?> queryWeatherByCity(@PathVariable int id) {
        return new ResponseEntity<Map>(weatherService.findCityDataById(id), HttpStatus.OK);
    }

    @GetMapping("/weather/port")
    public ResponseEntity<?> queryWeatherByCity() {
        return new ResponseEntity<>("weather service + " + randomServerPort, HttpStatus.OK);
    }
}
