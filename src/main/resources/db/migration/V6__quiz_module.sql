-- V6__quiz_module.sql
-- Flyway migration for Quiz Review module

CREATE TABLE quiz (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    offering_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NULL,
    date DATETIME NOT NULL,
    max_marks INT NOT NULL,
    CONSTRAINT fk_quiz_offering
        FOREIGN KEY (offering_id) REFERENCES course_offerings(id)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE quiz_question (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quiz_id BIGINT NOT NULL,
    question_number INT NOT NULL,
    max_marks INT NOT NULL,
    text TEXT NOT NULL,
    CONSTRAINT fk_question_quiz
        FOREIGN KEY (quiz_id) REFERENCES quiz(id)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE quiz_attempt (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quiz_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    obtained_marks INT NULL,
    status VARCHAR(20) NOT NULL,
    CONSTRAINT uq_attempt_quiz_user UNIQUE (quiz_id, user_id),
    CONSTRAINT fk_attempt_quiz
        FOREIGN KEY (quiz_id) REFERENCES quiz(id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_attempt_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE quiz_answer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    attempt_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    obtained_marks INT NULL,
    evaluator_comment TEXT NULL,
    CONSTRAINT fk_answer_attempt
        FOREIGN KEY (attempt_id) REFERENCES quiz_attempt(id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_answer_question
        FOREIGN KEY (question_id) REFERENCES quiz_question(id)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;
