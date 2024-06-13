create table public.chat (
                             is_forum boolean,
                             type smallint,
                             id bigint primary key not null,
                             description character varying(255),
                             first_name character varying(255),
                             invite_link character varying(255),
                             last_name character varying(255),
                             title character varying(255),
                             user_name character varying(255)
);
create unique index chat_invite_link_key on chat using btree (invite_link);

create table public."user" (
                               is_premium boolean,
                               chat_id bigint not null,
                               id bigint primary key not null,
                               registration_date timestamp(6) without time zone,
                               first_name character varying(255),
                               language_code character varying(255),
                               last_name character varying(255),
                               user_name character varying(255) not null,
                               foreign key (chat_id) references public.chat (id)
                                   match simple on update no action on delete no action
);
create unique index user_chat_id_key on "user" using btree (chat_id);
create unique index user_user_name_key on "user" using btree (user_name);

create table public.question (
                                 id bigint primary key not null,
                                 category_type character varying(255),
                                 text character varying(255)
);

create table public.answer (
                               is_right boolean,
                               id bigint primary key not null,
                               question_id bigint not null,
                               text character varying(255),
                               foreign key (question_id) references public.question (id)
                                   match simple on update no action on delete no action
);

create table public.interview_session (
                                          correct_answers_rate double precision,
                                          interview_category_type smallint,
                                          interview_type smallint,
                                          is_finished boolean,
                                          right_answers integer,
                                          wrong_answers integer,
                                          chat_id bigint not null,
                                          created_at timestamp(6) without time zone,
                                          id bigint primary key not null,
                                          user_id bigint not null,
                                          foreign key (user_id) references public."user" (id)
                                              match simple on update no action on delete no action,
                                          foreign key (chat_id) references public.chat (id)
                                              match simple on update no action on delete no action
);

create table public.interview_questions (
                                            interview_session_id bigint not null,
                                            question_id bigint not null,
                                            foreign key (interview_session_id) references public.interview_session (id)
                                                match simple on update no action on delete no action,
                                            foreign key (question_id) references public.question (id)
                                                match simple on update no action on delete no action
);

create table public.user_answer (
                                    is_right boolean,
                                    answer_id bigint,
                                    date_time timestamp(6) without time zone,
                                    id bigint primary key not null,
                                    question_id bigint not null,
                                    user_id bigint,
                                    foreign key (user_id) references public."user" (id)
                                        match simple on update no action on delete no action,
                                    foreign key (answer_id) references public.answer (id)
                                        match simple on update no action on delete no action,
                                    foreign key (question_id) references public.question (id)
                                        match simple on update no action on delete no action
);

create sequence public.answer_id_seq start with 1 increment by 1;
create sequence public.interview_session_id_seq start with 1 increment by 1;
create sequence public.question_id_seq start with 1 increment by 1;
create sequence public.user_id_seq start with 1 increment by 1;

