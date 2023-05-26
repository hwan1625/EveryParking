package com.everyparking.server.controller.app;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TestController {

    /**
     * { 'id': id, 'pass': pass, 's_number': s_number, 's_name': s_name }
     */

    @CrossOrigin("*")
    @PostMapping("/api/login")
    public LoginDto login(@RequestBody LoginDto loginDto) {

        log.info("[login] {}", loginDto.toString());

        return loginDto;
    }

    @GetMapping("/api/login")
    public String loginTest() {
        return "ok";
    }

    @Data
    private static class LoginDto {

        private int id;

        private String pass;

        private String studentNumber;

        private String studentName;

        public LoginDto(int id, String pass, String s_number, String s_name) {
            this.id = id;
            this.pass = pass;
            this.studentNumber = s_number;
            this.studentName = s_name;
        }
    }

    @Data
    private static class Student {


        public Student(int studentId, int age, String studentName) {
            this.studentId = studentId;
            this.age = age;
            this.studentName = studentName;
        }

        private int studentId;

        private int age;

        private String studentName;

    }
}
