

package com.microsoft.cfd.limelight.DroneInterface.flight.service.states;

import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.microsoft.cfd.limelight.DroneInterface.flight.drone.DroneProxy;
import com.microsoft.cfd.limelight.DroneInterface.flight.drone.DroneProxyConnectedReceiverDelegate;
import com.microsoft.cfd.limelight.DroneInterface.flight.drone.DroneProxyConnectionFailedReceiver;
import com.microsoft.cfd.limelight.DroneInterface.flight.drone.DroneProxyConnectionFailedReceiverDelegate;
import com.microsoft.cfd.limelight.DroneInterface.flight.drone.DroneProxyDisconnectedReceiver;
import com.microsoft.cfd.limelight.DroneInterface.flight.drone.DroneProxyDisconnectedReceiverDelegate;
import com.microsoft.cfd.limelight.DroneInterface.flight.service.DroneControlService;
import com.microsoft.cfd.limelight.DroneInterface.flight.service.ServiceStateBase;
import com.microsoft.cfd.limelight.DroneInterface.flight.service.commands.DisconnectCommand;
import com.microsoft.cfd.limelight.DroneInterface.flight.service.commands.DroneServiceCommand;
import com.microsoft.cfd.limelight.DroneInterface.flight.service.commands.ResumeCommand;

public class PausedServiceState
        extends ServiceStateBase
        implements DroneProxyConnectedReceiverDelegate,
        DroneProxyConnectionFailedReceiverDelegate,
        DroneProxyDisconnectedReceiverDelegate
{
    private Object lock = new Object();
    boolean disconnected;

    private LocalBroadcastManager bm;

    private DroneProxyDisconnectedReceiver disconnectedReceiver;
    private DroneProxyConnectionFailedReceiver connFailedReceiver;


    public PausedServiceState(DroneControlService context)
    {
        super(context);

        bm = LocalBroadcastManager.getInstance(context.getApplicationContext());

        disconnectedReceiver = new DroneProxyDisconnectedReceiver(this);
        connFailedReceiver = new DroneProxyConnectionFailedReceiver(this);
    }


    @Override
    protected void onPrepare()
    {
        bm.registerReceiver(disconnectedReceiver, new IntentFilter(DroneProxy.DRONE_PROXY_DISCONNECTED_ACTION));
        bm.registerReceiver(connFailedReceiver, new IntentFilter(DroneProxy.DRONE_PROXY_CONNECTION_FAILED_ACTION));
    }


    @Override
    protected void onFinalize()
    {
        bm.unregisterReceiver(disconnectedReceiver);
        bm.unregisterReceiver(connFailedReceiver);
    }


    @Override
    public void connect()
    {
        Log.w(getStateName(), "Can't connect. Already connected. Skipped.");
    }


    @Override
    public void disconnect()
    {
        Log.d(getStateName(), "Disconnect");
        disconnected = false;
        startCommand(new DisconnectCommand(context));

        // synchronized (lock) {
        // try {
        // if (!disconnected) {
        // lock.wait(5000);
        // }
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }
        // }
    }


    @Override
    public void resume()
    {
        startCommand(new ResumeCommand(context));
    }


    @Override
    public void pause()
    {
        Log.w(getStateName(), "Can't pause. Already paused. Skipped.");
    }


    public void onToolConnected()
    {
        Log.w(getStateName(), "onToolConnected() Should not happen here");
    }


    public void onToolConnectionFailed(int reason)
    {
        onToolDisconnected();
    }


    public void onToolDisconnected()
    {
        disconnected = true;

        synchronized (lock) {
            lock.notify();
        }

        setState(new DisconnectedServiceState(context));

        onDisconnected();
    }


    @Override
    public void onCommandFinished(DroneServiceCommand command)
    {
        if (command instanceof ResumeCommand) {
            setState(new ConnectedServiceState(context));
            onResumed();
        }
    }
}
