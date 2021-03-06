package com.microsoft.cfd.limelight.Controllers;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;

import com.microsoft.cfd.limelight.RelaySocketHandlers.RelayClient;
import com.microsoft.cfd.limelight.RelaySocketHandlers.RelayServer;


public class ConnectionController  implements ConnectionInfoListener {

    private WifiP2pInfo info;
    ProgressDialog progressDialog = null;
    public Context context;

    public ConnectionController(Context c) {
        this.context = c;
    }

    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        this.info = info;
        // After the group negotiation, we assign the group owner as the file
        // server. The file server is single threaded, single connection server
        // socket.
        if (info.groupFormed && info.isGroupOwner) {
            new RelayServer(context).execute();
        } else if (info.groupFormed) {
            // The other device acts as the client. In this case, we enable the
            // get file button.
            new RelayClient(
                    info.groupOwnerAddress.getHostAddress(),
                    8988,
                    context
            ).execute();
        }

    }

}
