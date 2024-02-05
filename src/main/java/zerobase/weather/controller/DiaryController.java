package zerobase.weather.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zerobase.weather.domain.Diary;
import zerobase.weather.service.DiaryService;

@RequiredArgsConstructor
@RestController
public class DiaryController {


  private final DiaryService diaryService;

  @ApiOperation("일기 쓰기")
  @PostMapping("/create/diary")
  Diary createDiary(@RequestParam @DateTimeFormat(iso = ISO.DATE) @ApiParam(value = "날짜 형식 : yyyy-MM-dd", example = "2024-01-01") LocalDate date,
      @RequestBody String text) {
    return diaryService.createDiary(date, text);
  }

  @ApiOperation("일기 읽기 - 해당 날짜의 일기를 모두 가져온다.")
  @GetMapping("/read/diary")
  List<Diary> readDiary(@RequestParam @DateTimeFormat(iso = ISO.DATE) @ApiParam(value = "날짜 형식 : yyyy-MM-dd", example = "2024-01-01") LocalDate date) {
    return diaryService.getDiary(date);
  }

  @ApiOperation("일기 모두 읽기 - 특정 날짜 사이의 일기를 모두 가져온다.")
  @GetMapping("/read/diaries")
  List<Diary> readDiaries(@RequestParam @DateTimeFormat(iso = ISO.DATE) @ApiParam(value = "날짜 형식 : yyyy-MM-dd", example = "2024-01-01") LocalDate startDate,
      @RequestParam @DateTimeFormat(iso = ISO.DATE) @ApiParam(value = "날짜 형식 : yyyy-MM-dd", example = "2024-01-01") LocalDate endDate) {
    return diaryService.getDiaries(startDate, endDate);
  }

  @ApiOperation("일기 수정")
  @PutMapping("/update/diary")
  Diary updateDiary(@RequestParam @DateTimeFormat(iso = ISO.DATE) @ApiParam(value = "날짜 형식 : yyyy-MM-dd", example = "2024-01-01") LocalDate date,
      @RequestBody String text) {
    return diaryService.updateDiary(date, text);
  }

  @ApiOperation("일기 삭제 - 해당 날짜의 일기를 모두 삭제한다.")
  @DeleteMapping("/delete/diary")
  void deleteDiary(@RequestParam @DateTimeFormat(iso = ISO.DATE) @ApiParam(value = "날짜 형식 : yyyy-MM-dd", example = "2024-01-01") LocalDate date) {
    diaryService.deleteDiary(date);
  }
}
