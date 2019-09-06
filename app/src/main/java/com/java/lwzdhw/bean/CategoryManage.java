package com.java.lwzdhw.bean;

import android.util.Log;

import com.java.lwzdhw.MainActivity;
import com.java.lwzdhw.utils.StateSaver;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

public class CategoryManage {
    public static CategoryManage categoryManage;
    /**
     * 默认的用户选择频道列表
     * */
    public static List<Category> defaultUserChannels;
    /**
     * 默认的其他频道列表
     * */
    public static List<Category> defaultOtherChannels;

    final static public String[] items = {"军事","娱乐","教育","健康","文化","财经","体育","汽车","科技","社会"};

    static public Vector<String> getItemVector(){
        Vector<String> vector = new Vector<>();
        for (String item : items)
            vector.add(item);
        return vector;
    }

    /** 判断数据库中是否存在用户数据 */
    private boolean userExist = false;
    static {
        defaultUserChannels = new ArrayList<Category>();
        defaultOtherChannels = new ArrayList<Category>();
        defaultUserChannels.add(new Category(1, "军事", 1, 1));
        defaultUserChannels.add(new Category(2, "娱乐", 2, 1));
        defaultUserChannels.add(new Category(3, "教育", 3, 1));
        defaultUserChannels.add(new Category(4, "文化", 4, 1));
        defaultUserChannels.add(new Category(5, "健康", 5, 1));
        defaultUserChannels.add(new Category(6, "财经", 6, 1));
        defaultUserChannels.add(new Category(7, "体育", 7, 1));
        defaultOtherChannels.add(new Category(8, "汽车", 1, 0));
        defaultOtherChannels.add(new Category(9, "科技", 2, 0));
        defaultOtherChannels.add(new Category(10, "社会", 3, 0));
    }

    /**
     * 初始化频道管理类
     */
    public static CategoryManage getManage() {
        if (categoryManage == null)
            categoryManage = new CategoryManage();
        return categoryManage;
    }

    /**
     * 获取其他的频道
     * @return 数据库存在用户配置 ? 数据库内的用户选择频道 : 默认用户选择频道 ;
     */
    public List<Category> getUserChannel() {
        StateSaver saver = MainActivity.saver;
        if(!saver.load()) {
            Vector<String> has = new Vector<>();
            for (Category category: defaultUserChannels){
                has.add(category.name);
            }
            MainActivity.saver.updateSection(has);
            saver.save();
            saver.load();
            for(int i = 0 ; i < defaultUserChannels.size() ; ++i){
                saver.setRank(defaultUserChannels.get(i).name, defaultUserChannels.get(i).orderId);
            }
            saver.save();
            saver.load();
        }
        List<Category> temp = new ArrayList<>();
        int index = 1;
        for (String type : saver.getSections()) {
            temp.add(new Category(saver.getRank(type),type,saver.getRank(type),1));
            Log.d("CategoryManage", "getUserChannel: " + temp.get(index - 1));
            index ++;
        }

        Comparator<Category> cmp = new Comparator<Category>() {
            @Override
            public int compare(Category o1, Category o2) {
                return o1.id - o2.id;
            }
        };

        temp.sort(cmp);

        return temp;
    }

    /**
     * 获取其他的频道
     * @return 数据库存在用户配置 ? 数据库内的其它频道 : 默认其它频道 ;
     */
    public List<Category> getOtherChannel() {
        StateSaver saver = MainActivity.saver;
        if(!saver.load())
            return defaultOtherChannels;
        Vector<String> diff = getItemVector();
        diff.removeAll(saver.getSections());
        List<Category> temp = new ArrayList<>();
        int index = 10 - diff.size();
        for (String type : diff) {
            temp.add(new Category(index,type,saver.getRank(type),1));
            index ++;
        }
        return temp;
    }
}

