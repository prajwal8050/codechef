package com.example.codechef.controllers;

import com.example.codechef.models.*;
import com.example.codechef.repositories.*;
import com.example.codechef.services.CodeExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
public class MainController {

    @Autowired private ProblemRepository problemRepo;
    @Autowired private ContestRepository contestRepo;
    @Autowired private SubmissionRepository submissionRepo;
    @Autowired private CodeExecutionService codeExecutionService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("contests", contestRepo.findAll());
        model.addAttribute("problems", problemRepo.findAll());
        return "index";
    }

    @GetMapping("/ide")
    public String ide() {
        return "ide";
    }

    @GetMapping("/compete")
    public String compete(Model model) {
        model.addAttribute("contests", contestRepo.findAll());
        return "compete";
    }

    @GetMapping("/discuss")
    public String discuss() {
        return "discuss";
    }

    @GetMapping("/courses")
    public String courses() {
        return "courses";
    }

    @GetMapping("/course-details")
    public String courseDetails() {
        return "course-details";
    }

    @GetMapping("/problem/{id}")
    public String viewProblem(@PathVariable Long id, Model model) {
        Problem problem = problemRepo.findById(id).orElseThrow();
        model.addAttribute("problem", problem);
        return "problem";
    }

    @PostMapping("/submit/{id}")
    @ResponseBody
    public String submitCode(@PathVariable Long id, @RequestBody SubmissionRequest req) {
        Problem problem = problemRepo.findById(id).orElseThrow();
        String result = "WRONG_ANSWER";
        
        try {
            // Simple check against first testcase for now
            Testcase tc = problem.getTestcases().get(0);
            String output = codeExecutionService.execute(req.code, req.language, tc.getInput());
            
            if (output.trim().equals(tc.getExpectedOutput().trim())) {
                result = "ACCEPTED";
            } else if (output.equals("TIME_LIMIT_EXCEEDED")) {
                result = "TIME_LIMIT_EXCEEDED";
            }
        } catch (Exception e) {
            result = "RUNTIME_ERROR";
        }

        Submission submission = new Submission();
        submission.setProblem(problem);
        submission.setCode(req.code);
        submission.setLanguage(req.language);
        submission.setResult(Submission.Result.valueOf(result));
        submissionRepo.save(submission);

        return result;
    }

    public static class SubmissionRequest {
        public String code;
        public String language;
    }

    @PostMapping("/run")
    @ResponseBody
    public String runCode(@RequestBody RunRequest req) {
        try {
            return codeExecutionService.execute(req.code, req.language, req.input);
        } catch (Exception e) {
            return "Execution Error: " + e.getMessage();
        }
    }

    public static class RunRequest {
        public String code;
        public String language;
        public String input;
    }
}
