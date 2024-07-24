package com.example.guru.model.entity;

import com.example.guru.model.entity.enums.CategoryType;
import com.example.guru.model.entity.enums.InterviewType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "interview_session")
public class InterviewSession {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "interview_session_id_seq")
    @SequenceGenerator(name = "interview_session_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Chat chat;

    private LocalDateTime createdAt;

    @Column(name = "is_finished")
    private Boolean isFinished;

    @Column(name = "correct_answers_rate")
    private Double correctAnswersRate;

    @Enumerated
    @Column(name = "interview_type")
    private InterviewType interviewType;

    @Enumerated
    @Column(name = "interview_category_type")
    private CategoryType interviewCategory;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "interview_questions",
    joinColumns = @JoinColumn(name = "interview_session_id"),
    inverseJoinColumns = @JoinColumn(name = "question_id"))
    private List<Question> questions = new ArrayList<>();

    @Column(name = "right_answers")
    private Integer rightAnswers;

    @Column(name = "wrong_answers")
    private Integer wrongAnswers;

    @OneToOne()
    @JoinColumn(name = "last_question_id")
    private Question lastQuestion;
}
