  package com.knuissant.dailyq.archive.repository;

  import com.knuissant.dailyq.answer.Answer;
  import com.knuissant.dailyq.user.User;
  import java.util.Optional;
  import org.springframework.data.jpa.repository.JpaRepository;

  public interface ArchiveRepository extends JpaRepository<Answer, Long> {

    Optional<Answer> findByIdAndUser(Long answerId, User user);

  }
