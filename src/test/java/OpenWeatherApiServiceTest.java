import dto.LocationDTO;
import dto.WeatherDTO;
import exception.server.OpenWeatherApiInteractionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.OpenWeatherApiService;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OpenWeatherApiServiceTest {
    private OpenWeatherApiService openWeatherApiService;
    private HttpClient httpClientMock;
    private HttpResponse<String> httpResponseMock;

    @BeforeEach
    public void setUp() {
        httpClientMock = mock(HttpClient.class);
        httpResponseMock = mock(HttpResponse.class);
        openWeatherApiService = new OpenWeatherApiService();

        try {
            var httpClientField = OpenWeatherApiService.class.getDeclaredField("httpClient");
            httpClientField.setAccessible(true);
            httpClientField.set(openWeatherApiService, httpClientMock);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetWeatherDataByLocationDTOs_SuccessfulResponse() throws IOException, InterruptedException {
        List<LocationDTO> locations = List.of(
                new LocationDTO("Kazan", "Tatarstan", 55.7823547, 49.1242266),
                new LocationDTO("Yekaterinburg", "Sverdlovsk Oblast", 56.839104, 60.60825)
        );

        String yekaterinburgResponse = "{\"coord\":{\"lon\":60.6083,\"lat\":56.8391},\"weather\":[{\"id\":741,\"main\":\"Fog\",\"description\":\"fog\",\"icon\":\"50n\"}],\"base\":\"stations\",\"main\":{\"temp\":6.84,\"feels_like\":6.84,\"temp_min\":6.84,\"temp_max\":6.84,\"pressure\":1022,\"humidity\":100,\"sea_level\":1022,\"grnd_level\":988},\"visibility\":4400,\"wind\":{\"speed\":0,\"deg\":0},\"clouds\":{\"all\":20},\"dt\":1724708510,\"sys\":{\"type\":1,\"id\":8985,\"country\":\"RU\",\"sunrise\":1724719825,\"sunset\":1724771288},\"timezone\":18000,\"id\":1494346,\"name\":\"Pos?lok Rabochiy\",\"cod\":200}";
        String kazanResponse = "{\"coord\":{\"lon\":49.1242,\"lat\":55.7824},\"weather\":[{\"id\":803,\"main\":\"Clouds\",\"description\":\"broken clouds\",\"icon\":\"04n\"}],\"base\":\"stations\",\"main\":{\"temp\":18.4,\"feels_like\":18.33,\"temp_min\":18.4,\"temp_max\":18.4,\"pressure\":1023,\"humidity\":78,\"sea_level\":1023,\"grnd_level\":1010},\"visibility\":10000,\"wind\":{\"speed\":1.1,\"deg\":92,\"gust\":1.14},\"clouds\":{\"all\":56},\"dt\":1724709223,\"sys\":{\"type\":1,\"id\":9038,\"country\":\"RU\",\"sunrise\":1724722747,\"sunset\":1724773877},\"timezone\":10800,\"id\":551487,\"name\":\"Kazan’\",\"cod\":200}";
        when(httpResponseMock.body()).thenReturn(yekaterinburgResponse).thenReturn(kazanResponse);
        when(httpResponseMock.statusCode()).thenReturn(200);

        when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponseMock);

        List<WeatherDTO> result = openWeatherApiService.getWeatherData(locations);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(Double.valueOf(56.8391), result.getFirst().getCoord().getLat());
        assertEquals(Double.valueOf(60.6083), result.getFirst().getCoord().getLon());

        assertEquals(Double.valueOf(55.7824), result.getLast().getCoord().getLat());
        assertEquals(Double.valueOf(49.1242), result.getLast().getCoord().getLon());
    }

    @Test
    void testGetWeatherDataByLocationName_SuccessfulResponse() throws IOException, InterruptedException {
        String locationName = "казань";
        String geocodingApiResponse = "[{\"name\":\"Kazan\",\"local_names\":{\"sk\":\"Kaza?\",\"az\":\"Kazan\",\"ro\":\"Kazan\",\"ascii\":\"Kazan\",\"ca\":\"districte urb? de Kazan\",\"hu\":\"Kazany\",\"uz\":\"Qozon\",\"he\":\"?????\",\"en\":\"Kazan\",\"tk\":\"Kazan\",\"uk\":\"Казань\",\"kn\":\"???????\",\"id\":\"Kazan\",\"kv\":\"Казан\",\"cs\":\"Kaza?\",\"fi\":\"Kazan\",\"sr\":\"Казањ\",\"pt\":\"Caz?\",\"os\":\"Хъазан\",\"es\":\"Kaz?n\",\"cv\":\"Хусан\",\"da\":\"Kazan\",\"ko\":\"??\",\"kk\":\"?азан\",\"et\":\"Kaasan\",\"lt\":\"Kazan?s miesto apygarda\",\"fr\":\"Kazan\",\"nl\":\"Kazan\",\"de\":\"Kasan\",\"it\":\"Kazan'\",\"tr\":\"Kazan\",\"oc\":\"Kazan\",\"pl\":\"Kaza?\",\"ka\":\"??????\",\"eo\":\"Kazano\",\"hy\":\"?????\",\"ba\":\"?азан\",\"zh\":\"??\",\"hr\":\"Kazanj\",\"feature_name\":\"Kazan\",\"ru\":\"городской округ Казань\",\"hi\":\"??????\",\"ar\":\"?????\",\"tt\":\"Казан ш???р б?лгесе\",\"lv\":\"Kaza?a\",\"ja\":\"???\"},\"lat\":55.7823547,\"lon\":49.1242266,\"country\":\"RU\",\"state\":\"Tatarstan\"}]";
        String openWeatherApiResponse = "{\"coord\":{\"lon\":49.1242,\"lat\":55.7824},\"weather\":[{\"id\":803,\"main\":\"Clouds\",\"description\":\"broken clouds\",\"icon\":\"04n\"}],\"base\":\"stations\",\"main\":{\"temp\":18.4,\"feels_like\":18.33,\"temp_min\":18.4,\"temp_max\":18.4,\"pressure\":1023,\"humidity\":78,\"sea_level\":1023,\"grnd_level\":1010},\"visibility\":10000,\"wind\":{\"speed\":1.1,\"deg\":92,\"gust\":1.14},\"clouds\":{\"all\":56},\"dt\":1724709223,\"sys\":{\"type\":1,\"id\":9038,\"country\":\"RU\",\"sunrise\":1724722747,\"sunset\":1724773877},\"timezone\":10800,\"id\":551487,\"name\":\"Kazan’\",\"cod\":200}";
        when(httpResponseMock.body()).thenReturn(geocodingApiResponse).thenReturn(openWeatherApiResponse);
        when(httpResponseMock.statusCode()).thenReturn(200);

        when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponseMock);

        List<WeatherDTO> result = openWeatherApiService.getWeatherData(locationName);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Double.valueOf(55.7824), result.getLast().getCoord().getLat());
        assertEquals(Double.valueOf(49.1242), result.getLast().getCoord().getLon());
    }

    @Test
    void testGetWeatherDataByLocationDTOs_ThrowsAnException() throws IOException, InterruptedException {
        List<LocationDTO> locations = List.of(
                new LocationDTO("Yekaterinburg", "Sverdlovsk Oblast", 56.839104, 60.60825)
        );

        String yekaterinburgResponse = "{\"coord\":{\"lon\":60.6083,\"lat\":56.8391},\"weather\":[{\"id\":741,\"main\":\"Fog\",\"description\":\"fog\",\"icon\":\"50n\"}],\"base\":\"stations\",\"main\":{\"temp\":6.84,\"feels_like\":6.84,\"temp_min\":6.84,\"temp_max\":6.84,\"pressure\":1022,\"humidity\":100,\"sea_level\":1022,\"grnd_level\":988},\"visibility\":4400,\"wind\":{\"speed\":0,\"deg\":0},\"clouds\":{\"all\":20},\"dt\":1724708510,\"sys\":{\"type\":1,\"id\":8985,\"country\":\"RU\",\"sunrise\":1724719825,\"sunset\":1724771288},\"timezone\":18000,\"id\":1494346,\"name\":\"Pos?lok Rabochiy\",\"cod\":200}";
        when(httpResponseMock.body()).thenReturn(yekaterinburgResponse);
        when(httpResponseMock.statusCode()).thenReturn(400).thenReturn(401).thenReturn(402).thenReturn(429).thenReturn(500).thenReturn(501).thenReturn(503);

        when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponseMock);

        for (int i = 0; i < 7; i++) {
            assertThatThrownBy(() -> openWeatherApiService.getWeatherData(locations)).isInstanceOf(OpenWeatherApiInteractionException.class);
        }
    }

    @Test
    void testGetWeatherDataByLocationName_ThrowsAnException() throws IOException, InterruptedException {
        String locationName = "казань";
        String geocodingApiResponse = "[{\"name\":\"Kazan\",\"local_names\":{\"sk\":\"Kaza?\",\"az\":\"Kazan\",\"ro\":\"Kazan\",\"ascii\":\"Kazan\",\"ca\":\"districte urb? de Kazan\",\"hu\":\"Kazany\",\"uz\":\"Qozon\",\"he\":\"?????\",\"en\":\"Kazan\",\"tk\":\"Kazan\",\"uk\":\"Казань\",\"kn\":\"???????\",\"id\":\"Kazan\",\"kv\":\"Казан\",\"cs\":\"Kaza?\",\"fi\":\"Kazan\",\"sr\":\"Казањ\",\"pt\":\"Caz?\",\"os\":\"Хъазан\",\"es\":\"Kaz?n\",\"cv\":\"Хусан\",\"da\":\"Kazan\",\"ko\":\"??\",\"kk\":\"?азан\",\"et\":\"Kaasan\",\"lt\":\"Kazan?s miesto apygarda\",\"fr\":\"Kazan\",\"nl\":\"Kazan\",\"de\":\"Kasan\",\"it\":\"Kazan'\",\"tr\":\"Kazan\",\"oc\":\"Kazan\",\"pl\":\"Kaza?\",\"ka\":\"??????\",\"eo\":\"Kazano\",\"hy\":\"?????\",\"ba\":\"?азан\",\"zh\":\"??\",\"hr\":\"Kazanj\",\"feature_name\":\"Kazan\",\"ru\":\"городской округ Казань\",\"hi\":\"??????\",\"ar\":\"?????\",\"tt\":\"Казан ш???р б?лгесе\",\"lv\":\"Kaza?a\",\"ja\":\"???\"},\"lat\":55.7823547,\"lon\":49.1242266,\"country\":\"RU\",\"state\":\"Tatarstan\"}]";
        String openWeatherApiResponse = "{\"coord\":{\"lon\":49.1242,\"lat\":55.7824},\"weather\":[{\"id\":803,\"main\":\"Clouds\",\"description\":\"broken clouds\",\"icon\":\"04n\"}],\"base\":\"stations\",\"main\":{\"temp\":18.4,\"feels_like\":18.33,\"temp_min\":18.4,\"temp_max\":18.4,\"pressure\":1023,\"humidity\":78,\"sea_level\":1023,\"grnd_level\":1010},\"visibility\":10000,\"wind\":{\"speed\":1.1,\"deg\":92,\"gust\":1.14},\"clouds\":{\"all\":56},\"dt\":1724709223,\"sys\":{\"type\":1,\"id\":9038,\"country\":\"RU\",\"sunrise\":1724722747,\"sunset\":1724773877},\"timezone\":10800,\"id\":551487,\"name\":\"Kazan’\",\"cod\":200}";
        when(httpResponseMock.body()).thenReturn(geocodingApiResponse).thenReturn(openWeatherApiResponse);
        when(httpResponseMock.statusCode()).thenReturn(400).thenReturn(401).thenReturn(402).thenReturn(429).thenReturn(500).thenReturn(501).thenReturn(503);

        when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponseMock);

        for (int i = 0; i < 7; i++) {
            assertThatThrownBy(() -> openWeatherApiService.getWeatherData(locationName)).isInstanceOf(OpenWeatherApiInteractionException.class);
        }
    }
}
