import std.stdio : writeln;
import std.conv;

import sgio.read.read;
import sgio.SCSIDevice;
import sgio.utility;
import sgio.exceptions;


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

    try
    {
        auto dev = new SCSIDeviceBS(deviceName);
        auto read16 = new Read16(dev, lbaOffset, numBlocks);
        write(bufferToHexDump(read16.datain, read16.datain.length));
    }
    catch (SCSIException err)
    {
        return 1;
    }

    return 0;
}
