package spring.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MyAspect {

    @Before("execution(public * *(*))")
    public void addStudentAdvice(){
        System.out.println("Before service advice");
    }
}