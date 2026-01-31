package com.sca.smartcampusbackend.service.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Weather API Service for external integration
 * Fetches weather data from OpenWeatherMap API
 * 
 * @since 1.0.0
 */
@Service
@Slf4j
public class WeatherService {

    private final RestTemplate restTemplate;

    @Value("${weather.api.key:demo}")
    private String apiKey;

    @Value("${weather.api.url:https://api.openweathermap.org/data/2.5/weather}")
    private String apiUrl;

    @Value("${weather.default.city:Delhi}")
    private String defaultCity;

    public WeatherService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Get current weather for the campus location
     * Results are cached for 30 minutes
     * 
     * @return WeatherResponse with current weather data
     */
    @Cacheable(value = "weather", key = "'campus'")
    public WeatherResponse getCampusWeather() {
        return getWeather(defaultCity);
    }

    /**
     * Get weather for a specific city
     * 
     * @param city City name
     * @return WeatherResponse with weather data
     */
    public WeatherResponse getWeather(String city) {
        try {
            String url = String.format("%s?q=%s&appid=%s&units=metric", apiUrl, city, apiKey);

            // If using demo key, return mock data
            if ("demo".equals(apiKey)) {
                log.info("Using mock weather data for city: {}", city);
                return getMockWeather(city);
            }

            WeatherApiResponse response = restTemplate.getForObject(url, WeatherApiResponse.class);

            if (response != null) {
                return WeatherResponse.builder()
                        .city(response.getName())
                        .temperature(response.getMain().getTemp())
                        .feelsLike(response.getMain().getFeelsLike())
                        .humidity(response.getMain().getHumidity())
                        .description(response.getWeather() != null && response.getWeather().length > 0
                                ? response.getWeather()[0].getDescription()
                                : "Unknown")
                        .icon(response.getWeather() != null && response.getWeather().length > 0
                                ? response.getWeather()[0].getIcon()
                                : "01d")
                        .windSpeed(response.getWind() != null ? response.getWind().getSpeed() : 0)
                        .build();
            }
        } catch (Exception e) {
            log.error("Failed to fetch weather for city: {}", city, e);
        }

        return getMockWeather(city);
    }

    /**
     * Returns mock weather data for demo/fallback
     */
    private WeatherResponse getMockWeather(String city) {
        return WeatherResponse.builder()
                .city(city)
                .temperature(25.0)
                .feelsLike(27.0)
                .humidity(65)
                .description("Partly cloudy")
                .icon("02d")
                .windSpeed(5.5)
                .build();
    }

    // Response DTOs

    @Data
    @lombok.Builder
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class WeatherResponse {
        private String city;
        private Double temperature;
        private Double feelsLike;
        private Integer humidity;
        private String description;
        private String icon;
        private Double windSpeed;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WeatherApiResponse {
        private String name;
        private MainData main;
        private WeatherData[] weather;
        private WindData wind;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class MainData {
            private Double temp;
            @JsonProperty("feels_like")
            private Double feelsLike;
            private Integer humidity;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class WeatherData {
            private String main;
            private String description;
            private String icon;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class WindData {
            private Double speed;
        }
    }
}
