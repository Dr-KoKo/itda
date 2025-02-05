CREATE TABLE mail_event
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    receiver   VARCHAR(255)                        NOT NULL,
    subject    VARCHAR(255)                        NOT NULL,
    template   ENUM('EMAIL_VERIFICATION') NOT NULL,
    arguments  TEXT                                NOT NULL,
    status     ENUM('REQUESTED', 'SENDING', 'SUCCESS', 'FAILED') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NULL
);
