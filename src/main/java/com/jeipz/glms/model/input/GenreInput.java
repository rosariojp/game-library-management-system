package com.jeipz.glms.model.input;

import jakarta.validation.constraints.NotBlank;

public record GenreInput(@NotBlank String name) {
}
