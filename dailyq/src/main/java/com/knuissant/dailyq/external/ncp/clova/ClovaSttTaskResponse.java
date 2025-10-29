package com.knuissant.dailyq.external.ncp.clova;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClovaSttTaskResponse(
        String token
) {

}
