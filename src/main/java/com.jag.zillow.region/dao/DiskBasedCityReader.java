package com.jag.zillow.region.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import com.jag.zillow.region.model.City;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DiskBasedCityReader implements CityReader {
    private static final int CITY = 0;
    private static final int STATE = 3;
    private static final int TIMEZONE = 13;
    private final Map<String, City> nameToCities = createNameToCityMap();

    @Override
    public Optional<City> getByName(String name) {
        return Optional.ofNullable(nameToCities.get(name));
    }

    private  Map<String, City> createNameToCityMap() {
        InputStream cityStream = getClass().getClassLoader().getResourceAsStream("us-cities.csv.gz");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(cityStream)))) {
            return reader.lines().map(line -> {
                String[] tokens = line.split(",");
                return new City(tokens[CITY], tokens[STATE], tokens[TIMEZONE]);
            }).collect(Collectors.toMap(City::getName, Function.identity()));
        } catch (IOException e) {
            throw new RuntimeException("Failed while reading cities", e);
        }
    }
}
