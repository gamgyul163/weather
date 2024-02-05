package zerobase.weather;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional(isolation = Isolation.READ_UNCOMMITTED)
@Sql("classpath:data.sql")
class WeatherApplicationTests {

  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("일기 쓰기 - DB에 날씨 정보 있음")
  void createDiaryWithDB() throws Exception {
    //given
    //when
    //then
    mockMvc.perform(post("/create/diary?date=2024-01-01")
            .content("textBody1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("id").exists())
        .andExpect(jsonPath("weather").value("rain"))
        .andExpect(jsonPath("icon").value("icon01"))
        .andExpect(jsonPath("temperature").value(10.0))
        .andExpect(jsonPath("text").value("textBody1"))
        .andExpect(jsonPath("date").value("2024-01-01"))
        .andDo(print());
  }

  @Test
  @DisplayName("일기 쓰기 - DB에 날씨 정보 없음")
  void createAndGetDiaryWithAPI() throws Exception {
    //given
    //when
    //then
    mockMvc.perform(post("/create/diary?date=2024-01-02")
            .content("textBody"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("id").exists())
        .andExpect(jsonPath("weather").exists())
        .andExpect(jsonPath("icon").exists())
        .andExpect(jsonPath("temperature").exists())
        .andExpect(jsonPath("text").value("textBody"))
        .andExpect(jsonPath("date").value("2024-01-02"))
        .andDo(print());
  }

  @Test
  @DisplayName("일기 읽기 - 특정 날짜의 일기를 모두 가져온다.")
  void readDiary() throws Exception {
    //given
    //when
    //then
    mockMvc.perform(get("/read/diary?date=2030-01-01"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").exists())
        .andExpect(jsonPath("$[0].weather").value("Rain"))
        .andExpect(jsonPath("$[0].icon").value("icon01"))
        .andExpect(jsonPath("$[0].temperature").value(10.0))
        .andExpect(jsonPath("$[0].text").value("text1"))
        .andExpect(jsonPath("$[0].date").value("2030-01-01"))
        .andExpect(jsonPath("$[1].id").exists())
        .andExpect(jsonPath("$[1].weather").value("Clouds"))
        .andExpect(jsonPath("$[1].icon").value("icon02"))
        .andExpect(jsonPath("$[1].temperature").value(11.0))
        .andExpect(jsonPath("$[1].text").value("text2"))
        .andExpect(jsonPath("$[1].date").value("2030-01-01"))
        .andDo(print());
  }

  @Test
  @DisplayName("일기 모두 읽기 - 특정 날짜 사이의 일기를 모두 가져온다.")
  void readDiaries() throws Exception {
    //given
    //when
    //then
    mockMvc.perform(get("/read/diaries?startDate=1999-12-10&endDate=1999-12-11"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").exists())
        .andExpect(jsonPath("$[0].weather").value("Rain"))
        .andExpect(jsonPath("$[0].icon").value("icon01"))
        .andExpect(jsonPath("$[0].temperature").value(10.0))
        .andExpect(jsonPath("$[0].text").value("text1"))
        .andExpect(jsonPath("$[0].date").value("1999-12-10"))
        .andExpect(jsonPath("$[1].id").exists())
        .andExpect(jsonPath("$[1].weather").value("Clouds"))
        .andExpect(jsonPath("$[1].icon").value("icon02"))
        .andExpect(jsonPath("$[1].temperature").value(20.0))
        .andExpect(jsonPath("$[1].text").value("text2"))
        .andExpect(jsonPath("$[1].date").value("1999-12-11"))
        .andDo(print());
  }

  @Test
  @DisplayName("일기 수정")
  void updateDiary() throws Exception {
    //given
    //when
    //then
    mockMvc.perform(put("/update/diary?date=1999-12-10")
            .content("newText"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("id").exists())
        .andExpect(jsonPath("weather").value("Rain"))
        .andExpect(jsonPath("icon").value("icon01"))
        .andExpect(jsonPath("temperature").value(10.0))
        .andExpect(jsonPath("text").value("newText"))
        .andExpect(jsonPath("date").value("1999-12-10"))
        .andDo(print());
  }

  @Test
  @DisplayName("일기 수정 실패 - 일기 없음")
  void updateDiaryFail() throws Exception {
    //given
    //when
    //then
    mockMvc.perform(put("/update/diary?date=1999-12-25")
            .content("newText"))
        .andExpect(status().isNoContent())
        .andDo(print());
  }

  @Test
  @DisplayName("일기 삭제")
  void deleteDiary() throws Exception {
    //given
    //when
    mockMvc.perform(delete("/delete/diary?date=1999-12-10"));
    //then
    mockMvc.perform(get("/read/diary?date=1999-12-10"))
        .andExpect(status().isNoContent())
        .andDo(print());
  }
}
