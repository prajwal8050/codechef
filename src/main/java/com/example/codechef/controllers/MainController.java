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

    @Autowired private CourseRepository courseRepo;
    @Autowired private CourseModuleRepository courseModuleRepo;
    @Autowired private DiscussionRepository discussionRepo;
    @Autowired private DiscussionReplyRepository replyRepo;

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
    public String discuss(Model model) {
        model.addAttribute("topics", discussionRepo.findAllByOrderByCreatedAtDesc());
        return "discuss";
    }

    @GetMapping("/discuss/{id}")
    public String viewTopic(@PathVariable Long id, Model model) {
        Discussion topic = discussionRepo.findById(id).orElseThrow();
        model.addAttribute("topic", topic);
        model.addAttribute("replies", replyRepo.findByDiscussionIdOrderByCreatedAtAsc(id));
        return "topic-view";
    }

    @PostMapping("/discuss/save")
    public String saveTopic(@RequestParam String title, @RequestParam(required=false) String content, @RequestParam String tag) {
        Discussion d = new Discussion();
        d.setTitle(title);
        d.setContent(content != null ? content : "");
        d.setTag(tag);
        d.setAuthor("user_" + (int)(Math.random() * 1000));
        discussionRepo.save(d);
        return "redirect:/discuss";
    }

    @PostMapping("/discuss/reply/{id}")
    public String saveReply(@PathVariable Long id, @RequestParam String content) {
        Discussion topic = discussionRepo.findById(id).orElseThrow();
        DiscussionReply r = new DiscussionReply();
        r.setDiscussion(topic);
        r.setContent(content);
        r.setAuthor("user_" + (int)(Math.random() * 1000));
        replyRepo.save(r);
        
        topic.setRepliesCount(topic.getRepliesCount() + 1);
        discussionRepo.save(topic);
        return "redirect:/discuss/" + id;
    }

    @PostMapping("/discuss/edit/{id}")
    public String editTopic(@PathVariable Long id, @RequestParam String title, @RequestParam String tag) {
        Discussion d = discussionRepo.findById(id).orElseThrow();
        d.setTitle(title);
        d.setTag(tag);
        discussionRepo.save(d);
        return "redirect:/discuss";
    }

    @PostMapping("/discuss/delete/{id}")
    public String deleteTopic(@PathVariable Long id) {
        discussionRepo.deleteById(id);
        return "redirect:/discuss";
    }

    @GetMapping("/practice")
    public String practice(Model model) {
        model.addAttribute("problems", problemRepo.findAll());
        return "practice";
    }

    @GetMapping("/submissions")
    public String submissions(Model model) {
        model.addAttribute("submissions", submissionRepo.findAll());
        return "submissions";
    }

    @GetMapping("/dsa-learning-path")
    public String dsaPath() {
        return "dsa-learning-path";
    }

    @GetMapping("/competitive/1v1")
    public String battle1v1() {
        return "competitive/1v1";
    }

    @GetMapping("/competitive/speed")
    public String speedCoding() {
        return "competitive/speed";
    }

    @GetMapping("/competitive/reverse")
    public String reverseCoding() {
        return "competitive/reverse";
    }

    @GetMapping("/competitive/blind")
    public String blindCoding() {
        return "competitive/blind";
    }

    @GetMapping("/blog")
    public String blog() {
        return "blog";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/careers")
    public String careers() {
        return "careers";
    }

    @GetMapping("/privacy")
    public String privacy() {
        return "privacy";
    }

    @GetMapping("/terms")
    public String terms() {
        return "terms";
    }

    @GetMapping("/courses")
    public String courses(Model model) {
        model.addAttribute("courses", courseRepo.findAll());
        return "courses";
    }

    @GetMapping("/course/{id}")
    public String courseDetails(@PathVariable Long id, Model model) {
        Course course = courseRepo.findById(id).orElseThrow();
        model.addAttribute("course", course);
        return "course-details";
    }

    @GetMapping("/course-details")
    public String courseDetailsRedirect(Model model) {
        Course course = courseRepo.findAll().stream().findFirst().orElse(null);
        if (course != null) return "redirect:/course/" + course.getId();
        return "redirect:/courses";
    }

    @GetMapping("/module-view/{id}")
    public String moduleView(@PathVariable Long id, Model model) {
        CourseModule module = courseModuleRepo.findById(id).orElse(null);
        model.addAttribute("module", module);
        return "module-view";
    }

    @PostMapping("/module/complete/{id}")
    public String markComplete(@PathVariable Long id) {
        CourseModule module = courseModuleRepo.findById(id).orElse(null);
        if (module != null) {
            Course course = module.getCourse();
            int nextIndex = module.getOrderIndex() + 1;
            java.util.Optional<CourseModule> nextModule = course.getModules().stream()
                .filter(m -> m.getOrderIndex() == nextIndex)
                .findFirst();
            
            if (nextModule.isPresent()) {
                CourseModule next = nextModule.get();
                next.setLocked(false);
                courseModuleRepo.save(next);
            }
        }
        return "redirect:/course-details";
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
