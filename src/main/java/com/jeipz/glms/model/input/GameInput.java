package com.jeipz.glms.model.input;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record GameInput(@NotBlank String title,
                        String description,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate releaseDate,
                        @NotNull @DecimalMin(value = "0.00") BigDecimal price,
                        List<UUID> genreIds,
                        List<UUID> platformIds) {
}
