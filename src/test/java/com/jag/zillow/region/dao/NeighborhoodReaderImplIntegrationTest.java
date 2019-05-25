package com.jag.zillow.region.dao;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.Set;

import com.jag.zillow.region.model.City;
import com.jag.zillow.region.model.Neighborhood;
import org.junit.jupiter.api.Test;

public class NeighborhoodReaderImplIntegrationTest {
    @Test
    public void shouldReturnNeighborhoods() {
        NeighborhoodReader reader = NeighborhoodReaderImpl.instance();
        Optional<Set<Neighborhood>> neighborhoods = reader.getNeighborhoods(new City("Seattle", "Washington", "America/Los_Angeles"));
        assertTrue(neighborhoods::isPresent);
        assertTrue(neighborhoods.get().size() > 0);
    }

    @Test
    public void shouldReturnEmpty() {
        NeighborhoodReader reader = NeighborhoodReaderImpl.instance();
        Optional<Set<Neighborhood>> neighborhoods = reader.getNeighborhoods(new City("Wampus", "Washington", "America/Los_Angeles"));
        assertFalse(neighborhoods::isPresent);
    }
}
