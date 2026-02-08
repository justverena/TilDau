package kz.kbtu.tildau.controller;

import kz.kbtu.tildau.dto.course.CourseFullResponse;
import kz.kbtu.tildau.dto.course.CourseShortResponse;
import kz.kbtu.tildau.security.CustomerUserDetails;
import kz.kbtu.tildau.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public ResponseEntity<List<CourseShortResponse>> getCourses(
            @AuthenticationPrincipal CustomerUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UUID userId = userDetails.getUser().getId();
        List<CourseShortResponse> courses = courseService.getCoursesForUser(userId);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseFullResponse> getCourseById(
            @AuthenticationPrincipal CustomerUserDetails userDetails,
            @PathVariable("id") UUID courseId
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UUID userId = userDetails.getUser().getId();
        try {
            CourseFullResponse course = courseService.getCourseForUser(userId, courseId);
            return ResponseEntity.ok(course);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}