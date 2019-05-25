package com.jag.zillow.region.dao;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.jag.zillow.exception.NonRetryableServiceException;
import com.jag.zillow.exception.RetryableServiceException;
import com.jag.zillow.region.model.City;
import com.jag.zillow.region.model.Neighborhood;
import com.jag.zillow.region.model.Neighborhood.NeighborhoodBuilder;
import lombok.RequiredArgsConstructor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@RequiredArgsConstructor
public class NeighborhoodReaderImpl implements NeighborhoodReader {

    private final Client client;
    private final NeighborhoodDeserializer deserializer;

    public static NeighborhoodReader instance() {
        return new NeighborhoodReaderImpl(ClientBuilder.newClient(), new NeighborhoodDeserializer());
    }

    @Override
    public Optional<Set<Neighborhood>> getNeighborhoods(City city) {
        WebTarget webTarget = client.target(buildUri(city, "neighborhood"));
        Response response = webTarget.request().get();
        int status = response.getStatus();
        if (status != 200) {
          handleError(status);
        }
        Set<Neighborhood> neighborhoods = deserializer.deserialize(response.readEntity(String.class));
        return neighborhoods.isEmpty() ? Optional.empty() : Optional.of(neighborhoods);
    }

    private void handleError(int errorCode) {
        switch (errorCode) {
            case 500:
            case 503:
            case 504:
                throw new RetryableServiceException("Failed with errorCode: " + errorCode);
            default:
                throw new NonRetryableServiceException("Failed with errorCode: " + errorCode);
        }
    }

    private java.net.URI buildUri(City city, String childType) {
        return UriBuilder.fromUri("http://www.zillow.com/webservice/GetRegionChildren.htm")
                .queryParam("zws-id", "X1-ZWz1h3p47a82kr_5upah")
                .queryParam("state", city.getState())
                .queryParam("city", city.getName())
                .queryParam("childtype", childType)
                .build();
    }

    static class NeighborhoodDeserializer {

        private final DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();

        Set<Neighborhood> deserialize(String xmlDocument) {
            try {
                DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
                Document document = documentBuilder.parse(new InputSource(new StringReader(xmlDocument)));
                NodeList regions = document.getElementsByTagName("region");
                Set<Neighborhood> neighborhoods = new HashSet<>(regions.getLength());
                for (int i = 1; i < regions.getLength(); i++) {
                    Node node = regions.item(i);
                    if (isElement(node)) {
                        Element element = (Element) node;
                        NodeList children = element.getChildNodes();
                        NeighborhoodBuilder neighborhoodBuilder = Neighborhood.builder();
                        for (int j = 1; j < children.getLength(); j++) {
                            Node child = children.item(j);
                            if (isElement(child)) {
                                Element childElement = (Element) child;
                                handleElement(childElement, neighborhoodBuilder);
                            }
                        }
                        neighborhoods.add(neighborhoodBuilder.build());
                    }
                }
                return neighborhoods;
            } catch (ParserConfigurationException | SAXException | IOException e) {
                throw new RuntimeException("Failed while parsing response\n" + xmlDocument, e);
            }
        }

        private void handleElement(Element element, NeighborhoodBuilder builder) {
            switch (element.getTagName()) {
                case "name":
                    builder.name(element.getTextContent().toUpperCase());
                    break;
                case "latitude":
                    builder.latitude(Double.parseDouble(element.getTextContent()));
                    break;
                case "longitude":
                    builder.longitude(Double.parseDouble(element.getTextContent()));
                    break;
            }
        }

        private boolean isElement(Node node) {
            return node.getNodeType() == Node.ELEMENT_NODE;
        }
    }
}
