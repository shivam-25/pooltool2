package com.example.android.pooltool2.Common;

import com.example.android.pooltool2.Remote.FCMClient;
import com.example.android.pooltool2.Remote.IFCMService;

public class Common {
    public static final String driver_tbl = "Drivers";
    public static final String user_rider_tbl = "Users";
    public static final String user_driver_tbl = "Users";
    public static final String pickup_request_tbl = "PickupRequest";
    public static final String token_tbl = "Tokens";

    public static final String baseURL = "https://maps.googleapis.com";
    public static final String fcmURL = "https://fcm.googleapis.com";
    public static IFCMService getFCMService() {
        return FCMClient.getClient(fcmURL).create(IFCMService.class);
    }
}
