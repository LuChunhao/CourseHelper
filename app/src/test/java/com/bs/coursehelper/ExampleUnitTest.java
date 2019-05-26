package com.bs.coursehelper;

import org.junit.Test;

import java.math.BigDecimal;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
//        assertEquals(4, 2 + 2);


//        String s = "660001";
//        System.out.println(!(s.startsWith("55") || s.startsWith("66")));

        BigDecimal bigDecimal= new BigDecimal(3.16);
        String scoreStr = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).toString();
        System.out.println(scoreStr);
        float score = Float.parseFloat(scoreStr);
        System.out.println("score===" + score);
    }
}