package com.kbytes.liveaddress.persistence.sqldb.models;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rate_limit", uniqueConstraints = @UniqueConstraint(columnNames = "client_id"))
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RateLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_id", nullable = false, unique = true)
    private String clientId;

    @Column(name = "request_count", nullable = false)
    private int requestCount;

    @Column(name = "max_requests_per_day", nullable = false)
    private long maxRequestsPerDay;

    @Column(name = "last_request_timestamp", nullable = false)
    private Timestamp lastRequestTimestamp;
}

