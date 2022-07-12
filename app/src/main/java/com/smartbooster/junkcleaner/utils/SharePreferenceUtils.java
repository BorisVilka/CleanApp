package com.smartbooster.junkcleaner.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SharePreferenceUtils {
    private static SharePreferenceUtils instance;

    private Context mContext;
    private SharedPreferences.Editor editor;
    private SharedPreferences pre;

    private SharePreferenceUtils(Context context) {
        this.mContext = context;
        this.pre = context.getSharedPreferences(SharePreferenceConstant.KEY_DATA, Context.MODE_MULTI_PROCESS);
        this.editor = this.pre.edit();
    }

    public static SharePreferenceUtils getInstance(Context context) {
        if (instance == null) {
            instance = new SharePreferenceUtils(context);
        }
        return instance;
    }

//   // public String getLanguage() {
//        return pre.getString(SharePreferenceConstant.LANGUAGE, "en");
//    }

    public void saveLanguageChange(boolean flag) {
        editor.putBoolean("LanguageChange", flag );
        editor.commit();
    }

    public Boolean getLanguageChange() {
        return pre.getBoolean("LanguageChange", false);
    }

//    public void saveLanguage(String language) {
//        editor.putString(SharePreferenceConstant.LANGUAGE, language);
//        editor.commit();
//    }

    public boolean getFirstRun() {
        boolean isFirst = pre.getBoolean("first_run_app", true);
        if (isFirst) {
            editor.putBoolean("first_run_app", false);
            editor.commit();
        }
        return isFirst;
    }

//    public ArrayList<ApplicationInfo> getAppList() {
//        ArrayList<ApplicationInfo> arrayList = new ArrayList();
//        Type type = new TypeToken<ArrayList<ApplicationInfo>>() {
//        }.getType();
//        String string = this.pre.getString(SharePreferenceConstant.KEY_APPS_LIST, null);
//        return string != null ? (ArrayList) new Gson().fromJson(string, type) : arrayList;
//    }




    public long getTimeIn(){
        return this.pre.getLong("TimeIn",0);
    }
    public void setTimeIn(long i) {
        editor.putLong("TimeIn", i);
        editor.commit();
    }

    public long getTimeOut(){
        return this.pre.getLong("TimeOut",0);
    }
    public void setTimeOut(long i) {
        editor.putLong("TimeOut", i);
        editor.commit();
    }
    public long getTime(){
        return this.pre.getLong("Time",0);
    }
    public void setTime(long i) {
        editor.putLong("Time", i);
        editor.commit();
    }


    public boolean getEnable(){
        return this.pre.getBoolean("Enable", false);
    }



    public long getChargeNormal(){
        return this.pre.getLong("ChargeNormal",0);
    }
    public void setChargeNormal(long i) {
        editor.putLong("ChargeNormal", i);
        editor.commit();
    }


    public long getChargeHealthy(){
        return this.pre.getLong("ChargeHealthy",0);
    }
    public void setChargeHealthy(long i) {
        editor.putLong("ChargeHealthy", i);
        editor.commit();
    }

    public long getChargeOver(){
        return this.pre.getLong("ChargeOver",0);
    }
    public void setChargeOver(long i) {
        editor.putLong("ChargeOver", i);
        editor.commit();
    }

    public String getChargeFull(){
        return this.pre.getString("TimeFull",null);
    }
    public void setChargeFull(String i) {
        editor.putString("TimeFull", i);
        editor.commit();
    }
    public String getChargeType(){
        return this.pre.getString("ChargeType",null);
    }
    public void setChargeType(String i) {
        editor.putString("ChargeType", i);
        editor.commit();
    }

    public long getLevelIn(){
        return this.pre.getLong("LevelIn",0);
    }
    public void setLevelIn(long i) {
        editor.putLong("LevelIn", i);
        editor.commit();
    }
    public long getTimeCharge(){
        return this.pre.getLong("TimeCharge",0);
    }
    public void setTimeCharge(long i) {
        editor.putLong("TimeCharge", i);
        editor.commit();
    }
    public long getChargeQuantity(){
        return this.pre.getLong("ChargeQuantity",0);
    }
    public void setChargeQuantity(long i) {
        editor.putLong("ChargeQuantity", i);
        editor.commit();
    }





    public boolean getKillApp(){
        return this.pre.getBoolean("KillApp",true);
    }
    public void setKillApp(boolean i) {
        editor.putBoolean("KillApp", i);
        editor.commit();
    }

    public boolean getWifiStatus(){
        return this.pre.getBoolean("WifiStatus",true);
    }
    public void setWifiStatus(boolean i) {
        editor.putBoolean("WifiStatus", i);
        editor.commit();
    }
    public boolean getAutoBrightness(){
        return this.pre.getBoolean("AutoBrightness",true);
    }
    public void setAutoBrightness(boolean i) {
        editor.putBoolean("AutoBrightness", i);
        editor.commit();
    }
    public boolean getBluetoothStatus(){
        return this.pre.getBoolean("BluetoothStatus",true);
    }
    public void setBluetoothStatus(boolean i) {
        editor.putBoolean("BluetoothStatus", i);
        editor.commit();
    }
    public boolean getAutoSync(){
        return this.pre.getBoolean("AutoSync",true);
    }
    public void setAutoSyncs(boolean i) {
        editor.putBoolean("AutoSync", i);
        editor.commit();
    }

    public boolean getAutoRunSaverMode(){
        return this.pre.getBoolean("AutoRunSaverMode",false);
    }
    public void setAutoRunSaverMode(boolean i) {
        editor.putBoolean("AutoRunSaverMode", i);
        editor.commit();
    }
    public boolean getAdd(){
        return this.pre.getBoolean("AddBatteryPlan",true);
    }
    public void setAdd(boolean i) {
        editor.putBoolean("AddBatteryPlan", i);
        editor.commit();
    }
    public int getPostion(){
        return this.pre.getInt("Postion",2);
    }
    public void setPostion(int i) {
        editor.putInt("Postion", i);
        editor.commit();
    }
    public int getTypeMode(){
        return this.pre.getInt("TypeMode",2);
    }
    public void setTypeMode(int i) {
        editor.putInt("TypeMode", i);
        editor.commit();
    }

    public boolean getLevelCheck(){
        return this.pre.getBoolean("LevelCheck",true);
    }
    public void setLevelCheck(boolean i) {
        editor.putBoolean("LevelCheck", i);
        editor.commit();
    }



    public int getTimeOn(){
        return this.pre.getInt("TimeOn",800);
    }
    public void setTimeOn(int i) {
        editor.putInt("TimeOn", i);
        editor.commit();
    }
    public int getTimeOff(){
        return this.pre.getInt("TimeOff",2300);
    }
    public void setTimeOff(int i) {
        editor.putInt("TimeOff", i);
        editor.commit();
    }

    public boolean getSmartMode(){
        return this.pre.getBoolean("SmartMode",false);
    }
    public void setSmartMode(boolean i) {
        editor.putBoolean("SmartMode", i);
        editor.commit();
    }


    public boolean getFsWifi(){
        return this.pre.getBoolean("FsWifi",true);
    }
    public void setFsWifi(boolean i) {
        editor.putBoolean("FsWifi", i);
        editor.commit();
    }
    public boolean getFsBluetooth(){
        return this.pre.getBoolean("FsBluetooth",false);
    }
    public void setFsBluetooth(boolean i) {
        editor.putBoolean("FsBluetooth", i);
        editor.commit();
    }
    public boolean getFsAutoSync(){
        return this.pre.getBoolean("FsAutoSync",false);
    }
    public void setFsAutoSync(boolean i) {
        editor.putBoolean("FsAutoSync", i);
        editor.commit();
    }
    public boolean getFsAutoRun(){
        return this.pre.getBoolean("FsAutoRun",false
        );
    }
    public void setFsAutoRun(boolean i) {
        editor.putBoolean("FsAutoRun", i);
        editor.commit();
    }
    public boolean getFsAutoBrightness(){
        return this.pre.getBoolean("FsAutoBrightness",true);
    }
    public void setFsAutoBrightness(boolean i) {
        editor.putBoolean("FsAutoBrightness", i);
        editor.commit();
    }

    public int getBatterySaveModeIndex(){
        return this.pre.getInt("BatterySaveModeIndex",0);
    }
    public void setBatterySaveModeIndex(int i) {
        editor.putInt("BatterySaveModeIndex", i);
        editor.commit();
    }

    public int getSaveLevel(){
        return this.pre.getInt("SaveLevel",30);
    }
    public void setSaveLevel(int i) {
        editor.putInt("SaveLevel", i);
        editor.commit();
    }
    public int getDndStart(){
        return this.pre.getInt("DndStart",2200);
    }
    public void setDndStart(int i) {
        editor.putInt("DndStart", i);
        editor.commit();
    }
    public int getDndStop(){
        return this.pre.getInt("DndStop",800);
    }
    public void setDndStop(int i) {
        editor.putInt("DndStop", i);
        editor.commit();
    }

    public boolean getDnd(){
        return this.pre.getBoolean("Dnd",true);
    }
    public void setDnd(boolean i) {
        editor.putBoolean("Dnd", i);
        editor.commit();
    }
    public boolean getChargeFullReminder(){
        return this.pre.getBoolean("ChargeFullReminder",true);
    }
    public void setChargeFullReminder(boolean i) {
        editor.putBoolean("ChargeFullReminder", i);
        editor.commit();
    }

    public long getChargeFullReminderTime(){
        return this.pre.getLong("ChargeFullReminderTime",0);
    }
    public void setChargeFullReminderTime(long i) {
        editor.putLong("ChargeFullReminderTime", i);
        editor.commit();
    }

    public boolean getLowBatteryReminder(){
        return this.pre.getBoolean("LowBatteryReminder",true);
    }
    public void setLowBatteryReminder(boolean i) {
        editor.putBoolean("LowBatteryReminder", i);
        editor.commit();
    }
    public boolean getTempFormat(){
        return this.pre.getBoolean("TempFormat",false);
    }
    public void setTempFormat(boolean i) {
        editor.putBoolean("TempFormat", i);
        editor.commit();
    }
    public boolean getCoolDownReminder(){
        return this.pre.getBoolean("CoolDownReminder",true);
    }
    public void setCoolDownReminder(boolean i) {
        editor.putBoolean("CoolDownReminder", i);
        editor.commit();
    }

    public boolean getCoolNotification(){
        return this.pre.getBoolean("CoolNotification",false);
    }
    public void setCoolNotification(boolean i) {
        editor.putBoolean("CoolNotification", i);
        editor.commit();
    }

    public boolean getBoostReminder(){
        return this.pre.getBoolean("BoostReminder",true);
    }
    public void setBoostRemindert(boolean i) {
        editor.putBoolean("BoostReminder", i);
        editor.commit();
    }

    public long getTotalJunk(){
        return this.pre.getLong("TotalJunk",0);
    }
    public void setTotalJunk(long i) {
        editor.putLong("TotalJunk", i);
        editor.commit();
    }


    public long getOptimizeTime(){
        return this.pre.getLong("OptimizeTime",0);
    }
    public void setOptimizeTime(long i) {
        editor.putLong("OptimizeTime", i);
        editor.commit();
    }
    public long getCoolerTime(){
        return this.pre.getLong("CoolerTime",0);
    }
    public void setCoolerTime(long i) {
        editor.putLong("CoolerTime", i);
        editor.commit();
    }
    public long getBoostTime(){
        return this.pre.getLong("BoostTime",0);
    }
    public void setBoostTime(long i) {
        editor.putLong("BoostTime", i);
        editor.commit();
    }
    public long getCleanTime(){
        return this.pre.getLong("CleanTime",0);
    }
    public void setCleanTime(long i) {
        editor.putLong("CleanTime", i);
        editor.commit();
    }

    public boolean getChargeStatus(){
        return this.pre.getBoolean("ChargeStatus",false);
    }
    public void setChargeStatus(boolean i) {
        editor.putBoolean("ChargeStatus", i);
        editor.commit();
    }

    public long getOptimizeTimeMain(){
        return this.pre.getLong("OptimizeTimeMain",0);
    }
    public void setOptimizeTimeMain(long i) {
        editor.putLong("OptimizeTimeMain", i);
        editor.commit();
    }
    public long getCoolerTimeMain(){
        return this.pre.getLong("CoolerTimeMain",0);
    }
    public void setCoolerTimeMain(long i) {
        editor.putLong("CoolerTimeMain", i);
        editor.commit();
    }
    public long getBoostTimeMain(){
        return this.pre.getLong("BoostTimeMain",0);
    }
    public void setBoostTimeMain(long i) {
        editor.putLong("BoostTimeMain", i);
        editor.commit();
    }



    public boolean getFlagAds(){
        return this.pre.getBoolean("Ads",false);
    }
    public void setFlagAds(boolean i) {
        editor.putBoolean("Ads", i);
        editor.commit();
    }
    public boolean getNotification(){
        return this.pre.getBoolean("notification_enable",true);
    }
    public void setNotification(boolean i) {
        editor.putBoolean("notification_enable", i);
        editor.commit();
    }
    public long getHideChargeView(){
        return this.pre.getLong("HideChargeView",0);
    }
    public void setHideChargeView(long i) {
        editor.putLong("HideChargeView", i);
        editor.commit();
    }
    public boolean getStatusPer(){
        return this.pre.getBoolean("StatusPer",true);
    }
    public void setStatusPer(boolean i) {
        editor.putBoolean("StatusPer", i);
        editor.commit();
    }
    public int getLevelScreenOn(){
        return this.pre.getInt("LevelScreenOn",0);
    }
    public void setLevelScreenOn(int i) {
        editor.putInt("LevelScreenOn", i);
        editor.commit();
    }
    public boolean getStatusExit(){
        return this.pre.getBoolean("StatusExit",false);
    }
    public void setStatusExit(boolean i) {
        editor.putBoolean("StatusExit", i);
        editor.commit();
    }

}
