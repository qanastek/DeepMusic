package com.ceri.deepmusic.models;

import android.util.Log;

import Server.HelloPrx;

public class IceServer {

    // Singleton Instance
    private static IceServer INSTANCE;

    // Hello Proxy
    private static Server.HelloPrx hello;

    // Private Constructor
    private IceServer() {

        Log.d("Zeroc-Ice","1");

        try(com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize())
        {
            communicator.getProperties().setProperty("Ice.Default.Package", "com.ceri.deepmusic.Server.hello");

            Log.d("Zeroc-Ice","2");
            com.zeroc.Ice.ObjectPrx base = communicator.stringToProxy("hello:default -h 192.168.0.29 -p 10001");
//            com.zeroc.Ice.ObjectPrx base = communicator.stringToProxy("hello:default -h 192.168.0.29 -p 10001");

            Log.d("Zeroc-Ice","3");
            IceServer.hello = Server.HelloPrx.checkedCast(base);

            Log.d("Zeroc-Ice","4");
            if(hello == null)
            {
                throw new Error("Invalid proxy");
            }
        }
        catch (Exception e) {
            Log.e("Zeroc-Ice", e.getMessage(), e);
        }
    }

    public static IceServer getInstance() {

        // Check if isn't instantiated
        if (IceServer.INSTANCE == null) {
            IceServer.INSTANCE = new IceServer();
        }

        return IceServer.INSTANCE;
    }

    public HelloPrx getHello() {
        return IceServer.hello;
    }
}
