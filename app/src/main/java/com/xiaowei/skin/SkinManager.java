package com.xiaowei.skin;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import java.lang.reflect.Method;

/**
 * 皮肤资源管理类，用来管理皮肤插件apk的资源对象
 */
public class SkinManager {
    private static SkinManager skinManager = new SkinManager();
    //皮肤插件apk的资源对象
    private Resources resource;
    //上下文
    private Context context;
    //皮肤插件apk的包名
    private String skinPackageName;

    private SkinManager(){}

    public static SkinManager getInstance(){
        return skinManager;
    }

    public void setContext(Context context){
        this.context = context;
    }

    /**
     * 根据皮肤apk 的路径去获取到它的资源对象  28
     * @param path
     */
    public void loadSkinApk(String path){
        //获取包管理器
        PackageManager packageManager = context.getPackageManager();
        //获取到皮肤apk的包信息类
        PackageInfo packageArchiveInfo = packageManager.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        //获取到皮肤apk的包名
        skinPackageName = packageArchiveInfo.packageName;
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager,path);
            resource = new Resources(assetManager,context.getResources().getDisplayMetrics(),context.getResources().getConfiguration());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据传进来的id  去匹配皮肤插件apk的资源对象  如果有类型和名字一样的就返回
     * @param id
     * @return
     */
    public int getColor(int id){
        if (resource==null)
            return id;
        //获取到属性值的名字
        String entryName = context.getResources().getResourceEntryName(id);
        //获取到属性值的类型
        String typeName = context.getResources().getResourceTypeName(id);
        //名字和类型匹配的资源对象中的ID
        int identifier = resource.getIdentifier(entryName, typeName, skinPackageName);
        if (identifier==0){
            return id;
        }
        return resource.getColor(identifier);
    }

    /**
     * 从外置的apk中拿到drawabled的资源id
     * @param id
     * @return
     */
    public Drawable getDrawable(int id){
        if (resource==null)
            return ContextCompat.getDrawable(context,id);
        //获取到资源id的类型
        String resourseTypeName = context.getResources().getResourceTypeName(id);
        //获取到资源id的名字
        String resourseEntryName = context.getResources().getResourceEntryName(id);
        //colorAccent这个资源在外置apk中的id
        int identifier = resource.getIdentifier(resourseEntryName, resourseTypeName, skinPackageName);
        if (identifier==0){
            return ContextCompat.getDrawable(context,id);
        }
        return resource.getDrawable(identifier);
    }
}
