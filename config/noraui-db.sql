CREATE TABLE hello (
    id int primary key not null,
    author text,
    zip text,
    city text,
    element text,
    element2 text,
    date text,
    title text
);
INSERT INTO hello VALUES (1, 'Jenkins T1', '35000', 'Rennes', 'smile', 'smile', '16/01/2050', '');
INSERT INTO hello VALUES (2, 'Jenkins T2', '75000', 'Paris', 'smile', 'smile', '', '');
INSERT INTO hello VALUES (3, 'Jenkins T3', '56100', 'Lorient', 'smile', 'smile', '', '');
INSERT INTO hello VALUES (4, 'Jenkins T4', '35000', 'Rennes', 'smile', 'smile', '', '');
INSERT INTO hello VALUES (5, 'Jenkins T5', '35000', 'Rennes', 'noExistElement', 'noExistElement', '', '');
INSERT INTO hello VALUES (6, 'Jenkins T6', '35000', '', 'smile', 'smile', '', '');
INSERT INTO hello VALUES (7, 'Jenkins T7', '35000', 'Rennes', '', '', '', '');
INSERT INTO hello VALUES (8, 'Jenkins T8', '', 'Rennes', 'smile', 'smile', '', '');

CREATE TABLE loginLogout (
    id int primary key not null,
    `user` text,
    `password` text,
    profile text
);
INSERT INTO loginLogout VALUES (1, 'sgrillon', '℗:qmTAYKS9UG87rNuUQ0Ao6Q==', 'admin');
INSERT INTO loginLogout VALUES (2, 'sgrillon2', '℗:qmTAYKS9UG87rNuUQ0Ao6Q==', 'referencer');

CREATE TABLE blog (
    id int primary key not null,
    blog text,
    title text,
    text text,
    author text,
    note text
);

INSERT INTO blog VALUES (1, 'Blog 1', 'Article 1', 'text 1', 'Peter', '7');
INSERT INTO blog VALUES (2, 'Blog 1', 'Article 2', 'text 2', 'Peter', '10');
INSERT INTO blog VALUES (3, 'Blog 2', 'Article 3', 'text 3', 'Peter', '8');
INSERT INTO blog VALUES (4, '', '', '', '', '');
INSERT INTO blog VALUES (5, 'Blog 2', 'Article 4', 'text 4', 'anonymous', '2');
INSERT INTO blog VALUES (6, 'Blog 1', 'Article 5', 'text 5', 'Peter', '9');

CREATE TABLE sampleRESTAPI (
    id int primary key not null,
    location text,
    language text,
    data text
);
INSERT INTO sampleRESTAPI VALUES (1, 'rennes', 'java', '');
INSERT INTO sampleRESTAPI VALUES (2, 'rennes', 'javascript', '');
