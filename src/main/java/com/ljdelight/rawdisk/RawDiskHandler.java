package com.ljdelight.rawdisk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.thrift.TException;

import com.ljdelight.rawdisk.generated.DiskDevice;
import com.ljdelight.rawdisk.generated.RawDisk;
import com.ljdelight.rawdisk.generated.ServerNativeException;

public class RawDiskHandler implements RawDisk.Iface {

    private static final int READ_WAIT_TIME_MS = 3000;

    @Override
    public List<DiskDevice> getDiskDevices() throws TException {
        // TODO Auto-generated method stub
        return new LinkedList<DiskDevice>();
        // return null;
    }

    @Override
    public String readLBAPretty(String path, long offset_lba) throws TException {
        // Read 1 block from the device at give offset
        ProcessBuilder rawDiskPb = new ProcessBuilder("readfromdev", path, Long.toString(offset_lba), "1");
        rawDiskPb.redirectErrorStream(true);

        try {
            System.out.println("Executing " + rawDiskPb.command());
            Process p = rawDiskPb.start();

            // capture the call's output
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }

            p.waitFor(RawDiskHandler.READ_WAIT_TIME_MS, TimeUnit.MILLISECONDS);

            if (p.exitValue() != 0) {
                System.err.println("Non-zero exit status in readfromdev:");
                System.err.println(builder.toString());
                throw new ServerNativeException("Non-zero exit status from readfromdev");
            }
            return builder.toString();

        } catch (IOException | InterruptedException e) {
            System.err.println("Caught exception: " + e.getMessage());
            throw new ServerNativeException(e.getMessage());
        }
    }
}
