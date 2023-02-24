package bgu.spl.mics;

import static org.junit.Assert.*;


import org.junit.Before;
import org.junit.Test;

class FutureTest<T> {

    private Future<T> future;

    @Before protected void setUp() throws Exception{
        future=new Future<T>();
    }


    @Test void testResolve() {
        future.resolve((T)"result");
        assertTrue(future.get()=="result");
    }

    @Test void testIsDone() {
        assertTrue(future.get()==null);
        future.resolve((T)"result");
        assertTrue(future.get()=="result");
    }

    @Test void testGetTime() {}
}