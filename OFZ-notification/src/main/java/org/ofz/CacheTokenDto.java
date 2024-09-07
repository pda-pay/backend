package org.ofz;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CacheTokenDto {
    private String userLoginId;
    private String token;
}
