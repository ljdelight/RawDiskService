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

public class RawDiskHandler implements RawDisk.Iface {

    @Override
    public List<DiskDevice> getDiskDevices() throws TException {
        // TODO Auto-generated method stub
        return new LinkedList<DiskDevice>();
        // return null;
    }

    @Override
    public String readLBAPretty(String path, long offset_lba) throws TException {
        // Read 1 block from the device at give offset
        ProcessBuilder rawDiskPb = new ProcessBuilder("readfromdev",
                path, Long.toString(offset_lba), "1");
        rawDiskPb.redirectErrorStream(true);
        System.out.println(rawDiskPb.command());
        try {
            Process p = rawDiskPb.start();

            // capture the call's output
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            String result = builder.toString();
            p.waitFor(3, TimeUnit.SECONDS);
            return result;

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "none";
    }
}
