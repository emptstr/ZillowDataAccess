package com.jag.zillow.region.dao;

import java.util.Optional;

import com.jag.zillow.region.model.City;

public interface CityReader {
    Optional<City> getByName(String name);
}
