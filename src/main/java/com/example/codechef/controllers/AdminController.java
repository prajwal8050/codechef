package com.example.codechef.controllers;

import com.example.codechef.models.*;
import com.example.codechef.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private ProblemRepository problemRepo;
    @Autowired private ContestRepository contestRepo;
    @Autowired private CourseRepository courseRepo;
    @Autowired private CourseModuleRepository courseModuleRepo;

    @GetMapping
    public String dashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/problems")
    public String listProblems(Model model) {
        model.addAttribute("problems", problemRepo.findAll());
        return "admin/problems";
    }

    @GetMapping("/problems/add")
    public String addProblemForm() {
        return "admin/add-problem";
    }

    @PostMapping("/problems/save")
    public String saveProblem(@ModelAttribute Problem problem) {
        problemRepo.save(problem);
        return "redirect:/admin/problems";
    }

    @GetMapping("/contests")
    public String listContests(Model model) {
        model.addAttribute("contests", contestRepo.findAll());
        return "admin/contests";
    }

    @GetMapping("/contests/add")
    public String addContestForm() {
        return "admin/add-contest";
    }

    @PostMapping("/contests/save")
    public String saveContest(@ModelAttribute Contest contest) {
        contestRepo.save(contest);
        return "redirect:/admin/contests";
    }

    @GetMapping("/courses")
    public String listCourses(Model model) {
        model.addAttribute("courses", courseRepo.findAll());
        return "admin/courses";
    }

    @GetMapping("/courses/add")
    public String addCourseForm() {
        return "admin/add-course";
    }

    @PostMapping("/courses/save")
    public String saveCourse(@ModelAttribute Course course) {
        courseRepo.save(course);
        return "redirect:/admin/courses";
    }

    @GetMapping("/modules/add/{courseId}")
    public String addModuleForm(@PathVariable Long courseId, Model model) {
        model.addAttribute("courseId", courseId);
        return "admin/add-module";
    }

    @PostMapping("/modules/save")
    public String saveModule(@RequestParam Long courseId, @ModelAttribute CourseModule module) {
        Course course = courseRepo.findById(courseId).orElseThrow();
        module.setCourse(course);
        courseModuleRepo.save(module);
        return "redirect:/admin/courses";
    }
}
