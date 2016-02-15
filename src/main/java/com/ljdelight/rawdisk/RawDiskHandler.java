package com.ljdelight.rawdisk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.thrift.TException;

import com.ljdelight.rawdisk.generated.DiskDevice;
import com.ljdelight.rawdisk.generated.RawDisk;
import com.ljdelight.rawdisk.generated.ServerNativeException;

public class RawDiskHandler implements RawDisk.Iface {
    private static final Logger logger = LogManager.getLogger(RawDiskHandler.class);
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
        BufferedReader reader = null;

        try {
            logger.debug("Executing {}", rawDiskPb.command());
            Process p = rawDiskPb.start();

            // capture the call's output
            StringBuilder builder = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF8"));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }

            p.waitFor(RawDiskHandler.READ_WAIT_TIME_MS, TimeUnit.MILLISECONDS);

            if (p.exitValue() != 0) {
                logger.error("Non-zero exit status in readfromdev and throwing ServerNativeException");
                logger.error("Process output: '{}'", builder.toString());
                throw new ServerNativeException("Non-zero exit status from readfromdev");
            }
            return builder.toString();

        } catch (IOException | InterruptedException e) {
            logger.error("Problem creating process", e);
            throw new ServerNativeException(e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    logger.error("Problem creating process", e);
                    throw new ServerNativeException(e.getMessage());
                }
            }
        }
    }
}
