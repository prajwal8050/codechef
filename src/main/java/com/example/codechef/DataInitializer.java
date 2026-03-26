package com.example.codechef;

import com.example.codechef.models.*;
import com.example.codechef.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.Collections;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProblemRepository problemRepo;
    private final UserRepository userRepo;
    private final ContestRepository contestRepo;
    private final CourseRepository courseRepo;
    private final CourseModuleRepository courseModuleRepo;
    private final DiscussionRepository discussionRepo;
    private final SubmissionRepository submissionRepo;

    public DataInitializer(ProblemRepository problemRepo, UserRepository userRepo, 
                           ContestRepository contestRepo, CourseRepository courseRepo, 
                           CourseModuleRepository courseModuleRepo, DiscussionRepository discussionRepo,
                           SubmissionRepository submissionRepo) {
        this.problemRepo = problemRepo;
        this.userRepo = userRepo;
        this.contestRepo = contestRepo;
        this.courseRepo = courseRepo;
        this.courseModuleRepo = courseModuleRepo;
        this.discussionRepo = discussionRepo;
        this.submissionRepo = submissionRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        User admin = null;
        User normalUser = null;
        
        if (userRepo.count() == 0) {
            admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@codechef.com");
            admin.setPassword("admin123");
            admin.setRole(User.Role.ADMIN);
            userRepo.save(admin);

            normalUser = new User();
            normalUser.setUsername("user");
            normalUser.setEmail("user@codechef.com");
            normalUser.setPassword("user123");
            normalUser.setRole(User.Role.USER);
            userRepo.save(normalUser);
        } else {
            admin = userRepo.findAll().get(0);
            normalUser = userRepo.findAll().get(1);
        }

        Problem p1 = null;
        Problem p2 = null;

        if (problemRepo.count() == 0) {
            p1 = new Problem();
            p1.setTitle("Sum of Two Numbers");
            p1.setDifficulty(Problem.Difficulty.EASY);
            p1.setDescription("Given two integers A and B, find their sum.");
            p1.setInputFormat("Two space-separated integers A and B.");
            p1.setOutputFormat("A single integer denoting the sum.");
            p1.setConstraints("1 <= A, B <= 10^9");

            Testcase t1 = new Testcase();
            t1.setInput("5 10");
            t1.setExpectedOutput("15");
            t1.setProblem(p1);
            p1.setTestcases(Collections.singletonList(t1));
            problemRepo.save(p1);

            p2 = new Problem();
            p2.setTitle("Fibonacci Series");
            p2.setDifficulty(Problem.Difficulty.MEDIUM);
            p2.setDescription("Print the Nth Fibonacci number.");
            p2.setInputFormat("A single integer N.");
            p2.setOutputFormat("A single integer denoting the Nth Fibonacci number.");
            problemRepo.save(p2);
        } else {
            p1 = problemRepo.findAll().get(0);
            p2 = problemRepo.findAll().get(1);
        }

        if (contestRepo.count() == 0) {
            Contest c1 = new Contest();
            c1.setName("Monthly Mega Contest");
            c1.setStartTime(LocalDateTime.now().plusDays(1));
            c1.setStatus(Contest.Status.UPCOMING);
            c1.setDurationMs(180L * 60 * 1000); 
            contestRepo.save(c1);
        }

        if (courseRepo.count() == 0) {
            Course dsaCourse = new Course();
            dsaCourse.setTitle("Data Structures & Algorithms");
            dsaCourse.setDescription("A comprehensive path covering everything from Arrays and Linked Lists to Dynamic Programming and Graphs.");
            dsaCourse.setImagePath("/images/dsa_course.png");
            courseRepo.save(dsaCourse);

            for(int i=1; i<=15; i++) {
                CourseModule dm = new CourseModule();
                dm.setTitle("Topic " + i);
                dm.setDescription("Description for Topic " + i);
                dm.setOrderIndex(i);
                dm.setLocked(i > 1);
                dm.setCourse(dsaCourse);
                courseModuleRepo.save(dm);
            }

            Course pythonCourse = new Course();
            pythonCourse.setTitle("Python for CP");
            pythonCourse.setDescription("Master Python specifically tailored for competitive programming. Learn fast I/O, built-in collections, and tricks.");
            pythonCourse.setImagePath("/images/python_course.png");
            courseRepo.save(pythonCourse);

            for(int i=1; i<=8; i++) {
                CourseModule pm = new CourseModule();
                pm.setTitle("Python Unit " + i);
                pm.setOrderIndex(i);
                pm.setCourse(pythonCourse);
                courseModuleRepo.save(pm);
            }

            Course javaCourse = new Course();
            javaCourse.setTitle("Java Masterclass");
            javaCourse.setDescription("Learn Core Java, OOP concepts, and how to effectively leverage Java Collections Framework for complex problems.");
            javaCourse.setImagePath("/images/java_course.png");
            courseRepo.save(javaCourse);
            
            for(int i=1; i<=10; i++) {
                CourseModule jm = new CourseModule();
                jm.setTitle("Java Unit " + i);
                jm.setOrderIndex(i);
                jm.setCourse(javaCourse);
                courseModuleRepo.save(jm);
            }
        }

        if (discussionRepo.count() == 0) {
            Discussion d1 = new Discussion();
            d1.setTitle("Editorial for Starters 125 (Division 2) is out now!");
            d1.setAuthor("admin");
            d1.setTag("editorial");
            d1.setUpvotes(42);
            d1.setRepliesCount(15);
            discussionRepo.save(d1);

            Discussion d2 = new Discussion();
            d2.setTitle("How to get better at dynamic programming?");
            d2.setAuthor("code_ninja");
            d2.setTag("help");
            d2.setUpvotes(28);
            d2.setRepliesCount(34);
            discussionRepo.save(d2);
        }

        if (submissionRepo.count() == 0 && p1 != null && normalUser != null) {
            Submission s1 = new Submission();
            s1.setUser(normalUser);
            s1.setProblem(p1);
            s1.setResult(Submission.Result.ACCEPTED);
            s1.setLanguage("C++");
            s1.setSubmissionTime(LocalDateTime.now().minusMinutes(5));
            submissionRepo.save(s1);

            Submission s2 = new Submission();
            s2.setUser(normalUser);
            s2.setProblem(p2 != null ? p2 : p1);
            s2.setResult(Submission.Result.WRONG_ANSWER);
            s2.setLanguage("Java");
            s2.setSubmissionTime(LocalDateTime.now().minusHours(2));
            submissionRepo.save(s2);
        }
    }
}
