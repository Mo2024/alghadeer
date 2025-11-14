package com.mohamed.backend.salah.attempt;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/salah/attempt")
@RequiredArgsConstructor
@Tag(name = "Salah Attempt Management", description = "Operations related to Salah test attempt entity for salah module")
public class AttemptController {
}
