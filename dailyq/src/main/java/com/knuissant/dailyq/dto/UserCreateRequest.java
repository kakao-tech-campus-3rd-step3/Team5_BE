package com.knuissant.dailyq.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(

        @NotBlank(message = "이메일은 필수 입력 항목입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,

        @NotBlank(message = "이름은 필수 입력 항목입니다.")
        @Size(max = 100, message = "이름은 100자를 초과할 수 없습니다.")
        String name
) {

}
