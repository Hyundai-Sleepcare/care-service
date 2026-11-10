package com.sleepcare.server.common;

import com.sleepcare.server.common.owner.OwnerIdInterceptor;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 공통 에러·헤더 처리를 시험하기 위한 테스트 전용 컨트롤러
@RestController
@RequestMapping("/api/v1/test")
public class TestApiController {

    public record Inner(@NotNull Integer level) {
    }

    public record EchoRequest(@NotBlank String name, @NotNull @Valid Inner inner) {
    }

    @GetMapping("/owner")
    public String owner(@RequestAttribute(OwnerIdInterceptor.ATTRIBUTE) String ownerId) {
        return ownerId;
    }

    @PostMapping("/echo")
    public EchoRequest echo(@Valid @RequestBody EchoRequest request) {
        return request;
    }

    @GetMapping("/items/{itemId}")
    public Long item(@PathVariable Long itemId) {
        return itemId;
    }

    @GetMapping("/boom")
    public String boom() {
        throw new IllegalStateException("테스트용 예외");
    }
}
