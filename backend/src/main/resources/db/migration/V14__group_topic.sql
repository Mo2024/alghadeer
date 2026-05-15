CREATE SEQUENCE topics_groups_sequence
START WITH 1
INCREMENT BY 1;

CREATE TABLE topics_groups (
    id INTEGER NOT NULL DEFAULT nextval('topics_groups_sequence'),
    name VARCHAR(255),
    archived BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
);

ALTER TABLE main_topics
ADD COLUMN topics_groups_id INTEGER;

ALTER TABLE main_topics
ADD CONSTRAINT fk_main_topics_topics_groups
FOREIGN KEY (topics_groups_id)
REFERENCES topics_groups(id);

ALTER TABLE main_topics
ADD COLUMN archived BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE sub_topics
ADD COLUMN archived BOOLEAN NOT NULL DEFAULT FALSE;