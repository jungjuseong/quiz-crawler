CREATE TABLE IF NOT EXISTS quiz.quiz
(
    id int AUTO_INCREMENT,
    subject VARCHAR(10) NOT NULL,
    qualification  VARCHAR(20) NOT NULL,
    effective_date  VARCHAR(10) NOT NULL,
    problem VARCHAR(2048) NOT NULL,
    example2 CLOB(10000),
    answerExplain CLOB(15000),
    answer_num VARCHAR(2),

    PRIMARY KEY (id)
);

