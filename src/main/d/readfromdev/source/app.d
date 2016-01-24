import std.stdio : writeln;
import std.conv;
import std.ascii;

import sgio.read.read;
import sgio.SCSIDevice;
import sgio.utility;
import sgio.exceptions;
import sgio.SCSICommand;


string writeBufferPretty(const(ubyte)[] buff, ulong length)
{
   string prettyBuffer = "";

   string lhs = format("%06x", 0) ~ ":";
   string rhs = "|";

   for (int idx = 1; idx <= length; idx++) {
      string byteAsHex = format("%02x", buff[idx-1]);
      char byteAsChar = to!char(isPrintable(buff[idx-1]) ? toLower(buff[idx-1]) : '.');

      lhs ~= " " ~ byteAsHex;
      rhs ~= byteAsChar;

      if (idx > 0 && idx % 16 == 0) {
         prettyBuffer ~= lhs ~ "  " ~ rhs ~ "|\n";
         lhs = format("%06x", idx) ~ ":";
         rhs = "|";
      } else if (idx > 0 && idx % 8 == 0) {
         lhs ~= " ";
      }
   }

   return prettyBuffer;
}

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
        write(writeBufferPretty(read16.datain, read16.datain.length));
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
