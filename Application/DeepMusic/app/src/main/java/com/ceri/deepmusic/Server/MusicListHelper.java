//
// Copyright (c) ZeroC, Inc. All rights reserved.
//
//
// Ice version 3.7.5
//
// <auto-generated>
//
// Generated from file `Server.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package Server;

/**
 * Helper class for marshaling/unmarshaling MusicList.
 **/
public final class MusicListHelper
{
    public static void write(com.zeroc.Ice.OutputStream ostr, Music[] v)
    {
        if(v == null)
        {
            ostr.writeSize(0);
        }
        else
        {
            ostr.writeSize(v.length);
            for(int i0 = 0; i0 < v.length; i0++)
            {
                Music.ice_write(ostr, v[i0]);
            }
        }
    }

    public static Music[] read(com.zeroc.Ice.InputStream istr)
    {
        final Music[] v;
        final int len0 = istr.readAndCheckSeqSize(8);
        v = new Music[len0];
        for(int i0 = 0; i0 < len0; i0++)
        {
            v[i0] = Music.ice_read(istr);
        }
        return v;
    }

    public static void write(com.zeroc.Ice.OutputStream ostr, int tag, java.util.Optional<Music[]> v)
    {
        if(v != null && v.isPresent())
        {
            write(ostr, tag, v.get());
        }
    }

    public static void write(com.zeroc.Ice.OutputStream ostr, int tag, Music[] v)
    {
        if(ostr.writeOptional(tag, com.zeroc.Ice.OptionalFormat.FSize))
        {
            int pos = ostr.startSize();
            MusicListHelper.write(ostr, v);
            ostr.endSize(pos);
        }
    }

    public static java.util.Optional<Music[]> read(com.zeroc.Ice.InputStream istr, int tag)
    {
        if(istr.readOptional(tag, com.zeroc.Ice.OptionalFormat.FSize))
        {
            istr.skip(4);
            Music[] v;
            v = MusicListHelper.read(istr);
            return java.util.Optional.of(v);
        }
        else
        {
            return java.util.Optional.empty();
        }
    }
}
