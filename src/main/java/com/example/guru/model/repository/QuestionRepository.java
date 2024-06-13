package com.example.guru.model.repository;

import com.example.guru.model.entity.Question;
import com.example.guru.model.entity.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query(" SELECT q FROM Question q where q.categoryType = :category AND q.id not in :ids")
    List<Question> findByCategoryAndNotIn(@Param("category") CategoryType categoryType, @Param("ids") List<Long> ids);

    List<Question> findByCategoryType(CategoryType categoryType);
}
