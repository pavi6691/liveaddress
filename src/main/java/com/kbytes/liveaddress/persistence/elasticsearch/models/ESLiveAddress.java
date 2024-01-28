package com.kbytes.liveaddress.persistence.elasticsearch.models;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.math.BigDecimal;
import java.util.Date;


@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "#{@indexName}", createIndex = false, writeTypeHint = WriteTypeHint.FALSE)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setting(settingPath = "/settings/settings.json")
public class ESLiveAddress {
    
    @Id
    @Field(type = FieldType.Long, name = "id")
    @JsonIgnore
    private Long id;
    @Field(type = FieldType.Text)
    @JsonIgnore
    private String outcode;
    @Field(type = FieldType.Search_As_You_Type)
    @JsonInclude
    private String postcode;
    @JsonInclude
    @Field(type = FieldType.Search_As_You_Type, analyzer = "edge_ngram_analyzer")
    private String fulladdress;
    @Field(type = FieldType.Text)
    @JsonIgnore
    private String line1;
    @Field(type = FieldType.Text)
    @JsonIgnore
    private String line2;
    @Field(type = FieldType.Text)
    @JsonInclude
    private String category;
    @Field(type = FieldType.Text)
    @JsonIgnore
    private String iconpath;
    @GeoPointField
    @JsonInclude
    private BigDecimal latitude;
    @GeoPointField
    @JsonInclude
    private BigDecimal longitude;
    @Field(type = FieldType.Text)
    @JsonIgnore
    private String code;
    @Field(type = FieldType.Text)
    @JsonIgnore
    private String iata;
    @Field(type = FieldType.Text)
    @JsonIgnore
    private String icao;
    @Field(type = FieldType.Date)
    @JsonIgnore
    private Date created;
    @Field(type = FieldType.Integer)
    @JsonIgnore
    private int post_code_id;
 
}
