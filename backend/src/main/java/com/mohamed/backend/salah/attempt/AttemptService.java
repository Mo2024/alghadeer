package com.mohamed.backend.salah.attempt;

import com.mohamed.backend.salah.attempt.questions.StudentSalahQuestionRepository;
import com.mohamed.backend.utils.methods.Logger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AttemptService {

    private final StudentAttemptRepository studentSalahAttemptRepository;
    private final StudentSalahQuestionRepository studentSalahQuestionRepository;
    private final Logger logger;



}
