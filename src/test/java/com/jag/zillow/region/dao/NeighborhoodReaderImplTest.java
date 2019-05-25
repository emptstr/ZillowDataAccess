package com.jag.zillow.region.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import com.jag.zillow.region.dao.NeighborhoodReaderImpl.NeighborhoodDeserializer;
import com.jag.zillow.region.model.Neighborhood;
import org.junit.jupiter.api.Test;

public class NeighborhoodReaderImplTest {
    @Test
    public void shouldDeserializeResponse() {
        try {
            String xml = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("seattle-neighborhoods.xml").getPath())), StandardCharsets.UTF_8);
            NeighborhoodDeserializer deserializer = new NeighborhoodDeserializer();
            Set<Neighborhood> actualResult = deserializer.deserialize(xml);
            Neighborhood expectedResult = Neighborhood.builder()
                    .name("ALKI")
                    .latitude(47.56955)
                    .longitude(-122.397729)
                    .build();
            assertEquals(1, actualResult.size());
            assertTrue(actualResult.contains(expectedResult));
        } catch (Exception e) {
            throw new RuntimeException("Failed while parsing response", e);
        }
    }
}