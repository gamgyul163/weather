package zerobase.weather.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.WeatherApplication;
import zerobase.weather.domain.DateWeather;
import zerobase.weather.domain.Diary;
import zerobase.weather.repository.DateWeatherRepository;
import zerobase.weather.repository.DiaryRepository;

@RequiredArgsConstructor
@Service
public class DiaryService {

  private final DiaryRepository diaryRepository;
  private final DateWeatherRepository dateWeatherRepository;
  private static final Logger logger = LoggerFactory.getLogger(WeatherApplication.class);

  @Value("${openweathermap.api.key}")
  private String apiKey;

  @Value("${openweathermap.api.url}")
  private String apiUrl;

  @Transactional(isolation = Isolation.SERIALIZABLE)
  public Diary createDiary(LocalDate localDate, String text) {
    DateWeather dateWeather = dateWeatherRepository.findById(localDate)
        .orElseGet(this::getWeatherFromApi);
    Diary newDiary = Diary.builder()
        .weather(dateWeather.getWeather())
        .icon(dateWeather.getIcon())
        .temperature(dateWeather.getTemperature())
        .text(text)
        .date(localDate)
        .build();
    return diaryRepository.save(newDiary);
  }

  private String getWeatherString() {
    try {
      URL url = new URL(apiUrl+apiKey);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      BufferedReader bufferedReader;
      if(connection.getResponseCode() == 200) {
        bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      } else {
        bufferedReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
      }
      String inputLine;
      StringBuilder response = new StringBuilder();
      while((inputLine = bufferedReader.readLine()) != null) {
        response.append(inputLine);
      }
      bufferedReader.close();
      return response.toString();
    } catch (Exception e) {
      return "";
    }
  }

  private Map<String, Object> parseWeather(String jsonString)  {
    JSONParser jsonParser = new JSONParser();
    JSONObject jsonObject = null;
    try {
      jsonObject = (JSONObject) jsonParser.parse(jsonString);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }

    Map<String, Object> resultMap = new HashMap<>();
    JSONObject mainData = (JSONObject) jsonObject.get("main");
    resultMap.put("temp",mainData.get("temp"));
    JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
    JSONObject weatherData = (JSONObject) weatherArray.get(0);
    resultMap.put("weather",weatherData.get("main"));
    resultMap.put("icon",weatherData.get("icon"));

    return resultMap;
  }

  @Transactional(readOnly = true)
  public List<Diary> getDiary(LocalDate date) {
    return diaryRepository.findAllByDate(date);
  }

  @Transactional(readOnly = true)
  public List<Diary> getDiaries(LocalDate startDate, LocalDate endDate) {
    return diaryRepository.findAllByDateBetween(startDate, endDate);
  }

  @Transactional
  public Diary updateDiary(LocalDate date, String text) {
    Diary diaryToUpdate = diaryRepository.findFirstByDate(date)
        .orElseThrow(() -> new EntityNotFoundException());
    diaryToUpdate.setText(text);
    return diaryRepository.save(diaryToUpdate);
  }

  @Transactional
  public void deleteDiary(LocalDate date) {
    diaryRepository.deleteAllByDate(date);
  }

  @Transactional
  @Scheduled(cron = "${openweathermap.api.cron}")
  public void saveWeatherData() {
    logger.info("saveWeatherData started");
    dateWeatherRepository.save(getWeatherFromApi());
    logger.info("saveWeatherData ended");
  }

  private DateWeather getWeatherFromApi() {
    String weatherData = getWeatherString();
    Map<String, Object> parsedWeather = parseWeather(weatherData);
    return DateWeather.builder()
        .date(LocalDate.now())
        .weather(parsedWeather.get("weather").toString())
        .icon(parsedWeather.get("icon").toString())
        .temperature((double)parsedWeather.get("temp"))
        .build();
  }
}
