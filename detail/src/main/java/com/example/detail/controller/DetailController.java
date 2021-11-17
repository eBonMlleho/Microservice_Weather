package com.example.detail.controller;

import com.example.detail.service.DetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DetailController {

    DetailService detailService;

    @Autowired
    public DetailController(DetailService detailService) {
        this.detailService = detailService;
    }

    @Value("${server.port}")
    private int serverPort;

    @GetMapping("/port")
    public ResponseEntity<?> getDetails() {
        return new ResponseEntity<>("detail service port is " + serverPort, HttpStatus.OK);
    }


    // test from weather service: http://192.168.0.7:8200/weather?city=london
    // test for this: http://192.168.0.7:8200/detail/getID?city=london
    @GetMapping("/getID")
    public Integer getCityID( @RequestParam(required = true) String city){
        return detailService.findCityIdByName(city);
    }


}
