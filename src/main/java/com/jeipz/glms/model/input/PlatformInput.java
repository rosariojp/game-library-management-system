package com.jeipz.glms.model.input;

import jakarta.validation.constraints.NotBlank;

public record PlatformInput(@NotBlank String name, String description) {
}
