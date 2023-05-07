package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.service.AuthService;
import ru.skypro.homework.service.UserService;


@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/users")
@Tag(name = "Пользователи")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthService authService;

    @Operation(
            summary = "Обновление пароля",
            description = "Изменение пароля пользователя из тела запроса"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK. Установлен новый пароль"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content()}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content()}),
    }
    )
    @PostMapping("/set_password")
    public ResponseEntity<Void> setPassword(@RequestBody NewPasswordDto newPasswordDto) {
        authService.changePassword(newPasswordDto);
        return ResponseEntity.ok().build();
    }


    @Operation(
            summary = "Получить информацию об авторизованном пользователе"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK. Данные пользователя получены",
                    content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = UserDto.class))
                            )
                    }
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content()})
    }
    )
    @GetMapping("/me")
    public ResponseEntity<UserDto> getUser() {
        UserDto currentUserDto = userService.getUserDto();
        return ResponseEntity.ok().body(currentUserDto);
    }

    @Operation(
            summary = "Обновить информацию об авторизованном пользователе",
            description = "Обновление данных пользователя из тела запроса"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "OK. Данные пользователя обновлены",
                    content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = UserDto.class))
                            )
                    }
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content()})
    }
    )
    @PatchMapping("/me")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto userDto) {
        UserDto newUserDto = userService.updateUserDto(userDto);
        return ResponseEntity.ok().body(newUserDto);
    }

    @Operation(
            summary = "Обновить аватар авторизованного пользователя",
            description = "Обновление изображения пользователя из тела запроса"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK. Изображение пользователя обновлено", content = {@Content()}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content()})
    }
    )
    @PatchMapping(value = "/me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateUserImage(
            @Parameter(schema = @Schema(type = "string", format = "binary"))
            @RequestPart MultipartFile image) {
        return ResponseEntity.ok().build();
    }
}
