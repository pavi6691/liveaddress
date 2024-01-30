CREATE TABLE IF NOT EXISTS rate_limit (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id VARCHAR(255) NOT NULL UNIQUE,
    request_count INT NOT NULL,
    max_requests_per_day BIGINT NOT NULL,
    last_request_timestamp TIMESTAMP NOT NULL
);