package com.aryan.omybott.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupReqDTO {
    private String name;
    private String email;
    private String password;
}
