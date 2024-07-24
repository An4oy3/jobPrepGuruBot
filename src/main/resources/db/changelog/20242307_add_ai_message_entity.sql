CREATE TABLE ai_message (
    id bigint NOT NULL PRIMARY KEY,
    is_right BOOLEAN,
    content TEXT,
    user_answer TEXT,
    question_id bigint not null,
    ai_response_id varchar(255) not null,
    foreign key (question_id) references public.question (id)
        match simple on update no action on delete no action,
    foreign key (ai_response_id) references public.ai_response (id)
        match simple on update no action on delete no action
);

create sequence public.ai_message_id_seq start with 1 increment by 1;
