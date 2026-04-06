package kz.kbtu.tildau.controller;

import kz.kbtu.tildau.dto.course.CourseFullResponse;
import kz.kbtu.tildau.dto.course.CourseShortResponse;
import kz.kbtu.tildau.dto.nextStep.NextStepResponse;
import kz.kbtu.tildau.security.CustomerUserDetails;
import kz.kbtu.tildau.service.CourseService;
import kz.kbtu.tildau.service.NextStepService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;
    private final NextStepService nextStepService;

    @GetMapping
    public ResponseEntity<List<CourseShortResponse>> getCourses(
            @AuthenticationPrincipal CustomerUserDetails userDetails) {
        UUID userId = userDetails.getUser().getId();
        List<CourseShortResponse> courses = courseService.getCoursesForUser(userId);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseFullResponse> getCourseById(
            @AuthenticationPrincipal CustomerUserDetails userDetails,
            @PathVariable("id") UUID courseId) {

        UUID userId = userDetails.getUser().getId();
        CourseFullResponse course = courseService.getCourseForUser(userId, courseId);
        return ResponseEntity.ok(course);
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<NextStepResponse> startCourse(
            @AuthenticationPrincipal CustomerUserDetails userDetails,
            @PathVariable("id") UUID courseId) {

        UUID userId = userDetails.getUser().getId();

        NextStepResponse response = courseService.startCourse(userId, courseId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/resume")
    public ResponseEntity<NextStepResponse> getNextStep(
            @AuthenticationPrincipal CustomerUserDetails userDetails,
            @PathVariable UUID id
    ) {
        UUID userId = userDetails.getUser().getId();
        return ResponseEntity.ok(nextStepService.getNextStep(userId, id));
    }

}