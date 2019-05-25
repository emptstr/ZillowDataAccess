package com.jag.zillow.region.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Neighborhood {
    private final String name;
    private final double latitude;
    private final double longitude;
}
