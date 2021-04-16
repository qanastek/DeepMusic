package com.ceri.deepmusic.models;

import android.util.Log;

import com.zeroc.Ice.LocatorFinderPrx;

import java.nio.file.FileSystems;
import java.nio.file.Paths;

import Server.HelloPrx;

public class IceServer {

    // Singleton Instance
    private static IceServer INSTANCE;

    // Hello Proxy
    private static Server.HelloPrx hello;

    // Private Constructor
    private IceServer() {

        Log.d("Zeroc-Ice","1");


//        hello = None
//        try:
//          print("****** Inside try ******")
//          hello = Server.HelloPrx.checkedCast(communicator.stringToProxy("hello"))
//        except Ice.NotRegisteredException:
//          print("****** Inside Ice.NotRegisteredException ******")
//          query = IceGrid.QueryPrx.checkedCast(communicator.stringToProxy("DemoIceGrid/Query"))
//          hello = Server.HelloPrx.checkedCast(query.findObjectByType("::DemoIceGrid::Hello"))

        String[] args = new String[0];
        java.util.List<String> extraArgs = new java.util.ArrayList<>();

        try(com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize())
//        try(com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args, "config.client", extraArgs))
        {
            communicator.getProperties().setProperty("Ice.Default.Package", "com.ceri.deepmusic.Server.hello");
//            communicator.getProperties().setProperty("Ice.Default.Locator", "DemoIceGrid/Locator:default -h 192.168.0.29 -p 4061");

            com.zeroc.Ice.LocatorFinderPrx finder = com.zeroc.Ice.LocatorFinderPrx.checkedCast(
                communicator.stringToProxy("Ice/LocatorFinder:tcp -p 4061 -h 192.168.0.29")
            );

            communicator.setDefaultLocator(finder.getLocator());

            try {

                Log.d("Zeroc-Ice","3");
                hello = Server.HelloPrx.checkedCast(
                    communicator.stringToProxy("hello")
                );

            } catch (Exception e) {

                Log.d("Zeroc-Ice","3.0");
                com.zeroc.IceGrid.QueryPrx query = com.zeroc.IceGrid.QueryPrx.checkedCast(
                    communicator.stringToProxy("DemoIceGrid/Query")
                );

                Log.d("Zeroc-Ice","3.5");
                hello = Server.HelloPrx.checkedCast(
                    query.findObjectByType("::DemoIceGrid::Hello")
                );
            }

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
