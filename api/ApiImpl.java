package rpc.api;

import java.util.ArrayList;
import java.util.List;

public class ApiImpl implements Api {

    @Override
    public List<Integer> list() {
        List<Integer> list = new ArrayList<>();
        list.add(3);
        list.add(2);
        list.add(1);
        return list;
    }

    @Override
    public String get(String name) {
        return "hello, " + name;
    }

}
