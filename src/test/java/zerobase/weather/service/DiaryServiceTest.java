package zerobase.weather.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zerobase.weather.domain.DateWeather;
import zerobase.weather.domain.Diary;
import zerobase.weather.repository.DateWeatherRepository;
import zerobase.weather.repository.DiaryRepository;


@ExtendWith(MockitoExtension.class)
class DiaryServiceTest {

  private final List<Diary> diaries = Arrays.asList(
      Diary.builder()
          .id(1)
          .weather("날씨1")
          .icon("아이콘1")
          .temperature(10.0)
          .text("일기내용1")
          .date(LocalDate.now())
          .build(),
      Diary.builder()
          .id(2)
          .weather("날씨2")
          .icon("아이콘2")
          .temperature(11.0)
          .text("일기내용2")
          .date(LocalDate.now())
          .build()
  );

  @Mock
  private DiaryRepository diaryRepository;

  @Mock
  private DateWeatherRepository dateWeatherRepository;

  @InjectMocks
  private DiaryService diaryService;


  @Test
  void successToCreateDiary() {
    //given
    DateWeather dateWeather = DateWeather.builder()
        .date(LocalDate.now())
        .weather("날씨")
        .icon("아이콘")
        .temperature(10.0)
        .build();
    given(dateWeatherRepository.findById(any()))
        .willReturn(Optional.of(dateWeather));
    ArgumentCaptor<Diary> captor = ArgumentCaptor.forClass(Diary.class);
    //when
    diaryService.createDiary(LocalDate.now(), "일기내용");
    //then
    verify(diaryRepository, times(1)).save(captor.capture());
    assertEquals("날씨", captor.getValue().getWeather());
    assertEquals("아이콘", captor.getValue().getIcon());
    assertEquals(10.0, captor.getValue().getTemperature());
    assertEquals(LocalDate.now(), captor.getValue().getDate());
  }

  void successToGetDiary() {
    //given
    given(diaryRepository.findAllById(any()))
        .willReturn(diaries);
    //when
    List<Diary> foundDiaries = diaryService.getDiary(LocalDate.now());
    //then
    assertEquals(1, foundDiaries.get(0).getId());
    assertEquals("날씨1", foundDiaries.get(0).getWeather());
    assertEquals("아이콘1", foundDiaries.get(0).getIcon());
    assertEquals(10.0, foundDiaries.get(0).getTemperature());
    assertEquals("일기내용1", foundDiaries.get(0).getText());
    assertEquals(LocalDate.now(), foundDiaries.get(0).getDate());
    assertEquals(2, foundDiaries.get(1).getId());
    assertEquals("날씨2", foundDiaries.get(1).getWeather());
    assertEquals("아이콘2", foundDiaries.get(1).getIcon());
    assertEquals(11.0, foundDiaries.get(1).getTemperature());
    assertEquals("일기내용2", foundDiaries.get(1).getText());
    assertEquals(LocalDate.now(), foundDiaries.get(1).getDate());
  }

  @Test
  void successToGetDiaries() {
    //given
    given(diaryRepository.findAllByDateBetween(any(), any()))
        .willReturn(diaries);
    //when
    List<Diary> foundDiaries = diaryService.getDiaries(LocalDate.now(), LocalDate.now());
    //then
    assertEquals(1, foundDiaries.get(0).getId());
    assertEquals("날씨1", foundDiaries.get(0).getWeather());
    assertEquals("아이콘1", foundDiaries.get(0).getIcon());
    assertEquals(10.0, foundDiaries.get(0).getTemperature());
    assertEquals("일기내용1", foundDiaries.get(0).getText());
    assertEquals(LocalDate.now(), foundDiaries.get(0).getDate());
    assertEquals(2, foundDiaries.get(1).getId());
    assertEquals("날씨2", foundDiaries.get(1).getWeather());
    assertEquals("아이콘2", foundDiaries.get(1).getIcon());
    assertEquals(11.0, foundDiaries.get(1).getTemperature());
    assertEquals("일기내용2", foundDiaries.get(1).getText());
    assertEquals(LocalDate.now(), foundDiaries.get(1).getDate());
  }

  @Test
  void successToUpdateDiary() {
    //given
    Diary diary = Diary.builder()
        .id(1)
        .weather("날씨1")
        .icon("아이콘1")
        .temperature(10.0)
        .text("일기내용1")
        .date(LocalDate.now())
        .build();
    given(diaryRepository.findFirstByDate(any()))
        .willReturn(Optional.of(diary));
    ArgumentCaptor<Diary> captor = ArgumentCaptor.forClass(Diary.class);
    //when
    diaryService.updateDiary(LocalDate.now(), "수정한 일기내용");
    //then
    verify(diaryRepository, times(1)).save(captor.capture());
    assertEquals(1, captor.getValue().getId());
    assertEquals("날씨1", captor.getValue().getWeather());
    assertEquals("아이콘1", captor.getValue().getIcon());
    assertEquals(10.0, captor.getValue().getTemperature());
    assertEquals("수정한 일기내용", captor.getValue().getText());
    assertEquals(LocalDate.now(), captor.getValue().getDate());
  }

  @Test
  void failToUpdateDiary_EntityNotFoundException() {
    //given
    given(diaryRepository.findFirstByDate(any()))
        .willReturn(Optional.empty());
    //when

    //then
    assertThrows(EntityNotFoundException.class, () -> diaryService.updateDiary(LocalDate.now(), "수정한 일기내용"));
  }

  @Test
  void successToDeleteDiary() {
    //given

    ArgumentCaptor<LocalDate> captor = ArgumentCaptor.forClass(LocalDate.class);
    //when
    diaryService.deleteDiary(LocalDate.now());
    //then
    verify(diaryRepository, times(1)).deleteAllByDate(captor.capture());
    assertEquals(LocalDate.now(), captor.getValue());
  }
}