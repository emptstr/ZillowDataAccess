package com.jag.zillow.region.model;

import lombok.Value;

@Value
public class City {
    private final String name;
    private final String state;
    private final String timezone;
}
