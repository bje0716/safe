package kr.ac.dongeui.pangpang.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationUtil {

    public static String getAddressInString(Context context, double lat, double lng) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses != null && addresses.size() > 0) {
                return convertToString(addresses.get(0));
            } else {
                return "";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String convertToString(Address obj) {
        String add = "";
        if (obj == null)
            return "";
        add = obj.getAddressLine(0);
        return add;
    }

    public static String replaceAddressString(String str) {
        if (str.contains("대한민국")) {
            return str.replace("대한민국", "").trim();
        }
        return null;
    }
}
