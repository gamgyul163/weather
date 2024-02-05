package zerobase.weather.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import zerobase.weather.domain.Diary;
import zerobase.weather.service.DiaryService;

@WebMvcTest(DiaryController.class)
class DiaryControllerTest {

  @MockBean
  private DiaryService diaryService;

  @Autowired
  private MockMvc mockMvc;

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

  private final Diary diary = Diary.builder()
      .id(1)
      .weather("날씨1")
      .icon("아이콘1")
      .temperature(10.0)
      .text("일기내용1")
      .date(LocalDate.now())
      .build();

  @Test
  void whenSucceedToCreateDiary() throws Exception {
    //given
    given(diaryService.createDiary(any(),anyString()))
        .willReturn(diary);
    //when
    //then
    mockMvc.perform(post("/create/diary?date=1999-01-01")
            .content("일기내용"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("id").value(1))
        .andExpect(jsonPath("weather").value("날씨1"))
        .andExpect(jsonPath("icon").value("아이콘1"))
        .andExpect(jsonPath("temperature").value(10.0))
        .andExpect(jsonPath("text").value("일기내용1"))
        .andExpect(jsonPath("date").value(LocalDate.now().toString()))
        .andDo(print());
  }

  @Test
  void whenSucceedToReadDiary() throws Exception {
    //given
    given(diaryService.getDiary(any()))
        .willReturn(diaries);
    //when
    //then
    mockMvc.perform(get("/read/diary?date=1999-01-01"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].weather").value("날씨1"))
        .andExpect(jsonPath("$[0].icon").value("아이콘1"))
        .andExpect(jsonPath("$[0].temperature").value(10.0))
        .andExpect(jsonPath("$[0].text").value("일기내용1"))
        .andExpect(jsonPath("$[0].date").value(LocalDate.now().toString()))
        .andExpect(jsonPath("$[1].id").value(2))
        .andExpect(jsonPath("$[1].weather").value("날씨2"))
        .andExpect(jsonPath("$[1].icon").value("아이콘2"))
        .andExpect(jsonPath("$[1].temperature").value(11.0))
        .andExpect(jsonPath("$[1].text").value("일기내용2"))
        .andExpect(jsonPath("$[1].date").value(LocalDate.now().toString()))
        .andDo(print());
  }

  @Test
  void whenSucceedToReadDiaries() throws Exception {
    //given

    given(diaryService.getDiaries(any(),any()))
        .willReturn(diaries);
    //when
    //then
    mockMvc.perform(get("/read/diaries?startDate=1999-01-01&endDate=1999-01-02"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].weather").value("날씨1"))
        .andExpect(jsonPath("$[0].icon").value("아이콘1"))
        .andExpect(jsonPath("$[0].temperature").value(10.0))
        .andExpect(jsonPath("$[0].text").value("일기내용1"))
        .andExpect(jsonPath("$[0].date").value(LocalDate.now().toString()))
        .andExpect(jsonPath("$[1].id").value(2))
        .andExpect(jsonPath("$[1].weather").value("날씨2"))
        .andExpect(jsonPath("$[1].icon").value("아이콘2"))
        .andExpect(jsonPath("$[1].temperature").value(11.0))
        .andExpect(jsonPath("$[1].text").value("일기내용2"))
        .andExpect(jsonPath("$[1].date").value(LocalDate.now().toString()))
        .andDo(print());
  }

  @Test
  void whenSucceedToUpdateDiary() throws Exception {
    //given
    given(diaryService.updateDiary(any(),anyString()))
        .willReturn(diary);
    //when
    //then
    mockMvc.perform(put("/update/diary?date=1999-01-01")
            .content("일기내용"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("id").value(1))
        .andExpect(jsonPath("weather").value("날씨1"))
        .andExpect(jsonPath("icon").value("아이콘1"))
        .andExpect(jsonPath("temperature").value(10.0))
        .andExpect(jsonPath("text").value("일기내용1"))
        .andExpect(jsonPath("date").value(LocalDate.now().toString()))
        .andDo(print());
  }

  @Test
  void whenFailedToUpdateDiary() throws Exception {
    //given
    given(diaryService.updateDiary(any(),anyString()))
        .willThrow(new EntityNotFoundException());
    //when
    //then
    mockMvc.perform(put("/update/diary?date=1999-01-01")
            .content("일기내용"))
        .andExpect(status().isNoContent())
        .andDo(print());
  }
}