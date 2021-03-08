package datachain.fabric.entity;


import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author 侯明峰
 * @Email houmingefeng@datachain.cc
 * @Data 2018/1/26 下午4:14
 *
 * Fabric创建的orderer信息，涵盖单机和集群两种方案
 */
public class FabricOrderers {

    /** orderer 排序服务器所在根域名 */
    @Getter
    @Setter
    private String ordererDomainName; // anti-moth.com
/*
    *//** orderer 服务器 host名称 *//*
    @Getter
    @Setter
    private String ordererHostName ;// hostName + DomainName = hostnameOverride*/

    /** orderer 排序服务器集合 */
    private List<FabricOrderer> orderers;

    public FabricOrderers() {
        orderers = new ArrayList<>();
    }

    /** 新增排序服务器 */
    public void addOrderer(String name, String location) {
        this.orderers.add(new FabricOrderer(name, location));
    }

    /** 获取排序服务器集合 */
    public List<FabricOrderer> get() {
        return this.orderers;
    }
}
