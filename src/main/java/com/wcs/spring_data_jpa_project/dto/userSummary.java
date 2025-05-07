package com.wcs.spring_data_jpa_project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class userSummary {
    private String userName;
    private String email;
}
