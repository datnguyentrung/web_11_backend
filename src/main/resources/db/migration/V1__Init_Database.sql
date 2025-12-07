-- =========================================================
-- PHẦN THÊM MỚI: CẤU HÌNH TÌM KIẾM NÂNG CAO (FULL TEXT SEARCH)
-- =========================================================

-- 1. Bật extension (Extension thường cài vào schema public)
CREATE EXTENSION IF NOT EXISTS pg_trgm SCHEMA public;
CREATE EXTENSION IF NOT EXISTS unaccent SCHEMA public;

-- 2. Tạo hàm unaccent IMMUTABLE (Bắt buộc để tạo Index trên tên tiếng Việt)
-- Hàm gốc unaccent không thể dùng trong Index, nên phải tạo hàm wrap này.
CREATE OR REPLACE FUNCTION public.f_unaccent(text)
    RETURNS text AS
$func$
SELECT public.unaccent('public.unaccent', $1)
$func$ LANGUAGE sql IMMUTABLE;

-- =========================================================
-- KẾT THÚC PHẦN THÊM MỚI
-- =========================================================

-- DROP SCHEMA achievement;

CREATE SCHEMA IF NOT EXISTS achievement AUTHORIZATION root;
-- achievement.poomsae_list definition

-- Drop table

-- DROP TABLE achievement.poomsae_list;

CREATE TABLE achievement.poomsae_list
(
    id_poomsae_list     uuid         NOT NULL,
    age_division        varchar(255) NULL,
    medal               varchar(255) NULL,
    branch              int4         NULL,
    poomsae_combination uuid         NULL,
    student_id_user     uuid         NULL,
    tournament          uuid         NULL,
    CONSTRAINT poomsae_list_age_division_check CHECK (((age_division)::text = ANY
                                                       (ARRAY [('U6'::character varying)::text, ('U7'::character varying)::text, ('U8'::character varying)::text, ('U9'::character varying)::text, ('U10'::character varying)::text, ('U11'::character varying)::text, ('U12'::character varying)::text, ('U13'::character varying)::text, ('U14'::character varying)::text, ('U15'::character varying)::text, ('U16'::character varying)::text, ('U17'::character varying)::text, ('U18'::character varying)::text, ('U19'::character varying)::text, ('U20'::character varying)::text]))),
    CONSTRAINT poomsae_list_medal_check CHECK (((medal)::text = ANY
                                                (ARRAY [('GOLD'::character varying)::text, ('SILVER'::character varying)::text, ('BRONZE'::character varying)::text]))),
    CONSTRAINT poomsae_list_pkey PRIMARY KEY (id_poomsae_list)
);


-- achievement.sparring_list definition

-- Drop table

-- DROP TABLE achievement.sparring_list;

CREATE TABLE achievement.sparring_list
(
    id_sparring_list     uuid         NOT NULL,
    age_division         varchar(255) NULL,
    medal                varchar(255) NULL,
    branch               int4         NULL,
    sparring_combination uuid         NULL,
    student_id_user      uuid         NULL,
    tournament           uuid         NULL,
    CONSTRAINT sparring_list_age_division_check CHECK (((age_division)::text = ANY
                                                        (ARRAY [('U6'::character varying)::text, ('U7'::character varying)::text, ('U8'::character varying)::text, ('U9'::character varying)::text, ('U10'::character varying)::text, ('U11'::character varying)::text, ('U12'::character varying)::text, ('U13'::character varying)::text, ('U14'::character varying)::text, ('U15'::character varying)::text, ('U16'::character varying)::text, ('U17'::character varying)::text, ('U18'::character varying)::text, ('U19'::character varying)::text, ('U20'::character varying)::text]))),
    CONSTRAINT sparring_list_medal_check CHECK (((medal)::text = ANY
                                                 (ARRAY [('GOLD'::character varying)::text, ('SILVER'::character varying)::text, ('BRONZE'::character varying)::text]))),
    CONSTRAINT sparring_list_pkey PRIMARY KEY (id_sparring_list)
);


-- DROP SCHEMA association;

CREATE SCHEMA IF NOT EXISTS association AUTHORIZATION root;
-- association.bracket_node_links definition

-- Drop table

-- DROP TABLE association.bracket_node_links;

CREATE TABLE association.bracket_node_links
(
    bracket_node_id uuid NOT NULL,
    linked_node_id  int4 NULL
);


-- association.branch_weekdays definition

-- Drop table

-- DROP TABLE association.branch_weekdays;

CREATE TABLE association.branch_weekdays
(
    id_branch int4         NOT NULL,
    weekday   varchar(255) NULL,
    CONSTRAINT branch_weekdays_weekday_check CHECK (((weekday)::text = ANY
                                                     (ARRAY [('SUNDAY'::character varying)::text, ('MONDAY'::character varying)::text, ('TUESDAY'::character varying)::text, ('WEDNESDAY'::character varying)::text, ('THURSDAY'::character varying)::text, ('FRIDAY'::character varying)::text, ('SATURDAY'::character varying)::text])))
);


-- DROP SCHEMA attendance;

CREATE SCHEMA IF NOT EXISTS attendance AUTHORIZATION root;
-- attendance.coach_attendance definition

-- Drop table

-- DROP TABLE attendance.coach_attendance;

CREATE TABLE attendance.coach_attendance
(
    attendance_date date         NOT NULL,
    class_session   varchar(255) NOT NULL,
    coach_id_user   uuid         NOT NULL,
    created_at      timestamp(6) NULL,
    image_url       varchar(255) NULL,
    CONSTRAINT coach_attendance_pkey PRIMARY KEY (attendance_date, class_session, coach_id_user)
);


-- attendance.student_attendance definition

-- Drop table

-- DROP TABLE attendance.student_attendance;

CREATE TABLE attendance.student_attendance
(
    attendance_date          date         NOT NULL,
    class_session            varchar(255) NOT NULL,
    student_id_user          uuid         NOT NULL,
    attendance_status        varchar(255) NOT NULL,
    attendance_time          time(6)      NULL,
    evaluation_status        varchar(255) NULL,
    notes                    text         NULL,
    attendance_coach_id_user uuid         NULL,
    evaluation_coach_id_user uuid         NULL,
    CONSTRAINT student_attendance_attendance_status_check CHECK (((attendance_status)::text = ANY
                                                                  (ARRAY [('X'::character varying)::text, ('V'::character varying)::text, ('M'::character varying)::text, ('P'::character varying)::text, ('B'::character varying)::text]))),
    CONSTRAINT student_attendance_evaluation_status_check CHECK (((evaluation_status)::text = ANY
                                                                  (ARRAY [('T'::character varying)::text, ('TB'::character varying)::text, ('Y'::character varying)::text]))),
    CONSTRAINT student_attendance_pkey PRIMARY KEY (attendance_date, class_session, student_id_user)
);
CREATE INDEX idx_session_date ON attendance.student_attendance USING btree (class_session, attendance_date);


-- attendance.trial_attendance definition

-- Drop table

-- DROP TABLE attendance.trial_attendance;

CREATE TABLE attendance.trial_attendance
(
    attendance_date              date         NOT NULL,
    class_session                varchar(255) NOT NULL,
    registration_id_registration uuid         NOT NULL,
    attendance_status            varchar(255) NOT NULL,
    attendance_time              time(6)      NULL,
    evaluation_status            varchar(255) NULL,
    notes                        text         NULL,
    attendance_coach_id_user     uuid         NULL,
    evaluation_coach_id_user     uuid         NULL,
    CONSTRAINT trial_attendance_attendance_status_check CHECK (((attendance_status)::text = ANY
                                                                (ARRAY [('X'::character varying)::text, ('V'::character varying)::text, ('M'::character varying)::text, ('P'::character varying)::text, ('B'::character varying)::text]))),
    CONSTRAINT trial_attendance_evaluation_status_check CHECK (((evaluation_status)::text = ANY
                                                                (ARRAY [('T'::character varying)::text, ('TB'::character varying)::text, ('Y'::character varying)::text]))),
    CONSTRAINT trial_attendance_pkey PRIMARY KEY (attendance_date, class_session, registration_id_registration)
);


-- DROP SCHEMA authentication;

CREATE SCHEMA IF NOT EXISTS authentication AUTHORIZATION root;
-- authentication.user_tokens definition

-- Drop table

-- DROP TABLE authentication.user_tokens;

CREATE TABLE authentication.user_tokens
(
    id_token      uuid         NOT NULL,
    expires_at    timestamp(6) NOT NULL,
    id_device     varchar(255) NULL,
    refresh_token text         NULL,
    revoked       bool         NOT NULL,
    id_user       uuid         NULL,
    CONSTRAINT user_tokens_pkey PRIMARY KEY (id_token)
);


-- authentication.users definition

-- Drop table

-- DROP TABLE authentication.users;

CREATE TABLE authentication.users
(
    id_user                 uuid         NOT NULL,
    created_at              date         NULL,
    email                   varchar(255) NULL,
    id_account              varchar(255) NOT NULL,
    next_password_change_at timestamp(6) NULL,
    password_hash           varchar(255) NULL,
    status                  varchar(255) NULL,
    updated_at              timestamp(6) NULL,
    "role"                  varchar(255) NULL,
    CONSTRAINT uk6dotkott2kjsp8vw4d0m25fb7 UNIQUE (email),
    CONSTRAINT ukhdsjm6l3ludh1inv75noedbpm UNIQUE (id_account),
    CONSTRAINT users_pkey PRIMARY KEY (id_user),
    CONSTRAINT users_status_check CHECK (((status)::text = ANY
                                          (ARRAY [('ACTIVE'::character varying)::text, ('INACTIVE'::character varying)::text, ('BANNED'::character varying)::text])))
);


-- DROP SCHEMA authz;

CREATE SCHEMA IF NOT EXISTS authz AUTHORIZATION root;
-- authz.feature definition

-- Drop table

-- DROP TABLE authz.feature;

CREATE TABLE authz.feature
(
    id_feature     uuid         NOT NULL,
    enabled        bool         NULL,
    feature_group  varchar(255) NULL,
    feature_name   varchar(255) NULL,
    icon_component varchar(255) NULL,
    CONSTRAINT feature_pkey PRIMARY KEY (id_feature)
);


-- authz.roles definition

-- Drop table

-- DROP TABLE authz.roles;

CREATE TABLE authz.roles
(
    id_role     varchar(255) NOT NULL,
    description varchar(255) NULL,
    role_name   varchar(255) NULL,
    CONSTRAINT roles_pkey PRIMARY KEY (id_role)
);


-- authz.feature_roles definition

-- Drop table

-- DROP TABLE authz.feature_roles;

CREATE TABLE authz.feature_roles
(
    id_feature uuid         NOT NULL,
    id_role    varchar(255) NOT NULL,
    CONSTRAINT feature_roles_pkey PRIMARY KEY (id_feature, id_role),
    CONSTRAINT fk55pr8u5aj296640rwn4rq4hoc FOREIGN KEY (id_feature) REFERENCES authz.feature (id_feature),
    CONSTRAINT fkipym0hbxbg0rh8srwcnlqsnv7 FOREIGN KEY (id_role) REFERENCES authz.roles (id_role)
);


-- DROP SCHEMA public;

CREATE SCHEMA IF NOT EXISTS public AUTHORIZATION pg_database_owner;

COMMENT ON SCHEMA public IS 'standard public schema';


-- DROP SCHEMA registration;

CREATE SCHEMA IF NOT EXISTS registration AUTHORIZATION root;
-- registration.registration definition

-- Drop table

-- DROP TABLE registration.registration;

CREATE TABLE registration.registration
(
    id_registration     uuid         NOT NULL,
    birth_date          date         NULL,
    "name"              varchar(255) NULL,
    phone               varchar(255) NULL,
    referred_by         varchar(255) NULL,
    registration_date   date         NULL,
    registration_status varchar(255) NULL,
    branch              int4         NULL,
    CONSTRAINT registration_pkey PRIMARY KEY (id_registration),
    CONSTRAINT registration_registration_status_check CHECK (((registration_status)::text = ANY
                                                              (ARRAY [('REGISTERED'::character varying)::text, ('ENROLLED'::character varying)::text, ('TRIAL'::character varying)::text, ('COMPLETED'::character varying)::text])))
);


-- DROP SCHEMA tournament;

CREATE SCHEMA IF NOT EXISTS tournament AUTHORIZATION root;

-- DROP SEQUENCE tournament.age_group_id_age_group_seq;

CREATE SEQUENCE tournament.age_group_id_age_group_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 2147483647
    START 1
    CACHE 1
    NO CYCLE;

-- DROP SEQUENCE tournament.belt_group_id_belt_group_seq;

CREATE SEQUENCE tournament.belt_group_id_belt_group_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 2147483647
    START 1
    CACHE 1
    NO CYCLE;

-- DROP SEQUENCE tournament.poomsae_content_id_poomsae_content_seq;

CREATE SEQUENCE tournament.poomsae_content_id_poomsae_content_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 2147483647
    START 1
    CACHE 1
    NO CYCLE;

-- DROP SEQUENCE tournament.sparring_content_id_sparring_content_seq;

CREATE SEQUENCE tournament.sparring_content_id_sparring_content_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 2147483647
    START 1
    CACHE 1
    NO CYCLE;

-- tournament.age_group definition

-- Drop table

-- DROP TABLE tournament.age_group;

CREATE TABLE tournament.age_group
(
    id_age_group   int4 GENERATED BY DEFAULT AS IDENTITY ( INCREMENT BY 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1 NO CYCLE) NOT NULL,
    age_group_name varchar(255)                                                                                                    NULL,
    max_age        int4                                                                                                            NULL,
    is_active      bool                                                                                                            NULL,
    min_age        int4                                                                                                            NULL,
    CONSTRAINT age_group_pkey PRIMARY KEY (id_age_group)
);


-- tournament.belt_group definition

-- Drop table

-- DROP TABLE tournament.belt_group;

CREATE TABLE tournament.belt_group
(
    id_belt_group   int4 GENERATED BY DEFAULT AS IDENTITY ( INCREMENT BY 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1 NO CYCLE) NOT NULL,
    belt_group_name varchar(255)                                                                                                    NULL,
    is_active       bool                                                                                                            NULL,
    end_belt        varchar(255)                                                                                                    NULL,
    start_belt      varchar(255)                                                                                                    NULL,
    CONSTRAINT belt_group_end_belt_check CHECK (((end_belt)::text = ANY
                                                 ((ARRAY ['C10'::character varying, 'C9'::character varying, 'C8'::character varying, 'C7'::character varying, 'C6'::character varying, 'C5'::character varying, 'C4'::character varying, 'C3'::character varying, 'C2'::character varying, 'C1'::character varying, 'I_DANG'::character varying, 'II_DANG'::character varying, 'III_DANG'::character varying, 'IV_DANG'::character varying, 'V_DANG'::character varying, 'VI_DANG'::character varying, 'VII_DANG'::character varying, 'VIII_DANG'::character varying, 'IX_DANG'::character varying])::text[]))),
    CONSTRAINT belt_group_pkey PRIMARY KEY (id_belt_group),
    CONSTRAINT belt_group_start_belt_check CHECK (((start_belt)::text = ANY
                                                   ((ARRAY ['C10'::character varying, 'C9'::character varying, 'C8'::character varying, 'C7'::character varying, 'C6'::character varying, 'C5'::character varying, 'C4'::character varying, 'C3'::character varying, 'C2'::character varying, 'C1'::character varying, 'I_DANG'::character varying, 'II_DANG'::character varying, 'III_DANG'::character varying, 'IV_DANG'::character varying, 'V_DANG'::character varying, 'VI_DANG'::character varying, 'VII_DANG'::character varying, 'VIII_DANG'::character varying, 'IX_DANG'::character varying])::text[])))
);


-- tournament.bracket_node definition

-- Drop table

-- DROP TABLE tournament.bracket_node;

CREATE TABLE tournament.bracket_node
(
    id_bracket     uuid NOT NULL,
    child_node_id  int4 NULL,
    level_node     int4 NULL,
    parent_node_id int4 NULL,
    participants   int4 NULL,
    CONSTRAINT bracket_node_pkey PRIMARY KEY (id_bracket)
);


-- tournament.poomsae_content definition

-- Drop table

-- DROP TABLE tournament.poomsae_content;

CREATE TABLE tournament.poomsae_content
(
    id_poomsae_content int4 GENERATED BY DEFAULT AS IDENTITY ( INCREMENT BY 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1 NO CYCLE) NOT NULL,
    content_name       varchar(255)                                                                                                    NULL,
    is_active          bool                                                                                                            NULL,
    CONSTRAINT poomsae_content_content_name_check CHECK (((content_name)::text = ANY
                                                          (ARRAY [('MALE_INDIVIDUAL'::character varying)::text, ('FEMALE_INDIVIDUAL'::character varying)::text, ('PAIR'::character varying)::text, ('MALE_TEAM'::character varying)::text, ('FEMALE_TEAM'::character varying)::text, ('MIXED_TEAM'::character varying)::text]))),
    CONSTRAINT poomsae_content_pkey PRIMARY KEY (id_poomsae_content)
);


-- tournament.sparring_content definition

-- Drop table

-- DROP TABLE tournament.sparring_content;

CREATE TABLE tournament.sparring_content
(
    id_sparring_content int4 GENERATED BY DEFAULT AS IDENTITY ( INCREMENT BY 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1 NO CYCLE) NOT NULL,
    weight_class        int4                                                                                                            NOT NULL,
    CONSTRAINT sparring_content_pkey PRIMARY KEY (id_sparring_content)
);


-- tournament.tournament definition

-- Drop table

-- DROP TABLE tournament.tournament;

CREATE TABLE tournament.tournament
(
    id_tournament    uuid         NOT NULL,
    "location"       varchar(255) NULL,
    tournament_date  date         NULL,
    tournament_name  varchar(255) NULL,
    tournament_scope varchar(255) NULL,
    tournament_state varchar(255) NULL,
    CONSTRAINT tournament_pkey PRIMARY KEY (id_tournament),
    CONSTRAINT tournament_tournament_scope_check CHECK (((tournament_scope)::text = ANY
                                                         ((ARRAY ['EXCHANGE_EVENT'::character varying, 'INTERNAL_TOURNAMENT'::character varying, 'CITY_TOURNAMENT'::character varying, 'NATIONAL_TOURNAMENT'::character varying, 'NATIONAL_CHAMPIONSHIP'::character varying])::text[]))),
    CONSTRAINT tournament_tournament_state_check CHECK (((tournament_state)::text = ANY
                                                         ((ARRAY ['UPCOMING'::character varying, 'ONGOING'::character varying, 'COMPLETED'::character varying, 'CANCELLED'::character varying])::text[])))
);


-- tournament.poomsae_combination definition

-- Drop table

-- DROP TABLE tournament.poomsae_combination;

CREATE TABLE tournament.poomsae_combination
(
    id_poomsae_combination uuid         NOT NULL,
    poomsae_mode           varchar(255) NULL,
    age_group              int4         NULL,
    belt_group             int4         NULL,
    poomsae_content        int4         NULL,
    id_tournament          uuid         NULL,
    CONSTRAINT poomsae_combination_pkey PRIMARY KEY (id_poomsae_combination),
    CONSTRAINT poomsae_combination_poomsae_mode_check CHECK (((poomsae_mode)::text = ANY
                                                              (ARRAY [('ELIMINATION'::character varying)::text, ('ROUND_ROBIN'::character varying)::text]))),
    CONSTRAINT fk9ddwb6m7mcks4amrtp6w8jk1f FOREIGN KEY (belt_group) REFERENCES tournament.belt_group (id_belt_group),
    CONSTRAINT fklpellojbvw2jfar9t738vfrpc FOREIGN KEY (id_tournament) REFERENCES tournament.tournament (id_tournament),
    CONSTRAINT fkq369liqog8m1ypq2hdb1twsvk FOREIGN KEY (poomsae_content) REFERENCES tournament.poomsae_content (id_poomsae_content),
    CONSTRAINT fkqnkro1llk82xj9qfs0rry943u FOREIGN KEY (age_group) REFERENCES tournament.age_group (id_age_group)
);


-- tournament.sparring_combination definition

-- Drop table

-- DROP TABLE tournament.sparring_combination;

CREATE TABLE tournament.sparring_combination
(
    id_sparring_combination uuid         NOT NULL,
    gender                  varchar(255) NULL,
    is_active               bool         NOT NULL,
    age_group               int4         NULL,
    sparring_content        int4         NULL,
    CONSTRAINT sparring_combination_gender_check CHECK (((gender)::text = ANY
                                                         (ARRAY [('MALE'::character varying)::text, ('FEMALE'::character varying)::text]))),
    CONSTRAINT sparring_combination_pkey PRIMARY KEY (id_sparring_combination),
    CONSTRAINT fkd70n7wgnbrxsw52jw6s7lers6 FOREIGN KEY (sparring_content) REFERENCES tournament.sparring_content (id_sparring_content),
    CONSTRAINT fkiya8axyvur2qf9avhf3dcrn3i FOREIGN KEY (age_group) REFERENCES tournament.age_group (id_age_group)
);


-- tournament.tournament_match definition

-- Drop table

-- DROP TABLE tournament.tournament_match;

CREATE TABLE tournament.tournament_match
(
    id_combination             uuid         NOT NULL,
    target_node                int4         NOT NULL,
    tournament                 uuid         NOT NULL,
    duration                   numeric(21)  NULL,
    is_first_node              bool         NOT NULL,
    participants               int4         NULL,
    "session"                  varchar(255) NULL,
    tournament_type            varchar(255) NULL,
    left_match_id_combination  uuid         NULL,
    left_match_target_node     int4         NULL,
    left_match_tournament      uuid         NULL,
    right_match_id_combination uuid         NULL,
    right_match_target_node    int4         NULL,
    right_match_tournament     uuid         NULL,
    CONSTRAINT tournament_match_pkey PRIMARY KEY (id_combination, target_node, tournament),
    CONSTRAINT tournament_match_session_check CHECK (((session)::text = ANY
                                                      (ARRAY [('AM'::character varying)::text, ('PM'::character varying)::text]))),
    CONSTRAINT tournament_match_tournament_type_check CHECK (((tournament_type)::text = ANY
                                                              (ARRAY [('POOMSAE'::character varying)::text, ('SPARRING'::character varying)::text]))),
    CONSTRAINT fk9a41lacvn258xwybp9gyov0ag FOREIGN KEY (tournament) REFERENCES tournament.tournament (id_tournament),
    CONSTRAINT fkiu8nxt1obmt3g487eto1bss5l FOREIGN KEY (left_match_id_combination, left_match_target_node,
                                                        left_match_tournament) REFERENCES tournament.tournament_match (id_combination, target_node, tournament),
    CONSTRAINT fkrls0gg8nnvn5gnxcf63xprq2l FOREIGN KEY (right_match_id_combination, right_match_target_node,
                                                        right_match_tournament) REFERENCES tournament.tournament_match (id_combination, target_node, tournament)
);


-- tournament.poomsae_history definition

-- Drop table

-- DROP TABLE tournament.poomsae_history;

CREATE TABLE tournament.poomsae_history
(
    id_poomsae_history uuid NOT NULL,
    has_won            bool NULL,
    level_node         int4 NULL,
    source_node        int4 NULL,
    target_node        int4 NULL,
    poomsae_list       uuid NULL,
    CONSTRAINT poomsae_history_pkey PRIMARY KEY (id_poomsae_history)
);


-- tournament.sparring_history definition

-- Drop table

-- DROP TABLE tournament.sparring_history;

CREATE TABLE tournament.sparring_history
(
    id_sparring_history            uuid NOT NULL,
    has_won                        bool NULL,
    level_node                     int4 NULL,
    source_node                    int4 NULL,
    target_node                    int4 NULL,
    sparring_list_id_sparring_list uuid NULL,
    CONSTRAINT sparring_history_pkey PRIMARY KEY (id_sparring_history)
);


-- DROP SCHEMA training;

CREATE SCHEMA IF NOT EXISTS training AUTHORIZATION root;

-- DROP SEQUENCE training.branch_id_branch_seq;

CREATE SEQUENCE training.branch_id_branch_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 2147483647
    START 1
    CACHE 1
    NO CYCLE;

-- training.branch definition

-- Drop table

-- DROP TABLE training.branch;

CREATE TABLE training.branch
(
    id_branch int4 GENERATED BY DEFAULT AS IDENTITY ( INCREMENT BY 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1 NO CYCLE) NOT NULL,
    address   varchar(255)                                                                                                    NULL,
    avatar    varchar(255)                                                                                                    NULL,
    is_new    bool                                                                                                            NULL,
    title     varchar(255)                                                                                                    NULL,
    is_active bool                                                                                                            NULL,
    CONSTRAINT branch_pkey PRIMARY KEY (id_branch)
);


-- training.class_session definition

-- Drop table

-- DROP TABLE training.class_session;

CREATE TABLE training.class_session
(
    id_class_session varchar(255) NOT NULL,
    class_level      varchar(255) NULL,
    is_active        bool         NULL,
    "location"       varchar(255) NULL,
    "session"        varchar(255) NULL,
    shift            varchar(255) NULL,
    weekday          varchar(255) NULL,
    branch           int4         NULL,
    CONSTRAINT class_session_class_level_check CHECK (((class_level)::text = ANY
                                                       (ARRAY [('ADVANCED'::character varying)::text, ('BASIC'::character varying)::text, ('KID'::character varying)::text]))),
    CONSTRAINT class_session_location_check CHECK (((location)::text = ANY
                                                    (ARRAY [('INDOOR'::character varying)::text, ('OUTSIDE'::character varying)::text]))),
    CONSTRAINT class_session_pkey PRIMARY KEY (id_class_session),
    CONSTRAINT class_session_session_check CHECK (((session)::text = ANY
                                                   (ARRAY [('AM'::character varying)::text, ('PM'::character varying)::text]))),
    CONSTRAINT class_session_shift_check CHECK (((shift)::text = ANY
                                                 (ARRAY [('SHIFT_1'::character varying)::text, ('SHIFT_2'::character varying)::text]))),
    CONSTRAINT class_session_weekday_check CHECK (((weekday)::text = ANY
                                                   (ARRAY [('SUNDAY'::character varying)::text, ('MONDAY'::character varying)::text, ('TUESDAY'::character varying)::text, ('WEDNESDAY'::character varying)::text, ('THURSDAY'::character varying)::text, ('FRIDAY'::character varying)::text, ('SATURDAY'::character varying)::text]))),
    CONSTRAINT fknuvkir6hp9lv9p6cxcbnvqx62 FOREIGN KEY (branch) REFERENCES training.branch (id_branch)
);


-- training.coach definition

-- Drop table

-- DROP TABLE training.coach;

CREATE TABLE training.coach
(
    belt_level varchar(255) NULL,
    birth_date date         NULL,
    is_active  bool         NULL,
    "name"     varchar(255) NULL,
    phone      varchar(255) NULL,
    "position" varchar(255) NULL,
    id_user    uuid         NOT NULL,
    CONSTRAINT coach_belt_level_check CHECK (((belt_level)::text = ANY
                                              (ARRAY [('C10'::character varying)::text, ('C9'::character varying)::text, ('C8'::character varying)::text, ('C7'::character varying)::text, ('C6'::character varying)::text, ('C5'::character varying)::text, ('C4'::character varying)::text, ('C3'::character varying)::text, ('C2'::character varying)::text, ('C1'::character varying)::text, ('I_DANG'::character varying)::text, ('II_DANG'::character varying)::text, ('III_DANG'::character varying)::text, ('IV_DANG'::character varying)::text, ('V_DANG'::character varying)::text, ('VI_DANG'::character varying)::text, ('VII_DANG'::character varying)::text, ('VIII_DANG'::character varying)::text, ('IX_DANG'::character varying)::text]))),
    CONSTRAINT coach_pkey PRIMARY KEY (id_user),
    CONSTRAINT coach_position_check CHECK ("position" IN ('ADMIN', 'HEAD_COACH', 'SENIOR_MANAGER', 'MIDDLE_MANAGER', 'COACH'))
);


-- training.coach_class_session definition

-- Drop table

-- DROP TABLE training.coach_class_session;

CREATE TABLE training.coach_class_session
(
    id_class_session varchar(255) NOT NULL,
    id_user          uuid         NOT NULL,
    class_session    varchar(255) NULL,
    CONSTRAINT coach_class_session_pkey PRIMARY KEY (id_class_session, id_user)
);


-- training.student definition

-- Drop table

-- DROP TABLE training.student;

CREATE TABLE training.student
(
    belt_level  varchar(255) NULL,
    birth_date  date         NULL,
    end_date    date         NULL,
    id_national varchar(255) NULL,
    is_active   bool         NOT NULL,
    "member"    varchar(255) NULL,
    "name"      varchar(255) NULL,
    phone       varchar(255) NULL,
    start_date  date         NULL,
    id_user     uuid         NOT NULL,
    branch      int4         NULL,
    CONSTRAINT student_belt_level_check CHECK (((belt_level)::text = ANY
                                                (ARRAY [('C10'::character varying)::text, ('C9'::character varying)::text, ('C8'::character varying)::text, ('C7'::character varying)::text, ('C6'::character varying)::text, ('C5'::character varying)::text, ('C4'::character varying)::text, ('C3'::character varying)::text, ('C2'::character varying)::text, ('C1'::character varying)::text, ('I_DANG'::character varying)::text, ('II_DANG'::character varying)::text, ('III_DANG'::character varying)::text, ('IV_DANG'::character varying)::text, ('V_DANG'::character varying)::text, ('VI_DANG'::character varying)::text, ('VII_DANG'::character varying)::text, ('VIII_DANG'::character varying)::text, ('IX_DANG'::character varying)::text]))),
    CONSTRAINT student_member_check CHECK (((member)::text = ANY
                                            (ARRAY [('NORMAL'::character varying)::text, ('GOLD'::character varying)::text, ('SILVER'::character varying)::text, ('BRONZE'::character varying)::text, ('PREMIUM'::character varying)::text]))),
    CONSTRAINT student_pkey PRIMARY KEY (id_user)
);

-- =========================================================
-- PHẦN THÊM MỚI: TẠO INDEX GIN CHO TÌM KIẾM HỌC VIÊN
-- =========================================================

-- 1. Index cho Số điện thoại (Tìm chuỗi con bất kỳ: 090, 123...)
CREATE INDEX IF NOT EXISTS idx_student_phone_trgm
    ON training.student
        USING gin (phone public.gin_trgm_ops);

-- 2. Index cho Tên (Tìm tiếng Việt không dấu: "Huy" tìm ra "Huy", "Hùy")
-- Lưu ý: Sử dụng hàm public.f_unaccent đã tạo ở đầu file
CREATE INDEX IF NOT EXISTS idx_student_name_trgm
    ON training.student
        USING gin (public.f_unaccent("name") public.gin_trgm_ops);

-- =========================================================


-- training.student_class_session definition

-- Drop table

-- DROP TABLE training.student_class_session;

CREATE TABLE training.student_class_session
(
    id_class_session varchar(255) NOT NULL,
    id_user          uuid         NOT NULL,
    CONSTRAINT student_class_session_pkey PRIMARY KEY (id_class_session, id_user)
);
CREATE INDEX idx_class_session ON training.student_class_session USING btree (id_class_session);

-- =========================================================
-- PHẦN ALTER TABLE: PERMISSIONS & FOREIGN KEYS
-- =========================================================

-- Permissions

ALTER TABLE achievement.poomsae_list OWNER TO root;
GRANT ALL ON TABLE achievement.poomsae_list TO root;

ALTER TABLE achievement.sparring_list OWNER TO root;
GRANT ALL ON TABLE achievement.sparring_list TO root;

-- achievement.poomsae_list foreign keys

ALTER TABLE achievement.poomsae_list
    ADD CONSTRAINT fkah1j5q085bq4k1xetxr7hfyi4 FOREIGN KEY (tournament) REFERENCES tournament.tournament (id_tournament);
ALTER TABLE achievement.poomsae_list
    ADD CONSTRAINT fkdrxsm4hs5xexgkh0o985hk9gb FOREIGN KEY (student_id_user) REFERENCES training.student (id_user);
ALTER TABLE achievement.poomsae_list
    ADD CONSTRAINT fkeav7e9hitb68tilom2oe59htv FOREIGN KEY (poomsae_combination) REFERENCES tournament.poomsae_combination (id_poomsae_combination);
ALTER TABLE achievement.poomsae_list
    ADD CONSTRAINT fkroe8vxxke9sw7lipvf4cpai3n FOREIGN KEY (branch) REFERENCES training.branch (id_branch);


-- achievement.sparring_list foreign keys

ALTER TABLE achievement.sparring_list
    ADD CONSTRAINT fk6bfvg7flsu9ldu79sd8ps6si1 FOREIGN KEY (tournament) REFERENCES tournament.tournament (id_tournament);
ALTER TABLE achievement.sparring_list
    ADD CONSTRAINT fkjsvnmktsibp0o310t6hblu21 FOREIGN KEY (sparring_combination) REFERENCES tournament.sparring_combination (id_sparring_combination);
ALTER TABLE achievement.sparring_list
    ADD CONSTRAINT fkpb54v7xe2h94yfojcapohq7pd FOREIGN KEY (student_id_user) REFERENCES training.student (id_user);
ALTER TABLE achievement.sparring_list
    ADD CONSTRAINT fkq78ps6jxdbcn911u10qfx8lp FOREIGN KEY (branch) REFERENCES training.branch (id_branch);


-- Permissions

GRANT ALL ON SCHEMA achievement TO root;

ALTER TABLE association.bracket_node_links OWNER TO root;
GRANT ALL ON TABLE association.bracket_node_links TO root;

ALTER TABLE association.branch_weekdays OWNER TO root;
GRANT ALL ON TABLE association.branch_weekdays TO root;


-- association.bracket_node_links foreign keys

ALTER TABLE association.bracket_node_links
    ADD CONSTRAINT fkpdmily4vk5q50coga965e7l0p FOREIGN KEY (bracket_node_id) REFERENCES tournament.bracket_node (id_bracket);


-- association.branch_weekdays foreign keys

ALTER TABLE association.branch_weekdays
    ADD CONSTRAINT fkc1y8y7be74586au5cxh2lnxvp FOREIGN KEY (id_branch) REFERENCES training.branch (id_branch);


-- Permissions

GRANT ALL ON SCHEMA association TO root;

ALTER TABLE attendance.coach_attendance OWNER TO root;
GRANT ALL ON TABLE attendance.coach_attendance TO root;

ALTER TABLE attendance.student_attendance OWNER TO root;
GRANT ALL ON TABLE attendance.student_attendance TO root;

ALTER TABLE attendance.trial_attendance OWNER TO root;
GRANT ALL ON TABLE attendance.trial_attendance TO root;


-- attendance.coach_attendance foreign keys

ALTER TABLE attendance.coach_attendance
    ADD CONSTRAINT fk5tmvd6r68dnvrp7kwye8jde05 FOREIGN KEY (coach_id_user) REFERENCES training.coach (id_user);
ALTER TABLE attendance.coach_attendance
    ADD CONSTRAINT fk95v6q296fklhwaivxjkwrn7hy FOREIGN KEY (class_session) REFERENCES training.class_session (id_class_session);


-- attendance.student_attendance foreign keys

ALTER TABLE attendance.student_attendance
    ADD CONSTRAINT fk8cjj2v2f6rm8jy84rcrqx273i FOREIGN KEY (student_id_user) REFERENCES training.student (id_user);
ALTER TABLE attendance.student_attendance
    ADD CONSTRAINT fk9o5menbayr9ruuqp0shh35rql FOREIGN KEY (evaluation_coach_id_user) REFERENCES training.coach (id_user);
ALTER TABLE attendance.student_attendance
    ADD CONSTRAINT fkins98793o2nfg8sih28dp1be FOREIGN KEY (attendance_coach_id_user) REFERENCES training.coach (id_user);
ALTER TABLE attendance.student_attendance
    ADD CONSTRAINT fkq19qfilady66ks950q001kjl FOREIGN KEY (class_session) REFERENCES training.class_session (id_class_session);


-- attendance.trial_attendance foreign keys

ALTER TABLE attendance.trial_attendance
    ADD CONSTRAINT fk4qvbndk69d30khdwqg33a3nv8 FOREIGN KEY (registration_id_registration) REFERENCES registration.registration (id_registration);
ALTER TABLE attendance.trial_attendance
    ADD CONSTRAINT fkimnov3af38hxbblv26f373tx2 FOREIGN KEY (class_session) REFERENCES training.class_session (id_class_session);
ALTER TABLE attendance.trial_attendance
    ADD CONSTRAINT fkinfclq50rrqsj203rbexnyvet FOREIGN KEY (evaluation_coach_id_user) REFERENCES training.coach (id_user);
ALTER TABLE attendance.trial_attendance
    ADD CONSTRAINT fkt02mc32t2dlc3tm3g12fxexyy FOREIGN KEY (attendance_coach_id_user) REFERENCES training.coach (id_user);


-- Permissions

GRANT ALL ON SCHEMA attendance TO root;

ALTER TABLE authentication.user_tokens OWNER TO root;
GRANT ALL ON TABLE authentication.user_tokens TO root;

ALTER TABLE authentication.users OWNER TO root;
GRANT ALL ON TABLE authentication.users TO root;


-- authentication.user_tokens foreign keys

ALTER TABLE authentication.user_tokens
    ADD CONSTRAINT fk4u4y8tipw949t8xu0q59rwwws FOREIGN KEY (id_user) REFERENCES authentication.users (id_user);


-- authentication.users foreign keys

ALTER TABLE authentication.users
    ADD CONSTRAINT fk4c6vlshk8x83ifeoggi3exg3k FOREIGN KEY ("role") REFERENCES authz.roles (id_role);


-- Permissions

GRANT ALL ON SCHEMA authentication TO root;

ALTER TABLE authz.feature OWNER TO root;
GRANT ALL ON TABLE authz.feature TO root;

ALTER TABLE authz.roles OWNER TO root;
GRANT ALL ON TABLE authz.roles TO root;

ALTER TABLE authz.feature_roles OWNER TO root;
GRANT ALL ON TABLE authz.feature_roles TO root;


-- Permissions

GRANT ALL ON SCHEMA authz TO root;

-- Permissions

GRANT ALL ON SCHEMA public TO pg_database_owner;
GRANT USAGE ON SCHEMA public TO public;

ALTER TABLE registration.registration OWNER TO root;
GRANT ALL ON TABLE registration.registration TO root;


-- registration.registration foreign keys

ALTER TABLE registration.registration
    ADD CONSTRAINT fk3hx94smksccm13x9js8altcy FOREIGN KEY (branch) REFERENCES training.branch (id_branch);


-- Permissions

GRANT ALL ON SCHEMA registration TO root;

-- Permissions

ALTER SEQUENCE tournament.age_group_id_age_group_seq OWNER TO root;
GRANT ALL ON SEQUENCE tournament.age_group_id_age_group_seq TO root;

ALTER SEQUENCE tournament.belt_group_id_belt_group_seq OWNER TO root;
GRANT ALL ON SEQUENCE tournament.belt_group_id_belt_group_seq TO root;

ALTER SEQUENCE tournament.poomsae_content_id_poomsae_content_seq OWNER TO root;
GRANT ALL ON SEQUENCE tournament.poomsae_content_id_poomsae_content_seq TO root;

ALTER SEQUENCE tournament.sparring_content_id_sparring_content_seq OWNER TO root;
GRANT ALL ON SEQUENCE tournament.sparring_content_id_sparring_content_seq TO root;

ALTER TABLE tournament.age_group OWNER TO root;
GRANT ALL ON TABLE tournament.age_group TO root;

ALTER TABLE tournament.belt_group OWNER TO root;
GRANT ALL ON TABLE tournament.belt_group TO root;

ALTER TABLE tournament.bracket_node OWNER TO root;
GRANT ALL ON TABLE tournament.bracket_node TO root;

ALTER TABLE tournament.poomsae_content OWNER TO root;
GRANT ALL ON TABLE tournament.poomsae_content TO root;

ALTER TABLE tournament.sparring_content OWNER TO root;
GRANT ALL ON TABLE tournament.sparring_content TO root;

ALTER TABLE tournament.tournament OWNER TO root;
GRANT ALL ON TABLE tournament.tournament TO root;

ALTER TABLE tournament.poomsae_combination OWNER TO root;
GRANT ALL ON TABLE tournament.poomsae_combination TO root;

ALTER TABLE tournament.sparring_combination OWNER TO root;
GRANT ALL ON TABLE tournament.sparring_combination TO root;

ALTER TABLE tournament.tournament_match OWNER TO root;
GRANT ALL ON TABLE tournament.tournament_match TO root;

ALTER TABLE tournament.poomsae_history OWNER TO root;
GRANT ALL ON TABLE tournament.poomsae_history TO root;

ALTER TABLE tournament.sparring_history OWNER TO root;
GRANT ALL ON TABLE tournament.sparring_history TO root;


-- tournament.poomsae_history foreign keys

ALTER TABLE tournament.poomsae_history
    ADD CONSTRAINT fk7v7fhdg7lhseuawimmu44yaq2 FOREIGN KEY (poomsae_list) REFERENCES achievement.poomsae_list (id_poomsae_list);


-- tournament.sparring_history foreign keys

ALTER TABLE tournament.sparring_history
    ADD CONSTRAINT fksvj56pxdumbpk68m4gxny67wu FOREIGN KEY (sparring_list_id_sparring_list) REFERENCES achievement.sparring_list (id_sparring_list);


-- Permissions

GRANT ALL ON SCHEMA tournament TO root;

-- Permissions

ALTER SEQUENCE training.branch_id_branch_seq OWNER TO root;
GRANT ALL ON SEQUENCE training.branch_id_branch_seq TO root;

ALTER TABLE training.branch OWNER TO root;
GRANT ALL ON TABLE training.branch TO root;

ALTER TABLE training.class_session OWNER TO root;
GRANT ALL ON TABLE training.class_session TO root;

ALTER TABLE training.coach OWNER TO root;
GRANT ALL ON TABLE training.coach TO root;

ALTER TABLE training.coach_class_session OWNER TO root;
GRANT ALL ON TABLE training.coach_class_session TO root;

ALTER TABLE training.student OWNER TO root;
GRANT ALL ON TABLE training.student TO root;

ALTER TABLE training.student_class_session OWNER TO root;
GRANT ALL ON TABLE training.student_class_session TO root;


-- training.coach foreign keys

ALTER TABLE training.coach
    ADD CONSTRAINT fkabtrowbsxsqsu0h9rntsxg3yk FOREIGN KEY (id_user) REFERENCES authentication.users (id_user);


-- training.coach_class_session foreign keys

ALTER TABLE training.coach_class_session
    ADD CONSTRAINT fkbiwiuxx5jv6th3v1le8f99g80 FOREIGN KEY (class_session) REFERENCES training.class_session (id_class_session);
ALTER TABLE training.coach_class_session
    ADD CONSTRAINT fksxvdauunxffkkfyxkx4gllnn8 FOREIGN KEY (id_user) REFERENCES training.coach (id_user);


-- training.student foreign keys

ALTER TABLE training.student
    ADD CONSTRAINT fk43oyo2j5buhsikw7ruch2tbgf FOREIGN KEY (branch) REFERENCES training.branch (id_branch);
ALTER TABLE training.student
    ADD CONSTRAINT fkpsk2g068q077bvls9y3eypywi FOREIGN KEY (id_user) REFERENCES authentication.users (id_user);


-- training.student_class_session foreign keys

ALTER TABLE training.student_class_session
    ADD CONSTRAINT fkims3o00dfyaobp4pbycy09gn0 FOREIGN KEY (id_user) REFERENCES training.student (id_user);
ALTER TABLE training.student_class_session
    ADD CONSTRAINT fkl5vipp7vhv5gno2emlkkrjbu FOREIGN KEY (id_class_session) REFERENCES training.class_session (id_class_session);


-- Permissions

GRANT ALL ON SCHEMA training TO root;
