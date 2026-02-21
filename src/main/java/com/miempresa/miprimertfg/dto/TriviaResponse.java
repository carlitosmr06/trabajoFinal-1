package com.miempresa.miprimertfg.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class TriviaResponse {
    @JsonProperty("response_code")
    private int responseCode;

    @JsonProperty("results")
    private List<TriviaResult> results;
}
