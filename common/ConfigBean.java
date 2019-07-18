package rpc.common;

import lombok.Getter;

public class ConfigBean {

    /**
     * 扫描包路径
     */
    @Getter
    private String packageName;

    private ConfigBean() {
    }

    private static class SingletonHolder {
        private static ConfigBean configBean = new ConfigBean();
    }

    public static ConfigBean getInstance() {
        return SingletonHolder.configBean;
    }

    public ConfigBean setPackageName(String packageName) {
        this.packageName = packageName;
        return this;
    }

}
