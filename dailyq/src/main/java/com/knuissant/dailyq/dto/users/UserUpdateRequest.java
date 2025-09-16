package com.knuissant.dailyq.dto.users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// 사용자 이름 수정 요청에 사용되는 DTO.
public record UserUpdateRequest(
        @NotBlank(message = "이름은 필수 입력 항목입니다.")
        @Size(max = 100, message = "이름은 100자를 초과할 수 없습니다.")
        String name
) {

}
