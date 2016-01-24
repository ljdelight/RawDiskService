package com.ljdelight.rawdisk;

import org.apache.thrift.TException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class RawDiskHandlerTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void test() throws TException {
        this.exception.expect(TException.class);
        RawDiskHandler h = new RawDiskHandler();
        h.readLBAPretty("/this/does/not/exist", 1);
    }

}
