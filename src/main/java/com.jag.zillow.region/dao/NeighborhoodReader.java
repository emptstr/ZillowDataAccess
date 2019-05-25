package com.jag.zillow.region.dao;

import java.util.Optional;
import java.util.Set;

import com.jag.zillow.region.model.City;
import com.jag.zillow.region.model.Neighborhood;

public interface NeighborhoodReader {
    Optional<Set<Neighborhood>> getNeighborhoods(City city);
}
