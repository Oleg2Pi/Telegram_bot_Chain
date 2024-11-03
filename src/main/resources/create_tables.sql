CREATE TABLE person
(
    id          SERIAL PRIMARY KEY,
    first_name  VARCHAR(20)  NOT NULL,
    last_name   VARCHAR(40)  NOT NULL,
    username_tg VARCHAR(124) NOT NULL,
    phone       VARCHAR(12)
);

CREATE TABLE image_of_person
(
    id   SERIAL PRIMARY KEY,
    person_id INT REFERENCES person(id) ON DELETE CASCADE NOT NULL ,
    file BYTEA NOT NULL
);

CREATE TABLE executor
(
    id SERIAL PRIMARY KEY ,
    person_id INT REFERENCES person(id) ON DELETE CASCADE NOT NULL
);

CREATE TABLE activity_area
(
    id SERIAL PRIMARY KEY,
    type VARCHAR(124) NOT NULL
);

CREATE TABLE work_experience
(
    id SERIAL PRIMARY KEY ,
    category VARCHAR(20) NOT NULL
);

CREATE TABLE user_status
(
    id SERIAL PRIMARY KEY ,
    category VARCHAR(60) NOT NULL
);

CREATE TABLE resume
(
    id SERIAL PRIMARY KEY ,
    executor_id INT REFERENCES executor(id) ON DELETE CASCADE NOT NULL ,
    activity_area_id INT REFERENCES activity_area(id),
    work_experience_id INT REFERENCES work_experience(id),
    user_status_id INT REFERENCES user_status(id),
    information_yourself TEXT
);

CREATE TABLE work
(
    id SERIAL PRIMARY KEY ,
    executor_id INT REFERENCES executor(id) ON DELETE CASCADE NOT NULL ,
    name VARCHAR(124) NOT NULL ,
    date_added TIMESTAMP NOT NULL,
    description TEXT NOT NULL
);

CREATE TABLE image_video
(
    id SERIAL PRIMARY KEY ,
    work_id INT REFERENCES work(id) ON DELETE CASCADE NOT NULL,
    file BYTEA NOT NULL,
    type VARCHAR(124) NOT NULL
);