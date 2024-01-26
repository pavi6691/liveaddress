package com.kbytes.liveaddress.persistence.postgresql.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@Entity(name = "live_addresses")
@Table(name = "#{@indexName}")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PGLiveAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "outcode")
    private String outcode;

    @Column(name = "postcode", nullable = false)
    private String postcode;

    @Column(name = "fulladdress")
    private String fullAddress;

    @Column(name = "line1")
    private String line1;

    @Column(name = "line2")
    private String line2;

    @Column(name = "category")
    private String category;

    @Column(name = "iconpath")
    private String iconPath;

    @Column(name = "latitude",precision = 10, scale = 6)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 6)
    private BigDecimal longitude;

    @Column(name = "code")
    private String code;

    @Column(name = "iata")
    private String iata;

    @Column(name = "icao")
    private String icao;

    @Column(name = "created", nullable = false)
    private Timestamp created;

    @Column(name = "post_code_id")
    private Integer postCodeId;
}
