package com.example.guru.model.entity;

import com.example.guru.model.entity.enums.ChatType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class Chat {

    @Id
    private Long id;

    /**
     * Username, for private chats, supergroups and channels if available
     */
    @Column(name = "user_name")
    private String userName;

    /**
     * First name of the other party in a private chat
     */
    @Column(name = "first_name")
    private String firstName;

    /**
     * Last name of the other party in a private chat
     */
    @Column(name = "last_name")
    private String lastName;

    /**
     * True, if the supergroup chat is a forum
     */
    @Column(name = "is_forum")
    private Boolean isForum;

    /**
     * Type of the chat, can be either “private”, “group”, “supergroup” or “channel”
     */
    @Column(name = "type")
    @Enumerated
    private ChatType type;

    /**
     * Title, for supergroups, channels and group chats
     */
    private String title;

    private String description;

    @Column(name = "invite_link", unique = true)
    private String inviteLink;

    @OneToOne(optional = false, mappedBy = "chat")
    private User user;

//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "chat")
//    private List<InterviewSession> interviewSession;
}
