package com.jenkins.job.firstjenkinsjob;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FirstJenkinsJobApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(FirstJenkinsJobApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("Hello from first Jenkins job");
    }

}
