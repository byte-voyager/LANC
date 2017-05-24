package cc.alonebo.lanc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cc.alonebo.lanc.model.bean.ContactBean;

/**
 * Created by alonebo on 17-5-16.
 */

public class MyContactList {
    public int currentPos = 0;
    private ArrayList<ContactBean> list;
    public HashMap<String,Integer> map;

    public MyContactList() {
        list = new ArrayList<ContactBean>();
        map = new HashMap<String, Integer>();
        currentPos = 0;
    }

    public void add(ContactBean contact) {
        list.add(contact);

        map.put(contact.getDeviceIdent(),currentPos);
        currentPos++;
    }

    public ContactBean getContact(int pos) {
        return list.get(pos);
    }

    public int getIdentPos(String ident) {
        Integer integer = map.get(ident);
        if (integer == null) {
            return -1;
        }
        return integer;
    }

    public void removeContact(int pos) {
        String ident = list.get(pos).getDeviceIdent();
        list.remove(pos);
        currentPos--;
        map.remove(ident);//删除后要重新赋值
        freshMap();
    }

    public void cleanAll() {
        map.clear();
        currentPos = 0;
        list.clear();
    }

    public void addAll(List<ContactBean> addList) {
        list.addAll(addList);
        freshMap();
    }

    public void freshMap() {
        map.clear();
        for (int i = 0;i<list.size();i++){
            map.put(list.get(i).getDeviceIdent(),i);
        }
    }

    public void add(int pos,ContactBean contactBean) {
        list.add(pos,contactBean);
        freshMap();
    }

    public void update(int pos,ContactBean contactBean) {
        list.remove(pos);
        list.add(pos,contactBean);
        freshMap();
    }

    public int size() {
        return list.size();
    }

    public ContactBean get(int pos) {
        return list.get(pos);
    }

    public boolean isExist(String deviceIdent) {
        return map.containsKey(deviceIdent);
    }

    public void del(int position) {
        list.remove(position);
        freshMap();
    }
}
