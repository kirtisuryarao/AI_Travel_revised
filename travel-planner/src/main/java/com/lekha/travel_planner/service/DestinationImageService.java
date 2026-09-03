package com.lekha.travel_planner.service;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.lekha.travel_planner.dto.DestinationPhotosResponse;

@Service
public class DestinationImageService {

    private static final Logger logger = LoggerFactory.getLogger(DestinationImageService.class);

    private static final String USER_AGENT = "TravelPlanner/1.0 (https://github.com/travel-planner; educational)";

    private final RestTemplate restTemplate = new RestTemplate();

    private static final List<String> TRAVEL_FALLBACKS = List.of(
        "https://images.unsplash.com/photo-1488646953014-85cb44e25828?auto=format&fit=crop&w=1200&q=80",
        "https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?auto=format&fit=crop&w=1200&q=80",
        "https://images.unsplash.com/photo-1476514525535-07fb3b4eae5f?auto=format&fit=crop&w=1200&q=80",
        "https://images.unsplash.com/photo-1501785888041-af3ef285b470?auto=format&fit=crop&w=1200&q=80",
        "https://images.unsplash.com/photo-1502920917128-1aa500764cbd?auto=format&fit=crop&w=1200&q=80",
        "https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1200&q=80"
    );

    public DestinationPhotosResponse getPhotos(String destination) {
        String clean = cleanDestination(destination);
        Set<String> urls = new LinkedHashSet<>();

        String hero = fetchSummaryImage(clean);
        if (hero != null) {
            urls.add(hero);
        }

        urls.addAll(fetchSearchImages(clean));
        urls.addAll(fetchSearchImages(clean + " city"));
        urls.addAll(fetchSearchImages(clean + " tourism"));

        List<String> gallery = new ArrayList<>(urls);
        if (gallery.isEmpty()) {
            gallery = fallbackImages(clean);
            hero = gallery.get(0);
        } else if (hero == null) {
            hero = gallery.get(0);
        }

        return new DestinationPhotosResponse(clean, hero, gallery.stream().limit(6).toList());
    }

    @SuppressWarnings("unchecked")
    private String fetchSummaryImage(String destination) {
        try {
            String title = URLEncoder.encode(destination.replace(' ', '_'), StandardCharsets.UTF_8);
            URI uri = URI.create("https://en.wikipedia.org/api/rest_v1/page/summary/" + title);
            Map<String, Object> response = getJson(uri);
            if (response == null) {
                return null;
            }
            return extractImageUrl(response);
        } catch (Exception e) {
            logger.debug("Wikipedia summary failed for {}: {}", destination, e.getMessage());
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private List<String> fetchSearchImages(String query) {
        List<String> images = new ArrayList<>();
        try {
            String url = UriComponentsBuilder
                .fromHttpUrl("https://en.wikipedia.org/w/api.php")
                .queryParam("action", "query")
                .queryParam("generator", "search")
                .queryParam("gsrsearch", query)
                .queryParam("gsrlimit", "8")
                .queryParam("prop", "pageimages")
                .queryParam("piprop", "thumbnail")
                .queryParam("pithumbsize", "900")
                .queryParam("format", "json")
                .toUriString();

            Map<String, Object> response = getJson(URI.create(url));
            if (response == null || !response.containsKey("query")) {
                return images;
            }

            Map<String, Object> queryResult = (Map<String, Object>) response.get("query");
            Object pagesObj = queryResult.get("pages");
            if (!(pagesObj instanceof Map<?, ?> pages)) {
                return images;
            }

            for (Object pageValue : pages.values()) {
                if (pageValue instanceof Map<?, ?> page) {
                    String imageUrl = extractImageUrl(page);
                    if (imageUrl != null) {
                        images.add(imageUrl);
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("Wikipedia search failed for {}: {}", query, e.getMessage());
        }
        return images;
    }

    @SuppressWarnings("unchecked")
    private String extractImageUrl(Map<?, ?> data) {
        Object thumbnail = data.get("thumbnail");
        if (thumbnail instanceof Map<?, ?> thumbMap) {
            Object source = thumbMap.get("source");
            if (source instanceof String url && !url.isBlank()) {
                return url;
            }
        }
        Object original = data.get("originalimage");
        if (original instanceof Map<?, ?> originalMap) {
            Object source = originalMap.get("source");
            if (source instanceof String url && !url.isBlank()) {
                return url;
            }
        }
        return null;
    }

    private Map<String, Object> getJson(URI uri) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.USER_AGENT, USER_AGENT);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(uri, HttpMethod.GET, entity, Map.class);
        return response.getBody();
    }

    private List<String> fallbackImages(String destination) {
        int seed = Math.abs(destination.hashCode());
        List<String> images = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            images.add(TRAVEL_FALLBACKS.get((seed + i) % TRAVEL_FALLBACKS.size()));
        }
        return images;
    }

    private String cleanDestination(String destination) {
        if (destination == null || destination.isBlank()) {
            return "Travel";
        }
        String clean = destination.split("\\(")[0].trim();
        if (clean.contains(",")) {
            clean = clean.split(",")[0].trim();
        }
        return clean;
    }
}
