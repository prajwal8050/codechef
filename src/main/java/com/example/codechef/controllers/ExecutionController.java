package com.example.codechef.controllers;

import com.example.codechef.models.*;
import com.example.codechef.repositories.*;
import com.example.codechef.services.CodeExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/execute")
public class ExecutionController {

    @Autowired private ProblemRepository problemRepo;
    @Autowired private CodeExecutionService codeExecutionService;

    @PostMapping("/run")
    public Map<String, Object> runCode(@RequestBody Map<String, String> payload) {
        String code = payload.get("code");
        String language = payload.get("language");
        Long problemId = Long.valueOf(payload.get("problemId"));

        try {
            Problem p = problemRepo.findById(problemId).orElseThrow();
            Testcase tc = p.getTestcases().stream().findFirst().orElse(null);
            String input = tc != null ? tc.getInput() : "";
            String expected = tc != null ? tc.getExpectedOutput() : "";

            String output = codeExecutionService.execute(code, language, input);
            String status = output.trim().equals(expected.trim()) ? "ACCEPTED" : "WRONG_ANSWER";

            return Map.of("status", status, "output", output);
        } catch (Exception e) {
            return Map.of("status", "ERROR", "output", e.getMessage());
        }
    }

    @PostMapping("/submit")
    public Map<String, Object> submitCode(@RequestBody Map<String, String> payload) {
        // Simple for demo: same as run but marks as "SUBMITTED" logic could go here
        return runCode(payload);
    }
}
