package com.miaoshaproject;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue(){
        GreetingService greetService1 = message -> System.out.println("Hello " + message);
        greetService1.sayMessage("123");
        assertTrue( true );
    }
}
@FunctionalInterface
interface GreetingService{
    void sayMessage(String message);
}
