CREATE TABLE hello (
    author text,
    zip text,
    city text,
    element text,
	title text,
    date text
);
INSERT INTO hello VALUES ('Jenkins T1', '35000', 'Rennes', 'smile', '', '16/01/2020');
INSERT INTO hello VALUES ('Jenkins T2', '75000', 'Paris', 'smile', '', '16/01/2020');
INSERT INTO hello VALUES ('Jenkins T3', '56100', 'Lorient', 'smile', '', '16/01/2020');
INSERT INTO hello VALUES ('Jenkins T4', '35000', 'Rennes', 'smile', '', '16/01/2020');
INSERT INTO hello VALUES ('Jenkins T5', '35000', 'Rennes', 'noExistElement', '', '16/01/2020');

CREATE TABLE Loginlogout (
    login text,
    password text
);
INSERT INTO Loginlogout VALUES ('user', 'user');
INSERT INTO Loginlogout VALUES ('user', 'user');
INSERT INTO Loginlogout VALUES ('user', 'user');
INSERT INTO Loginlogout VALUES ('user', 'user');
