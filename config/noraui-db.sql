CREATE TABLE hello (
    author text,
    zip text,
    city text,
    element text,
    element2 text,
    date text,
    title text
);
INSERT INTO hello VALUES ('Jenkins T1', '35000', 'Rennes', 'smile', 'smile', '16/01/2020', '');
INSERT INTO hello VALUES ('Jenkins T2', '75000', 'Paris', 'smile', 'smile', '', '');
INSERT INTO hello VALUES ('Jenkins T3', '56100', 'Lorient', 'smile', 'smile', '', '');
INSERT INTO hello VALUES ('Jenkins T4', '35000', 'Rennes', 'smile', 'smile', '', '');
INSERT INTO hello VALUES ('Jenkins T5', '35000', 'Rennes', 'noExistElement', 'noExistElement', '', '');
INSERT INTO hello VALUES ('Jenkins T6', '35000', '', 'smile', 'smile', '', '');
INSERT INTO hello VALUES ('Jenkins T7', '35000', '', '', '', '', '');
INSERT INTO hello VALUES ('Jenkins T8', '', 'Rennes', 'smile', 'smile', '', '');

CREATE TABLE Loginlogout (
    login text,
    password text
);
INSERT INTO Loginlogout VALUES ('user', 'user');
INSERT INTO Loginlogout VALUES ('user', 'user');
INSERT INTO Loginlogout VALUES ('user', 'user');
INSERT INTO Loginlogout VALUES ('user', 'user');
