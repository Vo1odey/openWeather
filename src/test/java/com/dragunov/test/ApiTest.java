package com.dragunov.test;

import com.dragunov.openweather.exceptions.api.ApiKeyException;
import com.dragunov.openweather.exceptions.api.BadRequestException;
import com.dragunov.openweather.exceptions.api.LocationInfoException;
import com.dragunov.openweather.exceptions.api.LocationNotFoundException;
import com.dragunov.openweather.service.ApiService;
import com.dragunov.openweather.service.dto.LocationDTO;
import com.dragunov.openweather.service.dto.WeatherDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

public class ApiTest {
    static ApiService apiService = Mockito.mock(ApiService.class);
    String locationNames = "[\n" +
            "  {\n" +
            "    \"name\": \"Moscow\",\n" +
            "    \"local_names\": {\n" +
            "      \"lg\": \"Moosko\",\n" +
            "      \"cu\": \"Москъва\",\n" +
            "      \"kk\": \"Мәскеу\",\n" +
            "      \"uz\": \"Moskva\",\n" +
            "      \"co\": \"Moscù\",\n" +
            "      \"nb\": \"Moskva\",\n" +
            "      \"ka\": \"მოსკოვი\",\n" +
            "      \"nl\": \"Moskou\",\n" +
            "      \"en\": \"Moscow\",\n" +
            "      \"ab\": \"Москва\",\n" +
            "      \"am\": \"ሞስኮ\",\n" +
            "      \"fo\": \"Moskva\",\n" +
            "      \"ascii\": \"Moscow\",\n" +
            "      \"sw\": \"Moscow\",\n" +
            "      \"sq\": \"Moska\",\n" +
            "      \"gl\": \"Moscova - Москва\",\n" +
            "      \"tk\": \"Moskwa\",\n" +
            "      \"pl\": \"Moskwa\",\n" +
            "      \"lt\": \"Maskva\",\n" +
            "      \"oc\": \"Moscòu\",\n" +
            "      \"dz\": \"མོསི་ཀོ\",\n" +
            "      \"ga\": \"Moscó\",\n" +
            "      \"it\": \"Mosca\",\n" +
            "      \"eu\": \"Mosku\",\n" +
            "      \"ml\": \"മോസ്കോ\",\n" +
            "      \"tl\": \"Moscow\",\n" +
            "      \"uk\": \"Москва\",\n" +
            "      \"tg\": \"Маскав\",\n" +
            "      \"hy\": \"Մոսկվա\",\n" +
            "      \"he\": \"מוסקווה\",\n" +
            "      \"mr\": \"मॉस्को\",\n" +
            "      \"hi\": \"मास्को\",\n" +
            "      \"es\": \"Moscú\",\n" +
            "      \"ro\": \"Moscova\",\n" +
            "      \"ca\": \"Moscou\",\n" +
            "      \"gv\": \"Moscow\",\n" +
            "      \"sg\": \"Moscow\",\n" +
            "      \"bo\": \"མོ་སི་ཁོ།\",\n" +
            "      \"te\": \"మాస్కో\",\n" +
            "      \"fy\": \"Moskou\",\n" +
            "      \"kv\": \"Мӧскуа\",\n" +
            "      \"wa\": \"Moscou\",\n" +
            "      \"lv\": \"Maskava\",\n" +
            "      \"fa\": \"مسکو\",\n" +
            "      \"gn\": \"Mosku\",\n" +
            "      \"jv\": \"Moskwa\",\n" +
            "      \"sl\": \"Moskva\",\n" +
            "      \"ss\": \"Moscow\",\n" +
            "      \"sk\": \"Moskva\",\n" +
            "      \"ku\": \"Moskow\",\n" +
            "      \"feature_name\": \"Moscow\",\n" +
            "      \"sc\": \"Mosca\",\n" +
            "      \"ch\": \"Moscow\",\n" +
            "      \"ps\": \"مسکو\",\n" +
            "      \"zu\": \"IMoskwa\",\n" +
            "      \"is\": \"Moskva\",\n" +
            "      \"ug\": \"Moskwa\",\n" +
            "      \"eo\": \"Moskvo\",\n" +
            "      \"st\": \"Moscow\",\n" +
            "      \"yi\": \"מאסקווע\",\n" +
            "      \"vo\": \"Moskva\",\n" +
            "      \"mt\": \"Moska\",\n" +
            "      \"an\": \"Moscú\",\n" +
            "      \"ie\": \"Moskwa\",\n" +
            "      \"sv\": \"Moskva\",\n" +
            "      \"cv\": \"Мускав\",\n" +
            "      \"na\": \"Moscow\",\n" +
            "      \"fi\": \"Moskova\",\n" +
            "      \"be\": \"Масква\",\n" +
            "      \"hu\": \"Moszkva\",\n" +
            "      \"ja\": \"モスクワ\",\n" +
            "      \"sr\": \"Москва\",\n" +
            "      \"af\": \"Moskou\",\n" +
            "      \"so\": \"Moskow\",\n" +
            "      \"ia\": \"Moscova\",\n" +
            "      \"ru\": \"Москва\",\n" +
            "      \"et\": \"Moskva\",\n" +
            "      \"li\": \"Moskou\",\n" +
            "      \"id\": \"Moskwa\",\n" +
            "      \"la\": \"Moscua\",\n" +
            "      \"os\": \"Мæскуы\",\n" +
            "      \"kg\": \"Moskva\",\n" +
            "      \"za\": \"Moscow\",\n" +
            "      \"mn\": \"Москва\",\n" +
            "      \"de\": \"Moskau\",\n" +
            "      \"iu\": \"ᒨᔅᑯ\",\n" +
            "      \"kn\": \"ಮಾಸ್ಕೋ\",\n" +
            "      \"tt\": \"Мәскәү\",\n" +
            "      \"vi\": \"Mát-xcơ-va\",\n" +
            "      \"gd\": \"Moscobha\",\n" +
            "      \"mg\": \"Moskva\",\n" +
            "      \"bs\": \"Moskva\",\n" +
            "      \"sh\": \"Moskva\",\n" +
            "      \"nn\": \"Moskva\",\n" +
            "      \"my\": \"မော်စကိုမြို့\",\n" +
            "      \"ko\": \"모스크바\",\n" +
            "      \"sm\": \"Moscow\",\n" +
            "      \"ak\": \"Moscow\",\n" +
            "      \"ay\": \"Mosku\",\n" +
            "      \"su\": \"Moskwa\",\n" +
            "      \"br\": \"Moskov\",\n" +
            "      \"tr\": \"Moskova\",\n" +
            "      \"yo\": \"Mọsko\",\n" +
            "      \"se\": \"Moskva\",\n" +
            "      \"io\": \"Moskva\",\n" +
            "      \"ln\": \"Moskú\",\n" +
            "      \"cs\": \"Moskva\",\n" +
            "      \"ar\": \"موسكو\",\n" +
            "      \"wo\": \"Mosku\",\n" +
            "      \"da\": \"Moskva\",\n" +
            "      \"ta\": \"மாஸ்கோ\",\n" +
            "      \"zh\": \"莫斯科\",\n" +
            "      \"kw\": \"Moskva\",\n" +
            "      \"bi\": \"Moskow\",\n" +
            "      \"hr\": \"Moskva\",\n" +
            "      \"av\": \"Москва\",\n" +
            "      \"bn\": \"মস্কো\",\n" +
            "      \"ur\": \"ماسکو\",\n" +
            "      \"ce\": \"Москох\",\n" +
            "      \"fr\": \"Moscou\",\n" +
            "      \"ky\": \"Москва\",\n" +
            "      \"th\": \"มอสโก\",\n" +
            "      \"dv\": \"މޮސްކޯ\",\n" +
            "      \"bg\": \"Москва\",\n" +
            "      \"pt\": \"Moscou\",\n" +
            "      \"ms\": \"Moscow\",\n" +
            "      \"cy\": \"Moscfa\",\n" +
            "      \"qu\": \"Moskwa\",\n" +
            "      \"no\": \"Moskva\",\n" +
            "      \"kl\": \"Moskva\",\n" +
            "      \"ht\": \"Moskou\",\n" +
            "      \"el\": \"Μόσχα\",\n" +
            "      \"ty\": \"Moscou\",\n" +
            "      \"mi\": \"Mohikau\",\n" +
            "      \"mk\": \"Москва\",\n" +
            "      \"az\": \"Moskva\",\n" +
            "      \"ba\": \"Мәскәү\"\n" +
            "    },\n" +
            "    \"lat\": 55.7504461,\n" +
            "    \"lon\": 37.6174943,\n" +
            "    \"country\": \"RU\",\n" +
            "    \"state\": \"Moscow\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"Moskow\",\n" +
            "    \"lat\": 58.9420786,\n" +
            "    \"lon\": 11.9692108,\n" +
            "    \"country\": \"SE\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"Mośki\",\n" +
            "    \"local_names\": {\n" +
            "      \"pl\": \"Mośki\"\n" +
            "    },\n" +
            "    \"lat\": 49.8119444,\n" +
            "    \"lon\": 20.5144444,\n" +
            "    \"country\": \"PL\",\n" +
            "    \"state\": \"Lesser Poland Voivodeship\"\n" +
            "  }\n" +
            "]";
    String key = "18c261cc8f1e9cbcdf34e23e088dd57f";
    String response = "{\n" +
            "  \"coord\": {\n" +
            "    \"lon\": 37.9,\n" +
            "    \"lat\": 59.1333\n" +
            "  },\n" +
            "  \"weather\": [\n" +
            "    {\n" +
            "      \"id\": 801,\n" +
            "      \"main\": \"Clouds\",\n" +
            "      \"description\": \"few clouds\",\n" +
            "      \"icon\": \"02d\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"base\": \"stations\",\n" +
            "  \"main\": {\n" +
            "    \"temp\": 275.05,\n" +
            "    \"feels_like\": 271.2,\n" +
            "    \"temp_min\": 275.05,\n" +
            "    \"temp_max\": 275.05,\n" +
            "    \"pressure\": 1031,\n" +
            "    \"humidity\": 69\n" +
            "  },\n" +
            "  \"visibility\": 10000,\n" +
            "  \"wind\": {\n" +
            "    \"speed\": 4,\n" +
            "    \"deg\": 150\n" +
            "  },\n" +
            "  \"clouds\": {\n" +
            "    \"all\": 20\n" +
            "  },\n" +
            "  \"dt\": 1709461978,\n" +
            "  \"sys\": {\n" +
            "    \"type\": 1,\n" +
            "    \"id\": 8931,\n" +
            "    \"country\": \"RU\",\n" +
            "    \"sunrise\": 1709439548,\n" +
            "    \"sunset\": 1709478107\n" +
            "  },\n" +
            "  \"timezone\": 10800,\n" +
            "  \"id\": 569223,\n" +
            "  \"name\": \"Cherepovets\",\n" +
            "  \"cod\": 200\n" +
            "}";
    @BeforeAll
    static void initializedTestObject() throws NoSuchMethodException, URISyntaxException, IOException, InterruptedException, ApiKeyException, LocationInfoException {
    }
    @Test
    void must_WriteItDownCurrentLocationByNameToWeatherDTO() throws ApiKeyException, LocationInfoException, URISyntaxException, IOException, InterruptedException, LocationNotFoundException {
        String url = "https://api.openweathermap.org/data/2.5/weather";
        HashMap<String, String> param = new HashMap<>();
        param.put("q", "Cherepovets");
        when(apiService.buildHttpClient(url, param, key)).thenReturn(response);
        Assertions.assertEquals(apiService.buildHttpClient(url, param, key), response);
        when(apiService.getWeatherByName("Cherepovets")).thenCallRealMethod();
        WeatherDTO weatherDTO = apiService.getWeatherByName("Cherepovets");
        Assertions.assertNotNull(weatherDTO);
    }
    @Test
    void must_WriteItDownLocationsNamesToList() throws ApiKeyException, LocationInfoException, URISyntaxException, IOException, InterruptedException, LocationNotFoundException {
        String url = "https://api.openweathermap.org/geo/1.0/direct";
        HashMap<String, String> param = new HashMap<>();
        param.put("q", "Moscow");
        param.put("limit", "5");
        when(apiService.buildHttpClient(url, param, key)).thenReturn(locationNames);
        Assertions.assertEquals(apiService.buildHttpClient(url, param, key), locationNames);
        when(apiService.getAvailableLocationNames("Moscow")).thenCallRealMethod();
        List<LocationDTO> locationsDTO = apiService.getAvailableLocationNames("Moscow");
        Assertions.assertEquals("Moskow", locationsDTO.get(1).getName());
    }
    @Test
    void must_GetLocationByLongitudeAndLatitude() throws ApiKeyException, LocationInfoException, URISyntaxException, IOException, InterruptedException, LocationNotFoundException {
        String url = "https://api.openweathermap.org/data/2.5/weather";
        Map<String, String> param = new HashMap<>();
        param.put("lon", "37.9");
        param.put("lat", "59.1333");
        when(apiService.buildHttpClient(url, param, key)).thenReturn(response);
        Assertions.assertEquals(apiService.buildHttpClient(url, param, key), response);
        when(apiService.getLocationByLonLat(59.1333, 37.9)).thenCallRealMethod();
        WeatherDTO weatherDTO = apiService.getLocationByLonLat(59.1333, 37.9);
        Assertions.assertNotNull(weatherDTO);
    }
    @Test
    void must_ThrowBadRequestException() {
        String url = "https://api.openweathermap.org/data/2.5/weather";
        HashMap<String, String> param = new HashMap<>();
        param.put("q", "");
        ApiService apiService = new ApiService();
        Assertions.assertThrows(BadRequestException.class, () -> apiService.buildHttpClient(url, param, key));
    }
}