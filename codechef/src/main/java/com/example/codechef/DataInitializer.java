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

    public DataInitializer(ProblemRepository problemRepo, UserRepository userRepo, ContestRepository contestRepo) {
        this.problemRepo = problemRepo;
        this.userRepo = userRepo;
        this.contestRepo = contestRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        if (problemRepo.count() == 0) {
            Problem p1 = new Problem();
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

            Problem p2 = new Problem();
            p2.setTitle("Fibonacci Series");
            p2.setDifficulty(Problem.Difficulty.MEDIUM);
            p2.setDescription("Print the Nth Fibonacci number.");
            p1.setInputFormat("A single integer N.");
            p1.setOutputFormat("A single integer denoting the Nth Fibonacci number.");

            Testcase t2 = new Testcase();
            t2.setInput("5");
            t2.setExpectedOutput("5");
            t2.setProblem(p2);
            p2.setTestcases(Collections.singletonList(t2));
            problemRepo.save(p2);

            Contest c = new Contest();
            c.setName("Monthly Mega Contest");
            c.setStartTime(LocalDateTime.now().plusHours(12));
            c.setDurationMs(3 * 3600 * 1000);
            c.setProblems(Collections.singletonList(p1));
            contestRepo.save(c);

            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("admin123");
            admin.setEmail("admin@codechef.com");
            admin.setRole(User.Role.ADMIN);
            userRepo.save(admin);
        }
    }
}
