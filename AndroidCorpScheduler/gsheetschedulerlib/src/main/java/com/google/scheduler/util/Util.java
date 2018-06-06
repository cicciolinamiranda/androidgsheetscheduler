package com.google.scheduler.util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.scheduler.enums.NetworkTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.scheduler.constants.AppConstants.REQUEST_PERMISSIONS;

/**
 * Created by cicciolina on 6/6/18.
 */

public class Util {

    private static Util instance;

    private Util() {}

    public static Util getInstance() {

        if (instance == null) {
            instance = new Util();
        }
        return instance;
    }

    /**
     * Checks if GPS is turned on. If not prompts user to turn it on.
     *
     * @param context - either the Activity or the context passed by the receiver.
     */
    public boolean checkGPS(final Context context) {
        boolean enabled = false;

        if (context !=null) {
            LocationManager mlocManager = (LocationManager) context.getSystemService(
                    Context.LOCATION_SERVICE);
            enabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!enabled) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Location needed")
                        .setMessage("App needs your current location. Enable GPS?")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        });

                builder.create().show();
            }
        }

        return enabled;
    }

    public void requestPermission(Activity activity){
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_NETWORK_STATE
                        ,Manifest.permission.CHANGE_NETWORK_STATE
                        , Manifest.permission.ACCESS_NETWORK_STATE
                        , Manifest.permission.ACCESS_COARSE_LOCATION
                        , Manifest.permission.ACCESS_FINE_LOCATION
                        , Manifest.permission.GET_ACCOUNTS
/*                        , Manifest.permission.READ_PHONE_STATE
                        , Manifest.permission.READ_EXTERNAL_STORAGE
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE*/

                }, REQUEST_PERMISSIONS);

    }

    public void showSnackBarToast(Context context, String message){
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP){
            // Do something for lollipop and above versions
            Snackbar.make(((Activity)context).getWindow().getDecorView().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
        } else{
            // do something for phones running an SDK before lollipop
            Toast.makeText(context,message,Toast.LENGTH_LONG).show();
        }
    }

    public String fromColumnNumberToColumnLetter(int colNum) {
        String columnLetter= "F";

        List<String> columnDateHeaderLetters = new ArrayList<>(
                Arrays.asList("F", "G", "H","I", "J", "K","L","M","N","O","P", "Q","R","S","T", "U","V","W","X","Y","Z",
                        "AA","AB","AC","AD","AE","AF","AG", "AH","AI", "AJ", "AK","AL","AM","AN","AO","AP", "AQ","AR","AS","AT", "AU","AV","AW","AX","AY","AZ",
                        "BA","BB","BC","BD","BE","BF","BG", "BH","BI", "BJ", "BK","BL","BM","BN","BO","BP", "BQ","BR","BS","BT", "BU","BV","BW","BX","BY","BZ",
                        "CA","CB","CC","CD","CE","CF","CG", "CH","CI", "CJ", "CK","CL","CM","CN","CO","CP", "CQ","CR","CS","CT", "CU","CV","CW","CX","CY","CZ",
                        "DA","DB","DC","DD","DE","DF","DG", "DH","DI", "DJ", "DK","DL","DM","DN","DO","DP", "DQ","DR","DS","DT", "DU","DV","DW","DX","DY","DZ",
                        "EA","EB","EC","ED","EE","EF","EG", "EH","EI", "EJ", "EK","EL","EM","EN","EO","EP", "EQ","ER","ES","ET", "EU","EV","EW","EX","EY","EZ",
                        "FA","FB","FC","FD","FE","FF","FG", "FH","FI", "FJ", "FK","FL","FM","FN","FO","FP", "FQ","FR","FS","FT", "FU","FV","FW","FX","FY","FZ",
                        "GA","GB","GC","GD","GE","GF","GG", "GH","GI", "GJ", "GK","GL","GM","GN","GO","GP", "GQ","GR","GS","GT", "GU","GV","GW","GX","GY","GZ",
                        "HA","HB","HC","HD","HE","HF","HG", "HH","HI", "HJ", "HK","HL","HM","HN","HO","HP", "HQ","HR","HS","HT", "HU","HV","HW","HX","HY","HZ",
                        "IA","IB","IC","ID","IE","IF","IG", "IH","II", "IJ", "IK","IL","IM","IN","IO","IP", "IQ","IR","IS","IT", "IU","IV","IW","IX","IY","IZ",
                        "JA","JB","JC","JD","JE","JF","JG", "JH","JI", "JJ", "JK","JL","JM","JN","JO","JP", "JQ","JR","JS","JT", "JU","JV","JW","JX","JY","JZ",
                        "KA","KB","KC","KD","KE","KF","KG", "KH","KI", "KJ", "KK","KL","KM","KN","KO","KP", "KQ","KR","KS","KT", "KU","KV","KW","KX","KY","KZ",
                        "LA","LB","LC","LD","LE","LF","LG", "LH","LI", "LJ", "LK","LL","LM","LN","LO","LP", "LQ","LR","LS","LT", "LU","LV","LW","LX","LY","LZ",
                        "MA","MB","MC","MD","ME","MF","MG", "MH","MI", "MJ", "MK","ML","MM","MN","MO","MP", "MQ","MR","MS","MT", "MU","MV","MW","MX","MY","MZ",
                        "NA","NB","NC","ND","NE","NF","NG", "NH","NI", "NJ", "NK","NL","NM","NN","NO","NP", "NQ","NR","NS","NT", "NU","NV","AW","NX","NY","NZ",
                        "OA","OB","OC","OD","OE","OF","OG", "OH","OI", "OJ", "OK","OL","OM","ON","OO","OP", "OQ","OR","OS","OT", "OU","OV","AW","OX","OY","OZ",
                        "PA","PB","PC","PD","PE","PF","PG", "PH","PI", "PJ", "PK","PL","PM","PN","PO","PP", "PQ","PR","PS","PT", "PU","PV","AW","PX","PY","PZ",
                        "QA","QB","QC","QD","QE","QF","QG", "QH","QI", "QJ", "QK","QL","QM","QN","QO","QP", "QQ","QR","QS","QT", "QU","QV","AW","QX","QY","QZ",
                        "RA","RB","RC","RD","RE","RF","RG", "RH","RI", "RJ", "RK","RL","RM","RN","RO","RP", "RQ","RR","RS","RT", "RU","RV","AW","RX","RY","RZ",
                        "SA","SB","SC","SD","SE","SF","SG", "SH","SI", "SJ", "SK","SL","SM","SN","SO","SP", "SQ","SR","SS","ST", "SU","SV","AW","SX","SY","SZ",
                        "TA","TB","TC","TD","TE","TF","TG", "TH","TI", "TJ", "TK","TL","TM","TN","TO","TP", "TQ","TR","TS","TT", "TU","TV","AW","TX","TY","TZ",
                        "UA","UB","UC","UD","UE","UF","UG", "UH","UI", "UJ", "UK","UL","UM","UN","UO","UP", "UQ","UR","US","UT", "UU","UV","AW","UX","UY","UZ",
                        "VA","VB","VC","VD","VE","VF","VG", "VH","VI", "VJ", "VK","VL","VM","VN","VO","VP", "VQ","VR","VS","VT", "VU","VV","AW","VX","VY","VZ",
                        "WA","WB","WC","WD","WE","WF","WG", "WH","WI", "WJ", "WK","WL","WM","WN","QO","WP", "WQ","WR","WS","WT", "WU","WV","AW","WX","WY","WZ",
                        "XA","XB","XC","XD","XE","XF","XG", "XH","XI", "XJ", "XK","XL","XM","XN","XO","XP", "XQ","XR","XS","XT", "XU","XV","AW","XX","XY","XZ",
                        "YA","YB","YC","YD","YE","YF","YG", "YH","YI", "YJ", "YK","YL","YM","YN","YO","YP", "YQ","YR","YS","YT", "YU","YV","AW","YX","YY","YZ",
                        "ZA","ZB","ZC","ZD","ZE","ZF","ZG", "ZH","ZI", "ZJ", "ZK","ZL","ZM","ZN","ZO","ZP", "ZQ","ZR","ZS","ZT", "ZU","ZV","AW","ZX","ZY","ZZ"));

        Log.d(Util.class.getName(), "columnDateHeaderLetters.size(): "+columnDateHeaderLetters.size());
        if(colNum <= columnDateHeaderLetters.size()) {
            columnLetter = columnDateHeaderLetters.get(colNum);
        }
        return columnLetter;
    }


    public static NetworkTypes getNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        if (info == null || !info.isConnected())
            return NetworkTypes.NO_CONNECTION;

        if (info.getType() == ConnectivityManager.TYPE_WIFI)
            return NetworkTypes.WIFI;

        if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
            int networkType = info.getSubtype();
            switch (networkType) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN: // all above for 2G

                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP: // all above for 3G

                case TelephonyManager.NETWORK_TYPE_LTE:   // 4G
                    return NetworkTypes.MOBILE_NETWORK;
                default:
                    return NetworkTypes.UNKNOWN;
            }
        }
        return NetworkTypes.UNKNOWN;

  /*  boolean wifi = isWifiConnected(context);
    boolean mobile = isMobileConnected(context);

    if(wifi) return NetWorkTypes.WIFI;
    if(mobile) return  NetWorkTypes.MOBILE_NETWORK;
    return NetWorkTypes.UNKNOWN;*/
    }
}
