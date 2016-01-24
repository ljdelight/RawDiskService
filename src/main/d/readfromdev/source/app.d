import std.stdio : writeln;
import std.conv;
import std.ascii;

import sgio.read.read;
import sgio.SCSIDevice;
import sgio.utility;
import sgio.exceptions;
import sgio.SCSICommand;


int main(string[] args)
{
    if (args.length <= 1)
    {
        writeln("app <device> <lbaOffset> <blocks>");
        return 1;
    }

    auto deviceName = args[1];
    auto lbaOffset = to!ulong(args[2]);
    auto numBlocks = to!uint(args[3]);
    // TODO input checking
    // TODO: this should be generalized somehow. nasty os-specific.
    version (Windows)
    {
        wchar* thefile = std.utf.toUTFz!(wchar*)(deviceName);
        auto file = CreateFileW(thefile,
            GENERIC_WRITE|GENERIC_READ,
            FILE_SHARE_WRITE|FILE_SHARE_READ,
            null, OPEN_EXISTING,
            FILE_ATTRIBUTE_NORMAL, null);

        if (file == INVALID_HANDLE_VALUE)
        {
            return 1;
        }
        auto dev = new SCSIDeviceBS(cast(uint)(file));
    }
    version (Posix)
    {
        auto file = File(deviceName, "rb");
        auto dev = new SCSIDeviceBS(file.fileno());
    }

    try
    {
        auto read16 = new Read16(dev, lbaOffset, numBlocks);
        write(bufferToHexDump(read16.datain, read16.datain.length));
    }
    catch (SCSIException err)
    {
        return 1;
    }
    finally
    {
        version (Posix)
        {
            file.close();
        }
        version (Windows)
        {
            CloseHandle(file);
        }
    }

    return 0;
}
