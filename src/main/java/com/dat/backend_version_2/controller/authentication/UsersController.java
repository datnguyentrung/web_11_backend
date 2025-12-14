package com.dat.backend_version_2.controller.authentication;

import com.dat.backend_version_2.domain.training.Coach;
import com.dat.backend_version_2.domain.training.Student;
import com.dat.backend_version_2.dto.RestResponse;
import com.dat.backend_version_2.dto.authentication.ChangePasswordReq;
import com.dat.backend_version_2.dto.authentication.UserRes;
import com.dat.backend_version_2.mapper.training.CoachMapper;
import com.dat.backend_version_2.mapper.training.StudentMapper;
import com.dat.backend_version_2.service.authentication.UsersService;
import com.dat.backend_version_2.service.training.CoachService;
import com.dat.backend_version_2.service.training.StudentService;
import com.dat.backend_version_2.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UsersController {
    private final UsersService usersService;
    private final CoachService coachService;
    private final StudentService studentService;
    private final StudentMapper studentMapper;

    @PostMapping("/me/change-password")
    public ResponseEntity<RestResponse<String>> changePassword(
            @RequestBody ChangePasswordReq request,
            Authentication authentication) {
        String idUser = authentication.getName();
        usersService.changePassword(idUser, request);

        RestResponse<String> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Đổi mật khẩu thành công");
        res.setData(null); // hoặc có thể set thêm thông tin gì đó nếu cần

        return ResponseEntity.ok(res);
    }

    @GetMapping("/me")
    public ResponseEntity<UserRes> getCurrentUser(Authentication authentication) throws IdInvalidException {
        String idUser = authentication.getName();
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        return switch (role) {
            case "STUDENT" -> {
                Student student = studentService.getStudentById(idUser);
                yield ResponseEntity.ok(studentMapper.studentToUserRes(student));
            }
            case "COACH" -> {
                Coach coach = coachService.getCoachById(UUID.fromString(idUser));
                yield ResponseEntity.ok(CoachMapper.coachToUserRes(coach));
            }
            default -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        };
    }
}
