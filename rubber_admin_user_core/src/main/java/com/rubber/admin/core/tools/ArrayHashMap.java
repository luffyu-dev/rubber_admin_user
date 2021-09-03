package com.rubber.admin.core.tools;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author luffyu
 * Created on 2021/9/3
 */
public class ArrayHashMap <K, V> extends HashMap<K, ArrayList<V>> {



    /**
     * 当key是一样当时候，把v用数组串起来
     * @param k k值
     * @param v v值
     * @return 返回当前k下面的数组
     */
    public ArrayList<V>  putAndAdd(K k,V v){
        ArrayList<V> vs = get(k);
        if(vs == null){
            synchronized (this){
                if(vs == null){
                    vs = new ArrayList<>();
                    put(k,vs);
                }
            }
        }
        vs.add(v);
        return vs;
    }

}
