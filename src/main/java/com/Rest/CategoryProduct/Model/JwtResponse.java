package com.Rest.CategoryProduct.Model;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {

    private String jwtToken;
    private String username;
}
