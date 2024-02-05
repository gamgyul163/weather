package zerobase.weather.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.weather.domain.Diary;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Integer> {

  List<Diary> findAllByDate(LocalDate date);

  List<Diary> findAllByDateBetween(LocalDate startDate, LocalDate endDate);

  Optional<Diary> findFirstByDate(LocalDate date);

  void deleteAllByDate(LocalDate date);
}
