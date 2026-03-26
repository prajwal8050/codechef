package com.example.codechef.services;

import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;

@Service
public class CodeExecutionService {

    public String execute(String code, String language, String input) throws Exception {
        Path tempDir = Files.createTempDirectory("codechef_exec_");
        String filename = language.equalsIgnoreCase("java") ? "Main" : "Solution";
        String ext = getExtension(language);
        Path sourceFile = tempDir.resolve(filename + ext);
        Files.writeString(sourceFile, code);

        ProcessBuilder pb;
        if (language.equalsIgnoreCase("python")) {
            pb = new ProcessBuilder("python3", sourceFile.toString());
        } else if (language.equalsIgnoreCase("java")) {
            // Compile Java
            Process compile = new ProcessBuilder("javac", sourceFile.toString())
                                .redirectErrorStream(true)
                                .start();
            if (!compile.waitFor(5, TimeUnit.SECONDS) || compile.exitValue() != 0) {
                StringBuilder err = new StringBuilder("COMPILATION_ERROR:\n");
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(compile.getInputStream()))) {
                    String line; while ((line = reader.readLine()) != null) err.append(line).append("\n");
                }
                return err.toString();
            }
            pb = new ProcessBuilder("java", "-cp", tempDir.toString(), filename);
        } else if (language.equalsIgnoreCase("cpp")) {
            Path exe = tempDir.resolve("solution.out");
            Process compile = new ProcessBuilder("g++", sourceFile.toString(), "-o", exe.toString()).start();
            if (!compile.waitFor(5, TimeUnit.SECONDS) || compile.exitValue() != 0) {
                return "COMPILATION_ERROR";
            }
            pb = new ProcessBuilder(exe.toString());
        } else if (language.equalsIgnoreCase("c")) {
            Path exe = tempDir.resolve("solution.out");
            Process compile = new ProcessBuilder("gcc", sourceFile.toString(), "-o", exe.toString()).start();
            if (!compile.waitFor(5, TimeUnit.SECONDS) || compile.exitValue() != 0) {
                return "COMPILATION_ERROR";
            }
            pb = new ProcessBuilder(exe.toString());
        } else {
            return "UNSUPPORTED_LANGUAGE";
        }

        pb.redirectErrorStream(true); // Merge stderr into stdout to capture errors
        Process process = pb.start();
        
        // Write input safely
        if (input != null && !input.isEmpty()) {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            writer.write(input);
            writer.flush();
            writer.close();
        } else {
            process.getOutputStream().close();
        }

        // Read output
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        if (!process.waitFor(2, TimeUnit.SECONDS)) {
            process.destroy();
            return "TIME_LIMIT_EXCEEDED";
        }

        return output.toString().trim();
    }

    private String getExtension(String language) {
        return switch (language.toLowerCase()) {
            case "python" -> ".py";
            case "java" -> ".java";
            case "cpp" -> ".cpp";
            case "c" -> ".c";
            default -> ".txt";
        };
    }
}
