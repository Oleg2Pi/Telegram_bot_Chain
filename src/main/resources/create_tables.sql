CREATE TABLE IF NOT EXISTS name_social_networks
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(124) NOT NULL
);

CREATE TABLE IF NOT EXISTS persons
(
    id          BIGSERIAL PRIMARY KEY,
    first_name  VARCHAR(124) NOT NULL,
    last_name   VARCHAR(124) NOT NULL,
    username_tg VARCHAR(124) NOT NULL,
    work_phone  VARCHAR(12)  NOT NULL,
    email       VARCHAR(256) NOT NULL,
    chat_id	BIGINT	     NOT NULL
);

CREATE TABLE IF NOT EXISTS social_networks
(
    id     BIGSERIAL PRIMARY KEY,
    name   INT REFERENCES name_social_networks (id)         NOT NULL,
    link   TEXT                                             NOT NULL,
    person BIGINT REFERENCES persons (id) ON DELETE CASCADE NOT NULL
);

CREATE TABLE IF NOT EXISTS status_subscription
(
    id   SERIAL PRIMARY KEY,
    type VARCHAR(30) NOT NULL
);

CREATE TABLE IF NOT EXISTS subscriptions
(
    id           BIGSERIAL PRIMARY KEY,
    person       BIGINT REFERENCES persons (id) ON DELETE CASCADE NOT NULL,
    payment_date DATE                                             NOT NULL,
    status       INT REFERENCES status_subscription (id)          NOT NULL
);

CREATE TABLE IF NOT EXISTS review
(
    id          BIGSERIAL PRIMARY KEY,
    from_person BIGINT                                           REFERENCES persons (id) ON DELETE SET NULL,
    to_person   BIGINT REFERENCES persons (id) ON DELETE CASCADE NOT NULL,
    review_text TEXT                                             NOT NULL
);

CREATE TABLE IF NOT EXISTS employer
(
    id     BIGSERIAL PRIMARY KEY,
    person BIGINT REFERENCES persons (id) ON DELETE CASCADE NOT NULL
);

CREATE TABLE IF NOT EXISTS company
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(124)                                      NOT NULL,
    address     VARCHAR(256)                                      NOT NULL,
    description TEXT                                              NOT NULL,
    employer    BIGINT REFERENCES employer (id) ON DELETE CASCADE NOT NULL
);

CREATE TABLE IF NOT EXISTS executor
(
    id          BIGSERIAL PRIMARY KEY,
    person      BIGINT REFERENCES persons (id) ON DELETE CASCADE NOT NULL,
    description TEXT                                             NOT NULL,
    portfolio   BYTEA
);

CREATE TABLE IF NOT EXISTS subject_work
(
    id   SERIAL PRIMARY KEY,
    type VARCHAR(124) NOT NULL
);

CREATE TABLE IF NOT EXISTS form_employment
(
    id   SERIAL PRIMARY KEY,
    type VARCHAR(124) NOT NULL
);

CREATE TABLE IF NOT EXISTS skills
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(124) NOT NULL
);

CREATE TABLE IF NOT EXISTS type_status_vacancy
(
    id   SERIAL PRIMARY KEY,
    type VARCHAR(124) NOT NULL
);

CREATE TABLE IF NOT EXISTS responsibilities
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(124) NOT NULL
);

CREATE TABLE IF NOT EXISTS conditions
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(124) NOT NULL
);

CREATE TABLE IF NOT EXISTS vacancy
(
    id           BIGSERIAL PRIMARY KEY,
    employer     BIGINT REFERENCES employer (id) ON DELETE CASCADE NOT NULL,
    work_address VARCHAR(256),
    executor     BIGINT                                            REFERENCES executor (id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS status_vacancy
(
    id      BIGSERIAL PRIMARY KEY,
    type    INT REFERENCES type_status_vacancy (id)          NOT NULL,
    vacancy BIGINT REFERENCES vacancy (id) ON DELETE CASCADE NOT NULL,
    date_change DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS subject_vacancy
(
    subject INT REFERENCES subject_work (id),
    vacancy BIGINT REFERENCES vacancy (id) ON DELETE CASCADE,
    PRIMARY KEY (subject, vacancy)
);

CREATE TABLE IF NOT EXISTS form_vacancy
(
    form_employment INT REFERENCES form_employment (id),
    vacancy         BIGINT REFERENCES vacancy (id) ON DELETE CASCADE,
    PRIMARY KEY (form_employment, vacancy)
);

CREATE TABLE IF NOT EXISTS skill_vacancy
(
    skill   INT REFERENCES skills (id),
    vacancy BIGINT REFERENCES vacancy (id) ON DELETE CASCADE,
    PRIMARY KEY (skill, vacancy)
);

CREATE TABLE IF NOT EXISTS responsibilities_vacancy
(
    responsibility INT REFERENCES responsibilities (id),
    vacancy        BIGINT REFERENCES vacancy (id) ON DELETE CASCADE,
    PRIMARY KEY (responsibility, vacancy)
);

CREATE TABLE IF NOT EXISTS condition_vacancy
(
    condition INT REFERENCES conditions (id),
    vacancy   BIGINT REFERENCES vacancy (id) ON DELETE CASCADE,
    PRIMARY KEY (condition, vacancy)
);

CREATE TABLE IF NOT EXISTS responses
(
    vacancy  BIGINT REFERENCES vacancy (id) ON DELETE CASCADE,
    executor BIGINT REFERENCES executor (id) ON DELETE CASCADE,
    PRIMARY KEY (vacancy, executor)
);