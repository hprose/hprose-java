/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hprose.util.concurrent;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author andot
 */
public class PromiseTest {

    public PromiseTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of value method, of class Promise.
     */
    @Test
    public void testValue() {
        System.out.println("value");
        Promise result = Promise.value("x");
        result.done(new Action<String>() {
            public void call(String value) throws Throwable {
                System.out.println(value);
                Assert.assertEquals("y", value);
            }
        });
    }

//    /**
//     * Test of error method, of class Promise.
//     */
//    @Test
//    public void testError() {
//        System.out.println("error");
//        Throwable reason = null;
//        Promise expResult = null;
//        Promise result = Promise.error(reason);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of delayed method, of class Promise.
//     */
//    @Test
//    public void testDelayed() {
//        System.out.println("delayed");
//        long duration = 0L;
//        Object value = null;
//        Promise expResult = null;
//        Promise result = Promise.delayed(duration, value);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of sync method, of class Promise.
//     */
//    @Test
//    public void testSync() {
//        System.out.println("sync");
//        Callable computation = null;
//        Promise expResult = null;
//        Promise result = Promise.sync(computation);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of isThenable method, of class Promise.
//     */
//    @Test
//    public void testIsThenable() {
//        System.out.println("isThenable");
//        Object value = null;
//        boolean expResult = false;
//        boolean result = Promise.isThenable(value);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of isPromise method, of class Promise.
//     */
//    @Test
//    public void testIsPromise() {
//        System.out.println("isPromise");
//        Object value = null;
//        boolean expResult = false;
//        boolean result = Promise.isPromise(value);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of toPromise method, of class Promise.
//     */
//    @Test
//    public void testToPromise() {
//        System.out.println("toPromise");
//        Object value = null;
//        Promise expResult = null;
//        Promise result = Promise.toPromise(value);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of all method, of class Promise.
//     */
//    @Test
//    public void testAll_ObjectArr() {
//        System.out.println("all");
//        Object[] array = null;
//        Promise expResult = null;
//        Promise result = Promise.all(array);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of all method, of class Promise.
//     */
//    @Test
//    public void testAll_Promise() {
//        System.out.println("all");
//        Promise promise = null;
//        Promise expResult = null;
//        Promise result = Promise.all(promise);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of all method, of class Promise.
//     */
//    @Test
//    public void testAll_0args() {
//        System.out.println("all");
//        Promise instance = new Promise();
//        Promise expResult = null;
//        Promise result = instance.all();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of join method, of class Promise.
//     */
//    @Test
//    public void testJoin() {
//        System.out.println("join");
//        Object[] args = null;
//        Promise expResult = null;
//        Promise result = Promise.join(args);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of race method, of class Promise.
//     */
//    @Test
//    public void testRace_ObjectArr() {
//        System.out.println("race");
//        Object[] array = null;
//        Promise expResult = null;
//        Promise result = Promise.race(array);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of race method, of class Promise.
//     */
//    @Test
//    public void testRace_Promise() {
//        System.out.println("race");
//        Promise promise = null;
//        Promise expResult = null;
//        Promise result = Promise.race(promise);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of race method, of class Promise.
//     */
//    @Test
//    public void testRace_0args() {
//        System.out.println("race");
//        Promise instance = new Promise();
//        Promise expResult = null;
//        Promise result = instance.race();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of any method, of class Promise.
//     */
//    @Test
//    public void testAny_ObjectArr() {
//        System.out.println("any");
//        Object[] array = null;
//        Promise expResult = null;
//        Promise result = Promise.any(array);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of any method, of class Promise.
//     */
//    @Test
//    public void testAny_Promise() {
//        System.out.println("any");
//        Promise promise = null;
//        Promise expResult = null;
//        Promise result = Promise.any(promise);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of any method, of class Promise.
//     */
//    @Test
//    public void testAny_0args() {
//        System.out.println("any");
//        Promise instance = new Promise();
//        Promise expResult = null;
//        Promise result = instance.any();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of run method, of class Promise.
//     */
//    @Test
//    public void testRun() {
//        System.out.println("run");
//        Callback handler = null;
//        Object[] args = null;
//        Promise expResult = null;
//        Promise result = Promise.run(handler, args);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of forEach method, of class Promise.
//     */
//    @Test
//    public void testForEach_Action_ObjectArr() {
//        System.out.println("forEach");
//        Promise expResult = null;
//        Promise result = Promise.forEach(null);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of forEach method, of class Promise.
//     */
//    @Test
//    public void testForEach_ObjectArr_Handler() {
//        System.out.println("forEach");
//        Promise expResult = null;
//        Promise result = Promise.forEach(null);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of forEach method, of class Promise.
//     */
//    @Test
//    public void testForEach_Promise_Handler() {
//        System.out.println("forEach");
//        Promise expResult = null;
//        Promise result = Promise.forEach(null);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of forEach method, of class Promise.
//     */
//    @Test
//    public void testForEach_Handler() {
//        System.out.println("forEach");
//        Promise instance = new Promise();
//        Promise expResult = null;
//        Promise result = instance.forEach(null);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of every method, of class Promise.
//     */
//    @Test
//    public void testEvery_Func_ObjectArr() {
//        System.out.println("every");
//        Promise<Boolean> expResult = null;
//        Promise<Boolean> result = Promise.every(null);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of every method, of class Promise.
//     */
//    @Test
//    public void testEvery_ObjectArr_Handler() {
//        System.out.println("every");
//        Promise<Boolean> expResult = null;
//        Promise<Boolean> result = Promise.every(null);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of every method, of class Promise.
//     */
//    @Test
//    public void testEvery_Promise_Handler() {
//        System.out.println("every");
//        Promise<Boolean> expResult = null;
//        Promise<Boolean> result = Promise.every(null);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of every method, of class Promise.
//     */
//    @Test
//    public void testEvery_Handler() {
//        System.out.println("every");
//        Promise instance = new Promise();
//        Promise<Boolean> expResult = null;
//        Promise<Boolean> result = instance.every(null);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of some method, of class Promise.
//     */
//    @Test
//    public void testSome_Func_ObjectArr() {
//        System.out.println("some");
//        Promise<Boolean> expResult = null;
//        Promise<Boolean> result = Promise.some(null);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of some method, of class Promise.
//     */
//    @Test
//    public void testSome_ObjectArr_Handler() {
//        System.out.println("some");
//        Promise<Boolean> expResult = null;
//        Promise<Boolean> result = Promise.some(null);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of some method, of class Promise.
//     */
//    @Test
//    public void testSome_Promise_Handler() {
//        System.out.println("some");
//        Promise<Boolean> expResult = null;
//        Promise<Boolean> result = Promise.some(null);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of some method, of class Promise.
//     */
//    @Test
//    public void testSome_Handler() {
//        System.out.println("some");
//        Promise instance = new Promise();
//        Promise<Boolean> expResult = null;
//        Promise<Boolean> result = instance.some(null);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of filter method, of class Promise.
//     */
//    @Test
//    public void testFilter_Func_ObjectArr() {
//        System.out.println("filter");
//        Promise expResult = null;
//        Promise result = Promise.filter(null);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of filter method, of class Promise.
//     */
//    @Test
//    public void testFilter_ObjectArr_Handler() {
//        System.out.println("filter");
//        Promise expResult = null;
//        Promise result = Promise.filter(null);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of filter method, of class Promise.
//     */
//    @Test
//    public void testFilter_Promise_Handler() {
//        System.out.println("filter");
//        Promise expResult = null;
//        Promise result = Promise.filter(null);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of filter method, of class Promise.
//     */
//    @Test
//    public void testFilter_Handler() {
//        System.out.println("filter");
//        Promise instance = new Promise();
//        Promise expResult = null;
//        Promise result = instance.filter(null);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of map method, of class Promise.
//     */
//    @Test
//    public void testMap_Func_ObjectArr() {
//        System.out.println("map");
//        Promise expResult = null;
//        Promise result = Promise.map(null);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of map method, of class Promise.
//     */
//    @Test
//    public void testMap_ObjectArr_Handler() {
//        System.out.println("map");
//        Promise expResult = null;
//        Promise result = Promise.map(null);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of map method, of class Promise.
//     */
//    @Test
//    public void testMap_Promise_Handler() {
//        System.out.println("map");
//        Promise expResult = null;
//        Promise result = Promise.map(null);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of map method, of class Promise.
//     */
//    @Test
//    public void testMap_Handler() {
//        System.out.println("map");
//        Promise instance = new Promise();
//        Promise expResult = null;
//        Promise result = instance.map(null);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of reduce method, of class Promise.
//     */
//    @Test
//    public void testReduce_ObjectArr_Reducer() {
//        System.out.println("reduce");
//        Promise expResult = null;
//        Promise result = Promise.reduce(null);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of reduce method, of class Promise.
//     */
//    @Test
//    public void testReduce_Promise_Reducer() {
//        System.out.println("reduce");
//        Promise expResult = null;
//        Promise result = Promise.reduce(null);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of reduce method, of class Promise.
//     */
//    @Test
//    public void testReduce_Reducer() {
//        System.out.println("reduce");
//        Promise instance = new Promise();
//        Promise expResult = null;
//        Promise result = instance.reduce(null);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of reduce method, of class Promise.
//     */
//    @Test
//    public void testReduce_3args_1() {
//        System.out.println("reduce");
//        Promise expResult = null;
//        Promise result = Promise.reduce(null);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of reduce method, of class Promise.
//     */
//    @Test
//    public void testReduce_3args_2() {
//        System.out.println("reduce");
//        Promise expResult = null;
//        Promise result = Promise.reduce(null);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of reduce method, of class Promise.
//     */
//    @Test
//    public void testReduce_Reducer_GenericType() {
//        System.out.println("reduce");
//        Promise instance = new Promise();
//        Promise expResult = null;
//        Promise result = instance.reduce(null);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of reduceRight method, of class Promise.
//     */
//    @Test
//    public void testReduceRight_ObjectArr_Reducer() {
//        System.out.println("reduceRight");
//        Promise expResult = null;
//        Promise result = Promise.reduceRight(null);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of reduceRight method, of class Promise.
//     */
//    @Test
//    public void testReduceRight_Promise_Reducer() {
//        System.out.println("reduceRight");
//        Promise expResult = null;
//        Promise result = Promise.reduceRight(null);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of reduceRight method, of class Promise.
//     */
//    @Test
//    public void testReduceRight_Reducer() {
//        System.out.println("reduceRight");
//        Promise instance = new Promise();
//        Promise expResult = null;
//        Promise result = instance.reduceRight(null);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of reduceRight method, of class Promise.
//     */
//    @Test
//    public void testReduceRight_3args_1() {
//        System.out.println("reduceRight");
//        Promise expResult = null;
//        Promise result = Promise.reduceRight(null);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of reduceRight method, of class Promise.
//     */
//    @Test
//    public void testReduceRight_3args_2() {
//        System.out.println("reduceRight");
//        Promise expResult = null;
//        Promise result = Promise.reduceRight(null);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of reduceRight method, of class Promise.
//     */
//    @Test
//    public void testReduceRight_Reducer_GenericType() {
//        System.out.println("reduceRight");
//        Promise instance = new Promise();
//        Promise expResult = null;
//        Promise result = instance.reduceRight(null);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of resolve method, of class Promise.
//     */
//    @Test
//    public void testResolve() {
//        System.out.println("resolve");
//        Object value = null;
//        Promise instance = new Promise();
//        instance.resolve(value);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of reject method, of class Promise.
//     */
//    @Test
//    public void testReject() {
//        System.out.println("reject");
//        Throwable e = null;
//        Promise instance = new Promise();
//        instance.reject(e);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of then method, of class Promise.
//     */
//    @Test
//    public void testThen_Callback() {
//        System.out.println("then");
//        Promise instance = new Promise();
//        Promise expResult = null;
//        Promise result = instance.then(null);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of then method, of class Promise.
//     */
//    @Test
//    public void testThen_Callback_Callback() {
//        System.out.println("then");
//        Promise instance = new Promise();
//        Promise expResult = null;
//        Promise result = instance.then(null);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of done method, of class Promise.
//     */
//    @Test
//    public void testDone_Callback() {
//        System.out.println("done");
//        Promise instance = new Promise();
//        instance.done(null);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of done method, of class Promise.
//     */
//    @Test
//    public void testDone_Callback_Callback() {
//        System.out.println("done");
//        Promise instance = new Promise();
//        instance.done(null);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getState method, of class Promise.
//     */
//    @Test
//    public void testGetState() {
//        System.out.println("getState");
//        Promise instance = new Promise();
//        Promise.State expResult = null;
//        Promise.State result = instance.getState();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getValue method, of class Promise.
//     */
//    @Test
//    public void testGetValue() {
//        System.out.println("getValue");
//        Promise instance = new Promise();
//        Object expResult = null;
//        Object result = instance.getValue();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getReason method, of class Promise.
//     */
//    @Test
//    public void testGetReason() {
//        System.out.println("getReason");
//        Promise instance = new Promise();
//        Throwable expResult = null;
//        Throwable result = instance.getReason();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of catchError method, of class Promise.
//     */
//    @Test
//    public void testCatchError_Callback_Func() {
//        System.out.println("catchError");
//        Callback<Throwable> onreject = null;
//        Func<Boolean, Throwable> test = null;
//        Promise instance = new Promise();
//        Promise expResult = null;
//        Promise result = instance.catchError(onreject, test);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of catchError method, of class Promise.
//     */
//    @Test
//    public void testCatchError_Callback() {
//        System.out.println("catchError");
//        Callback<Throwable> onreject = null;
//        Promise instance = new Promise();
//        Promise expResult = null;
//        Promise result = instance.catchError(onreject);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of fail method, of class Promise.
//     */
//    @Test
//    public void testFail() {
//        System.out.println("fail");
//        Callback<Throwable> onreject = null;
//        Promise instance = new Promise();
//        instance.fail(onreject);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of whenComplete method, of class Promise.
//     */
//    @Test
//    public void testWhenComplete_Callable() {
//        System.out.println("whenComplete");
//        Callable action = null;
//        Promise instance = new Promise();
//        Promise expResult = null;
//        Promise result = instance.whenComplete(action);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of whenComplete method, of class Promise.
//     */
//    @Test
//    public void testWhenComplete_Runnable() {
//        System.out.println("whenComplete");
//        Runnable action = null;
//        Promise instance = new Promise();
//        Promise expResult = null;
//        Promise result = instance.whenComplete(action);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of complete method, of class Promise.
//     */
//    @Test
//    public void testComplete() {
//        System.out.println("complete");
//        Callback oncomplete = null;
//        Promise instance = new Promise();
//        Promise expResult = null;
//        Promise result = instance.complete(oncomplete);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of always method, of class Promise.
//     */
//    @Test
//    public void testAlways() {
//        System.out.println("always");
//        Callback oncomplete = null;
//        Promise instance = new Promise();
//        instance.always(oncomplete);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of fill method, of class Promise.
//     */
//    @Test
//    public void testFill() {
//        System.out.println("fill");
//        Promise instance = new Promise();
//        instance.fill(null);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of timeout method, of class Promise.
//     */
//    @Test
//    public void testTimeout_long_Throwable() {
//        System.out.println("timeout");
//        long duration = 0L;
//        Throwable reason = null;
//        Promise instance = new Promise();
//        Promise expResult = null;
//        Promise result = instance.timeout(duration, reason);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of timeout method, of class Promise.
//     */
//    @Test
//    public void testTimeout_long() {
//        System.out.println("timeout");
//        long duration = 0L;
//        Promise instance = new Promise();
//        Promise expResult = null;
//        Promise result = instance.timeout(duration);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of delay method, of class Promise.
//     */
//    @Test
//    public void testDelay() {
//        System.out.println("delay");
//        long duration = 0L;
//        Promise instance = new Promise();
//        Promise expResult = null;
//        Promise result = instance.delay(duration);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of tap method, of class Promise.
//     */
//    @Test
//    public void testTap() {
//        System.out.println("tap");
//        Promise instance = new Promise();
//        Promise expResult = null;
//        Promise result = instance.tap(null);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

}
