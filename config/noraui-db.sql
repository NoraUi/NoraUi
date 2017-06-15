CREATE TABLE hello (
    author text,
    zip text,
    city text,
    element text,
	date text,
	title text
);
INSERT INTO hello VALUES ('Jenkins T1', '35000', 'Rennes', 'smile', '16/01/2020', '');
INSERT INTO hello VALUES ('Jenkins T2', '75000', 'Paris', 'smile', '', '');
INSERT INTO hello VALUES ('Jenkins T3', '56100', 'Lorient', 'smile', '', '');
INSERT INTO hello VALUES ('Jenkins T4', '35000', 'Rennes', 'smile', '', '');
INSERT INTO hello VALUES ('Jenkins T5', '35000', 'Rennes', 'noExistElement', '', '');
INSERT INTO hello VALUES ('Jenkins T6', '35000', '', 'smile', '', '');
INSERT INTO hello VALUES ('Jenkins T7', '35000', '', '', '', '');
INSERT INTO hello VALUES ('Jenkins T8', '', 'Rennes', '', 'smile', '');

CREATE TABLE Loginlogout (
    login text,
    password text
);
INSERT INTO Loginlogout VALUES ('user', 'user');
INSERT INTO Loginlogout VALUES ('user', 'user');
INSERT INTO Loginlogout VALUES ('user', 'user');
INSERT INTO Loginlogout VALUES ('user', 'user');
