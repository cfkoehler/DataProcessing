package com.DpServer.input;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "uuid",
        "feedingDate",
        "objectURL",
        "objectBucket",
        "originalFilename"
})
@Generated("jsonschema2pojo")
public class SqsMessage implements Serializable
{
    @JsonProperty("uuid")
    private String uuid;
    @JsonProperty("feedingDate")
    private Long feedingDate;
    @JsonProperty("objectURL")
    private String objectURL;
    @JsonProperty("objectBucket")
    private String objectBucket;
    @JsonProperty("originalFilename")
    private String originalFilename;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
    private final static long serialVersionUID = -2612033473699888864L;

    /**
     * No args constructor for use in serialization
     *
     */
    public SqsMessage() {
    }

    /**
     *
     * @param objectURL
     * @param uuid
     * @param feedingDate
     */
    public SqsMessage(String uuid, Long feedingDate, String objectURL, String objectBucket, String originalFilename) {
        super();
        this.uuid = uuid;
        this.feedingDate = feedingDate;
        this.objectURL = objectURL;
        this.objectBucket = objectBucket;
        this.originalFilename = originalFilename;
    }

    @JsonProperty("uuid")
    public String getUuid() {
        return uuid;
    }

    @JsonProperty("uuid")
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @JsonProperty("feedingDate")
    public Long getFeedingDate() {
        return feedingDate;
    }

    @JsonProperty("feedingDate")
    public void setFeedingDate(Long feedingDate) {
        this.feedingDate = feedingDate;
    }

    @JsonProperty("objectURL")
    public String getObjectURL() {
        return objectURL;
    }

    @JsonProperty("objectURL")
    public void setObjectURL(String objectURL) {
        this.objectURL = objectURL;
    }

    @JsonProperty("objectBucket")
    public String getObjectBucket() {
        return objectBucket;
    }

    @JsonProperty("objectBucket")
    public void setObjectBucket(String objectBucket) {
        this.objectBucket = objectBucket;
    }

    @JsonProperty("originalFilename")
    public String getOriginalFilename() {
        return originalFilename;
    }

    @JsonProperty("originalFilename")
    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}