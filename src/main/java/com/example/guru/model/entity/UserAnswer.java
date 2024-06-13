package com.example.guru.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "user_answer")
public class UserAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "answer_id_seq")
    @SequenceGenerator(name = "answer_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "is_right")
    private boolean isRight;

    @ManyToOne()
    private User user;

    @ManyToOne(optional = false)
    private Question question;

    @ManyToOne()
    @JoinColumn(name = "answer_id")
    private Answer answer;

    @Column(name = "date_time")
    private LocalDateTime dateTime;
}
