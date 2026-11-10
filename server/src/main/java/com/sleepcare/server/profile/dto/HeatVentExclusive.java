package com.sleepcare.server.profile.dto;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 열선과 통풍이 동시에 켜져 있으면(둘 다 1 이상) 실패. 에러는 seatVent.level에 붙인다
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = HeatVentExclusive.Validator.class)
public @interface HeatVentExclusive {

    String message() default "열선과 통풍은 동시에 켤 수 없습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<HeatVentExclusive, Preset> {
        @Override
        public boolean isValid(Preset preset, ConstraintValidatorContext context) {
            if (preset == null || preset.seatHeat() == null || preset.seatVent() == null
                    || preset.seatHeat().level() == null || preset.seatVent().level() == null) {
                return true; // 빠진 값은 @NotNull이 잡는다
            }
            if (preset.seatHeat().level() > 0 && preset.seatVent().level() > 0) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                        .addPropertyNode("seatVent").addPropertyNode("level")
                        .addConstraintViolation();
                return false;
            }
            return true;
        }
    }
}
