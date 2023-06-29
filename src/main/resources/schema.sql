DROP TABLE IF EXISTS USERS CASCADE;
DROP TABLE IF EXISTS ITEM_REQUESTS CASCADE;
DROP TABLE IF EXISTS ITEMS CASCADE;
DROP TABLE IF EXISTS BOOKINGS CASCADE;
DROP TABLE IF EXISTS COMMENTS CASCADE;

CREATE TABLE IF NOT EXISTS USERS (
    ID BIGINT generated BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    NAME VARCHAR(255) NOT NULL,
    EMAIL VARCHAR(512) NOT NULL,
    CONSTRAINT UNIQ_EMAIL UNIQUE (EMAIL)
);


CREATE TABLE IF NOT EXISTS ITEM_REQUESTS (
   ID BIGINT generated BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
   DESCRIPTION VARCHAR(1000) NOT NULL,
   REQUESTER_ID BIGINT REFERENCES USERS (ID) ON DELETE CASCADE NOT NULL,
   CREATED TIMESTAMP WITHOUT TIME ZONE
);


CREATE TABLE IF NOT EXISTS ITEMS (
    ID BIGINT generated BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    NAME VARCHAR(255) NOT NULL,
    DESCRIPTION VARCHAR(512) NOT NULL,
    IS_AVAILABLE BOOLEAN,
    OWNER_ID BIGINT REFERENCES USERS (ID) ON DELETE CASCADE NOT NULL,
    request_id BIGINT REFERENCES ITEM_REQUESTS (ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS BOOKINGS (
    ID BIGINT generated BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    START_DATE TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    END_DATE TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    ITEM_ID BIGINT REFERENCES ITEMS (ID) ON DELETE CASCADE NOT NULL,
    BOOKER_ID BIGINT REFERENCES USERS (ID) ON DELETE CASCADE NOT NULL,
    STATUS VARCHAR(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS COMMENTS (
    ID BIGINT generated BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    TEXT VARCHAR(1000) NOT NULL,
    ITEM_ID BIGINT REFERENCES ITEMS (ID) ON DELETE CASCADE NOT NULL,
    AUTHOR_ID BIGINT REFERENCES USERS (ID) ON DELETE CASCADE NOT NULL,
    CREATED TIMESTAMP WITHOUT TIME ZONE
);



