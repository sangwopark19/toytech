package io.toytech.backend.recruitment.repository;

import io.toytech.backend.recruitment.domain.Recruitment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> {

  Page<Recruitment> findAllByOrderByCreatedAtDesc(Pageable pageable);

  Page<Recruitment> findAllByOrderByViewDesc(Pageable pageable);
  Page<Recruitment> findAllByOrderByCommentDesc(Pageable pageable);

  Page<Recruitment> findAllByActiveOrderByCreatedAtDesc(Pageable pageable, Boolean active);
  Page<Recruitment> findAllByActiveOrderByViewDesc(Pageable pageable, Boolean active);
  Page<Recruitment> findAllByActiveOrderByCommentDesc(Pageable pageable, Boolean active);

  Optional<Recruitment> findById(long id);
  @Modifying
  @Query("update Recruitment r set r.view = r.view + 1 where r.id = :id")
  int updateView(Long id);

  Page<Recruitment> findByTitleContainingOrderByCreatedAtDesc(String keyword, Pageable pageable);
  Page<Recruitment> findByTitleContainingOrderByViewDesc(String keyword, Pageable pageable);

  Page<Recruitment> findByTitleContainingOrderByCommentDesc(String keyword, Pageable pageable);

  Page<Recruitment> findByTitleContainingAndActiveOrderByCreatedAtDesc(String keyword, Pageable pageable, Boolean active);

  Page<Recruitment> findByTitleContainingAndActiveOrderByViewDesc(String keyword, Pageable pageable, Boolean active);

  Page<Recruitment> findByTitleContainingAndActiveOrderByCommentDesc(String keyword, Pageable pageable, Boolean active);
}