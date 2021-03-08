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
 * Fabric创建的peer信息，包含有cli、org、ca、couchdb等节点服务器关联启动服务信息集合
 */
public class FabricPeers {

    /** 当前指定的组织名称 */
    @Setter
    @Getter
    private String orgName; // Org1
    /** 当前指定的组织名称 */
    @Setter
    @Getter
    private String orgMSPID; // Org1MSP

    /** 当前指定的组织节点ca访问地址*/
    @Setter
    @Getter
    private String caLocation ;
    /** 当前指定的组织所在根域名 */
    @Setter
    @Getter
    private String orgDomainName; //org1.example.com
    /** orderer 排序服务器集合 */
    private List<FabricPeer> peers;

    public FabricPeers(){
        this.peers = new ArrayList<>() ;
    }

    /** 新增排序服务器 */
    public void addPeer(String peerName, String peerEventHubName, String peerLocation, String peerEventHubLocation) {
        peers.add(new FabricPeer(peerName, peerEventHubName, peerLocation, peerEventHubLocation));
    }

    /** 获取排序服务器集合 */
    public List<FabricPeer> get() {
        return peers;
    }

}
