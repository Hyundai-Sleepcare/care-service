package com.sleepcare.server.profile.dto;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.BigDecimal;

// 0.5 단위인지 검사한다 (23.0, 23.5는 통과 / 23.3은 실패)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = HalfStep.Validator.class)
public @interface HalfStep {

    String message() default "0.5 단위여야 합니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<HalfStep, BigDecimal> {
        @Override
        public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
            if (value == null) {
                return true; // null은 @NotNull이 잡는다
            }
            // 2를 곱해서 정수면 0.5 단위
            return value.multiply(BigDecimal.TWO).stripTrailingZeros().scale() <= 0;
        }
    }
}
