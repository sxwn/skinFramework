package com.xiaowei.skin;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class SkinFactory implements LayoutInflater.Factory2 {
    //所有收集起来的需要换肤的控件的容器
    private List<SkinView> viewList = new ArrayList<>();
    //所有原生控件
    private static final String[] prxfixList = {
        "android.widget.",
            "android.view.",
            "android.webkit."
    };

    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        //监听xml的生成过程 自己去创建这些控件
        View view = null;
        //区分这个控件是不是自定义控件
        if (name.contains(".")){
            view = onCreateView(name,context,attrs);
        }else{
            for (String s : prxfixList) {
                view = onCreateView(s+name,context,attrs);
                if (view!=null){
                    break;
                }
            }
        }
        //收集所有需要换肤的控件
        if (view!=null){
            parseView(view,name,attrs);
        }
        Log.e("weip","===================="+name);
        return view;
    }
    public void apply(){
        for (SkinView skinView : viewList) {
            skinView.apply();
        }
    }

    /**
     * 如果控件已经被实例化 就去判断这个控件是否满足我们换肤的要求  然后收集起来
     * @param view
     * @param name
     * @param attrs
     */
    private void parseView(View view, String name, AttributeSet attrs) {
        List<SkinItem> itemList = new ArrayList<>();
        for (int i = 0 ;i<attrs.getAttributeCount();i++){
            //属性的名字
            String attributeName = attrs.getAttributeName(i);
            //属性资源id backgound textcolor color
            String attributeValue = attrs.getAttributeValue(i);
            if (attributeName.contains("background") || attributeName.contains("textColor")
                    || attributeName.contains("src") || attributeName.contains("color")){
                //获取资源id
                int resId = Integer.parseInt(attributeValue.substring(1));
                //获取到属性值的类型
                String typeName = view.getResources().getResourceTypeName(resId);
                //获取到属性值的名字
                String entryName = view.getResources().getResourceEntryName(resId);
                SkinItem skinItem = new SkinItem(attributeName,resId,entryName,typeName);
                itemList.add(skinItem);
            }
        }
        //如果长度大于0 就说明当前这个控件需要换肤
        if (itemList.size()>0){
            SkinView skinView = new SkinView(view,itemList);
            viewList.add(skinView);
            skinView.apply();
        }
    }

    /**
     * 将控件进行实例化(创建)
     * @param name 控件名
     * @param context
     * @param attrs
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        View view = null;
        try {
            //这个name的class类对象
            Class aClass = context.getClassLoader().loadClass(name);
            Constructor<? extends View> constructor = aClass.getConstructor(new Class[]{Context.class, AttributeSet.class});
            view = constructor.newInstance(context,attrs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    /**
     * 封装每一条属性的对象
     */
    class SkinItem{
        //属性名字
        String name;
        //属性id
        int resId;
        //属性值的名字
        String entryName;
        //属性值的类型 color bitmap
        String typeName;

        public SkinItem(String name, int resId, String entryName, String typeName) {
            this.name = name;
            this.resId = resId;
            this.entryName = entryName;
            this.typeName = typeName;
        }

        public String getName() {
            return name;
        }

        public int getResId() {
            return resId;
        }

        public String getEntryName() {
            return entryName;
        }

        public String getTypeName() {
            return typeName;
        }
    }

    class SkinView{
        View view;
        List<SkinItem> list;

        public SkinView(View view, List<SkinItem> list) {
            this.view = view;
            this.list = list;
        }

        public View getView() {
            return view;
        }

        public List<SkinItem> getList() {
            return list;
        }

        /**
         * 给单个控件进行换肤
         */
        public void apply(){
            for (SkinItem skinItem : list) {
                if (skinItem.getName().equals("background")){
                   if (skinItem.getTypeName().equals("color")){
                       //从皮肤插件apk资源对象中获取到相匹配的id 进行设值
                       view.setBackgroundColor(SkinManager.getInstance().getColor(skinItem.getResId()));
                   }else if(skinItem.getTypeName().equals("drawable") || skinItem.getTypeName().equals("mipmap")){
                        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN){
                            view.setBackground(SkinManager.getInstance().getDrawable(skinItem.getResId()));
                        }else{
                            view.setBackgroundDrawable(SkinManager.getInstance().getDrawable(skinItem.getResId()));
                        }
                   }
                }else if(skinItem.getName().equals("textColor")){
                    if (view instanceof TextView){
                        ((TextView)view).setTextColor(SkinManager.getInstance().getColor(skinItem.getResId()));
                    }else if(view instanceof Button){
                        ((Button)view).setTextColor(SkinManager.getInstance().getColor(skinItem.getResId()));
                    }
                }
            }
        }
    }
}
