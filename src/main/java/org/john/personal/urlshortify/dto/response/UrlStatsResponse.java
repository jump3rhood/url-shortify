package org.john.personal.urlshortify.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UrlStatsResponse {
    private String longUrl;
    private String shortUrl;
    private int clickCount;
}
