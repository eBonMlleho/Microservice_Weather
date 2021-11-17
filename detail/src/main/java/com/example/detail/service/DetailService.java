package com.example.detail.service;


import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DetailService {
    Integer findCityIdByName(String city);
    List<Integer> findCityIdByNames(String city);
}
