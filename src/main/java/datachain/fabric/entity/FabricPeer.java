package datachain.fabric.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author 侯明峰
 * @Email houmingefeng@datachain.cc
 * @Data 2018/1/26 下午4:33
 *
 * 节点服务器对象
 */

@Getter
@Setter
public class FabricPeer {

    /** 当前指定的组织节点域名 */
    private String peerName; // peer0.org1.example.com
    /** 当前指定的组织节点事件域名 */
    private String peerEventHubName; // peer0.org1.example.com
    /** 当前指定的组织节点访问地址 */
    private String peerLocation; // grpc://110.131.116.21:7051
    /** 当前指定的组织节点事件监听访问地址 */
    private String peerEventHubLocation; // grpc://110.131.116.21:7053
    /** 当前指定的组织节点ca访问地址 */
    //private String caLocation; // http://110.131.116.21:7054
    /** 当前peer是否增加Event事件处理 */
    private boolean addEventHub = false;

    public FabricPeer(String peerName, String peerEventHubName, String peerLocation, String peerEventHubLocation) {
        this.peerName = peerName;
        this.peerEventHubName = peerEventHubName;
        this.peerLocation = peerLocation;
        this.peerEventHubLocation = peerEventHubLocation;
        //this.caLocation = caLocation;
    }

}
