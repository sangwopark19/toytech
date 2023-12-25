package io.toytech.backend.recruitment.service;

import io.toytech.backend.recruitment.domain.Recruitment;
import io.toytech.backend.recruitment.domain.RecruitmentTag;
import io.toytech.backend.recruitment.domain.Tag;
import io.toytech.backend.recruitment.repository.RecruitmentRepository;
import io.toytech.backend.recruitment.repository.RecruitmentTagRepository;
import io.toytech.backend.recruitment.repository.TagRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecruitmentServiceImpl implements RecruitmentService{

  private final RecruitmentRepository recruitmentRepository;
  private final RecruitmentTagRepository recruitmentTagRepository;
  private final TagRepository tagRepository;


  @Autowired
  public RecruitmentServiceImpl(RecruitmentRepository recruitmentRepository, RecruitmentTagRepository recruitmentTagRepository, TagRepository tagRepository) {
    this.recruitmentRepository = recruitmentRepository;
    this.recruitmentTagRepository = recruitmentTagRepository;
    this.tagRepository = tagRepository;
  }

  // 전체 글 조회 (최신순)
  @Override
  public Map<Recruitment, List<Tag>> findAll() {
    Iterable<Recruitment> allRecruitments = recruitmentRepository.findAllByOrderByCreatedAtDesc();

    Map<Recruitment, List<Tag>> recruitmentTagMap = new LinkedHashMap<>();

    for (Recruitment recruitment : allRecruitments) {
      long recruitmentId = recruitment.getId();
      List<RecruitmentTag> tagIds = recruitmentTagRepository.findByRecruitmentId(recruitmentId);
      List<Tag> tags = new ArrayList<>();
      for (RecruitmentTag tagId : tagIds) {
        Tag tag = tagId.getTag();
        tags.add(tag);
      }

      // 댓글 개수
//      int commentCnt = commentRepository.countByRecruitmentId(recruitmentId);
//      recruitment.setComment(commentCnt);

      recruitmentTagMap.put(recruitment, tags);
    }
    return recruitmentTagMap;
  }

  // 전체 글 조회 (조회순)
  @Override
  public Map<Recruitment, List<Tag>> findAllByView() {
    Iterable<Recruitment> allRecruitments = recruitmentRepository.findAllByOrderByViewDesc();

    Map<Recruitment, List<Tag>> recruitmentTagMap = new LinkedHashMap<>();

    for (Recruitment recruitment : allRecruitments) {
      long recruitmentId = recruitment.getId();
      List<RecruitmentTag> tagIds = recruitmentTagRepository.findByRecruitmentId(recruitmentId);
      List<Tag> tags = new ArrayList<>();
      for (RecruitmentTag tagId : tagIds) {
        Tag tag = tagId.getTag();
        tags.add(tag);
      }

      // 댓글 개수
//      int commentCnt = commentRepository.countByRecruitmentId(recruitmentId);
//      recruitment.setComment(commentCnt);

      recruitmentTagMap.put(recruitment, tags);
    }
    return recruitmentTagMap;
  }

  // 게시글 id별 조회
  @Override
  public Map<Recruitment, List<Tag>> findById(Long id) {
    Optional<Recruitment> optionalRecruitment = recruitmentRepository.findById(id);
    if (optionalRecruitment.isPresent()) {
    Recruitment recruitment = optionalRecruitment.get(); //java.util.NoSuchElementException
    Map<Recruitment, List<Tag>> recruitmentTagMap = new HashMap<>();
    List<RecruitmentTag> tagIds = recruitmentTagRepository.findByRecruitmentId(id);
    List<Tag> tags = new ArrayList<>();
    for (RecruitmentTag tagId : tagIds) {
      Tag tag = tagId.getTag();
      tags.add(tag);
    }

      // 댓글 개수
//      int commentCnt = commentRepository.countByRecruitmentId(recruitmentId);
//      recruitment.setComment(commentCnt);

    recruitmentTagMap.put(recruitment, tags);
    return recruitmentTagMap;
    }
    throw new EntityNotFoundException("해당 ID에 매칭되는 글을 찾을 수 없습니다: " + id);
  }

  // 글 작성
  @Override
  public Map<Recruitment, List<Tag>> createRecruitment(Recruitment recruitment, Set<Tag> tags){
    Recruitment newrecruitment = recruitmentRepository.save(recruitment);
    Map<Recruitment, List<Tag>> recruitmentTagMap = new HashMap<>();
    List<Tag> taglist = new ArrayList<>();
    for (Tag tag : tags) {
      // 태그 이름으로 검색
      Tag tag1 = tagRepository.findByName(tag.getName()).orElseGet(()-> tagRepository.save(tag));
      recruitmentTagRepository.save(RecruitmentTag.builder()
          .recruitment(newrecruitment)
          .tag(tag1)
          .build());
      taglist.add(tag1);
      }
    recruitmentTagMap.put(recruitment, taglist);
    return recruitmentTagMap;
    }

  // 글 수정
  @Override
  public Map<Recruitment, List<Tag>> updateRecruitment(Long id, Recruitment recruitment,Set<Tag> tags) {
    Optional<Recruitment> optionalRecruitment = recruitmentRepository.findById(id);
    if (optionalRecruitment.isPresent()) {
      Recruitment oldRecruitment = optionalRecruitment.get();

      // 글과 연결된 기존 RecruitmentTag 가져오기
      List<RecruitmentTag> oldTags = recruitmentTagRepository.findByRecruitmentId(id);

      // 기존 관계 null 처리 (삭제하지 않음)
      for (RecruitmentTag recruitmentTag : oldTags) {
        recruitmentTag.setRecruitment(null);
        recruitmentTag.setTag(null);
        recruitmentTagRepository.save(recruitmentTag);
      }

      // 글 내용 업데이트
      if(recruitment.getTitle() != null)
        oldRecruitment.setTitle(recruitment.getTitle());
      if(recruitment.getContent() != null)
        oldRecruitment.setContent(recruitment.getContent());
      oldRecruitment.setActive(recruitment.isActive());

      Recruitment updatedRecruitment = recruitmentRepository.save(oldRecruitment);

      // 새로운 태그 추가
      List<Tag> updatedTags = new ArrayList<>();
      for (Tag tag : tags) {
        Tag newTag = tagRepository.findByName(tag.getName()).orElseGet(() -> tagRepository.save(tag));
        recruitmentTagRepository.save(RecruitmentTag.builder()
            .recruitment(updatedRecruitment)
            .tag(newTag)
            .build());
        updatedTags.add(newTag);
      }

      Map<Recruitment, List<Tag>> recruitmentTagMap = new HashMap<>();
      recruitmentTagMap.put(updatedRecruitment, updatedTags);
      return recruitmentTagMap;
    }
    throw new EntityNotFoundException("해당 ID에 매칭되는 글을 찾을 수 없습니다: " + id);
  }

  // 글 삭제
  @Override
  public void deleteRecruitment(Long id) {
    Optional<Recruitment> optionalRecruitment = recruitmentRepository.findById(id);
    if (optionalRecruitment.isPresent()) {
      Recruitment recruitment = optionalRecruitment.get();

      // 기존 관계 null 처리 (삭제하지 않음)
      List<RecruitmentTag> tags = recruitmentTagRepository.findByRecruitmentId(id);
      for (RecruitmentTag tag : tags) {
        tag.setRecruitment(null);
        tag.setTag(null);
        recruitmentTagRepository.save(tag);
      }

      recruitmentRepository.delete(recruitment);

    } else throw new EntityNotFoundException("해당 ID에 매칭되는 글을 찾을 수 없습니다: " + id);
  }

  // 조회수 증가
  @Override
  @Transactional
  public int updateView(Long id){
    return recruitmentRepository.updateView(id);
  }

  // 검색 기능
  @Override
  public Map<Recruitment, List<Tag>> search(String keyword) {
    Iterable<Recruitment> recruitments = recruitmentRepository.findByTitleContaining(keyword);

    Map<Recruitment, List<Tag>> recruitmentTagMap = new LinkedHashMap<>();

    for (Recruitment recruitment : recruitments) {
      long recruitmentId = recruitment.getId();
      List<RecruitmentTag> tagIds = recruitmentTagRepository.findByRecruitmentId(recruitmentId);
      List<Tag> tags = new ArrayList<>();
      for (RecruitmentTag tagId : tagIds) {
        Tag tag = tagId.getTag();
        tags.add(tag);
      }

      // 댓글 개수
//      int commentCnt = commentRepository.countByRecruitmentId(recruitmentId);
//      recruitment.setComment(commentCnt);

      recruitmentTagMap.put(recruitment, tags);
    }
    return recruitmentTagMap;
  }

  // 태그로 글 검색
  @Override
  public Map<Recruitment, List<Tag>> findByTag(String tagName) {
    Tag tag = tagRepository.findByName(tagName).orElseThrow(() -> new EntityNotFoundException("해당 태그가 없습니다: " + tagName));
    List<RecruitmentTag> recruitmentTags = recruitmentTagRepository.findByTag(tag);
    Map<Recruitment, List<Tag>> recruitmentTagMap = new HashMap<>();
    for (RecruitmentTag recruitmentTag : recruitmentTags) {
      Recruitment recruitment = recruitmentTag.getRecruitment();
      List<Tag> tags = new ArrayList<>();
      List<RecruitmentTag> tagIds = recruitmentTagRepository.findByRecruitmentId(recruitment.getId());
      for (RecruitmentTag tagId : tagIds) {
        Tag tag1 = tagId.getTag();
        tags.add(tag1);
      }

      // 댓글 개수
//      int commentCnt = commentRepository.countByRecruitmentId(recruitmentId);
//      recruitment.setComment(commentCnt);

      recruitmentTagMap.put(recruitment, tags);
    }
    return recruitmentTagMap;
  }

}