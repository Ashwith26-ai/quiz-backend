package com.onlinequiz.config;

import com.onlinequiz.entity.*;
import com.onlinequiz.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           QuizRepository quizRepository,
                           QuestionRepository questionRepository,
                           OptionRepository optionRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedUsers();
        seedQuizzes();
    }

    private void seedUsers() {
        if (!userRepository.existsByEmail("admin@quiz.com")) {
            User admin = new User(
                    "System Administrator",
                    "admin@quiz.com",
                    passwordEncoder.encode("admin123"),
                    Role.ROLE_ADMIN
            );
            userRepository.save(admin);
        }

        if (!userRepository.existsByEmail("student@quiz.com")) {
            User student = new User(
                    "Rahul Sharma",
                    "student@quiz.com",
                    passwordEncoder.encode("student123"),
                    Role.ROLE_STUDENT
            );
            userRepository.save(student);
        }

        if (!userRepository.existsByEmail("priya@quiz.com")) {
            User priya = new User(
                    "Priya Patel",
                    "priya@quiz.com",
                    passwordEncoder.encode("student123"),
                    Role.ROLE_STUDENT
            );
            userRepository.save(priya);
        }

        if (!userRepository.existsByEmail("aashwith46@gmail.com")) {
            User aashwith = new User(
                    "Ashwith Reddy",
                    "aashwith46@gmail.com",
                    passwordEncoder.encode("student123"),
                    Role.ROLE_STUDENT
            );
            userRepository.save(aashwith);
        }
    }

    private void seedQuizzes() {
        if (quizRepository.count() > 0) {
            return;
        }

        // Quiz 1: Java Programming Fundamentals
        Quiz javaQuiz = new Quiz(
                "Java Programming Fundamentals",
                "Test your knowledge on Core Java concepts, Object-Oriented Programming, and JVM architecture.",
                10, 5, 3
        );
        javaQuiz = quizRepository.save(javaQuiz);

        addQuestion(javaQuiz, "Which of the following is NOT a fundamental feature of Object-Oriented Programming in Java?", 1,
                new String[]{"Encapsulation", "Inheritance", "Pointers Manipulation", "Polymorphism"}, 3);

        addQuestion(javaQuiz, "What is the default value of an uninitialized local object reference variable in Java?", 1,
                new String[]{"null", "0", "undefined", "Local variables do not have default values and must be initialized before use"}, 4);

        addQuestion(javaQuiz, "Which memory area in the JVM is responsible for storing class structures, method data, and constant pools?", 1,
                new String[]{"Heap Memory", "Metaspace (Method Area)", "JVM Stack", "PC Register"}, 2);

        addQuestion(javaQuiz, "Which Java keyword is used to explicitly prevent a class from being inherited or subclassed?", 1,
                new String[]{"static", "abstract", "final", "sealed"}, 3);

        addQuestion(javaQuiz, "What is the return type of the hashCode() method defined in java.lang.Object?", 1,
                new String[]{"long", "int", "String", "void"}, 2);

        // Quiz 2: Web Development & Database Basics
        Quiz webQuiz = new Quiz(
                "Web Development & Database Basics",
                "Evaluate your skills in HTML5, CSS3, JavaScript, REST APIs, and SQL fundamentals.",
                10, 5, 3
        );
        webQuiz = quizRepository.save(webQuiz);

        addQuestion(webQuiz, "Which HTTP status code signifies that the requested resource was not found on the server?", 1,
                new String[]{"200 OK", "403 Forbidden", "404 Not Found", "500 Internal Server Error"}, 3);

        addQuestion(webQuiz, "In CSS Flexbox, which property is used to align items along the main axis?", 1,
                new String[]{"justify-content", "align-items", "flex-direction", "align-content"}, 1);

        addQuestion(webQuiz, "Which SQL clause is used to filter records resulting from a GROUP BY aggregation?", 1,
                new String[]{"WHERE", "HAVING", "ORDER BY", "FILTER"}, 2);

        addQuestion(webQuiz, "In JavaScript, which keyword declares a block-scoped variable that cannot be reassigned?", 1,
                new String[]{"var", "let", "const", "static"}, 3);

        addQuestion(webQuiz, "Which protocol is commonly used to encrypt web traffic between client and server?", 1,
                new String[]{"FTP", "HTTPS (SSL/TLS)", "SMTP", "Telnet"}, 2);

        // Quiz 3: Data Structures & Algorithms
        Quiz dsaQuiz = new Quiz(
                "Data Structures & Algorithms",
                "Assess your understanding of basic data structures including Arrays, Linked Lists, Stacks, Queues, and Trees.",
                15, 5, 3
        );
        dsaQuiz = quizRepository.save(dsaQuiz);

        addQuestion(dsaQuiz, "What is the average time complexity of searching an element in a balanced Binary Search Tree (BST)?", 1,
                new String[]{"O(1)", "O(n)", "O(log n)", "O(n log n)"}, 3);

        addQuestion(dsaQuiz, "Which data structure operates strictly on a Last-In, First-Out (LIFO) order?", 1,
                new String[]{"Queue", "Stack", "Array", "Priority Queue"}, 2);

        addQuestion(dsaQuiz, "Which of the following sorting algorithms has the best worst-case time complexity?", 1,
                new String[]{"Quick Sort (O(n^2))", "Bubble Sort (O(n^2))", "Merge Sort (O(n log n))", "Selection Sort (O(n^2))"}, 3);

        addQuestion(dsaQuiz, "What is the space complexity of an adjacency matrix representation of a graph with V vertices?", 1,
                new String[]{"O(V)", "O(V^2)", "O(V + E)", "O(E^2)"}, 2);

        addQuestion(dsaQuiz, "Which algorithm is widely used to find the shortest path from a single source vertex to all other vertices in a weighted graph with non-negative edge weights?", 1,
                new String[]{"Dijkstra Algorithm", "Kruskal Algorithm", "Prim Algorithm", "Floyd-Warshall Algorithm"}, 1);
    }

    private void addQuestion(Quiz quiz, String text, int marks, String[] options, int correctIndex1Based) {
        Question q = new Question(quiz, text, marks);
        q = questionRepository.save(q);

        for (int i = 0; i < options.length; i++) {
            boolean isCorrect = (i + 1 == correctIndex1Based);
            Option opt = new Option(q, options[i], isCorrect);
            optionRepository.save(opt);
        }
    }
}
