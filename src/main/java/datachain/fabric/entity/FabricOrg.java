package datachain.fabric.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author Leo Hope
 * @Email houmingefeng@datachain.cc
 * @Data 2018/5/7 下午2:23
 *
 * fabric 组织 CA证书
 */
public class FabricOrg {

    /** 名称 */
    @Getter
    private String name;

    /** 会员id */
    @Getter
    private String mspid;

    /** 域名名称 */
    @Getter
    @Setter
    private String domainName;

    /** 本地 ca */
    @Getter
    private String caLocation;
    /** 节点管理员 */
    @Getter
    private FabricUser peerAdmin ;

    /** 本地节点集合 */
    Map<String, String> peerLocations = new HashMap<>();
    /** 本地事件集合 */
    Map<String, String> eventHubLocations = new HashMap<>();

    public FabricOrg(FabricPeers peers ,  String cryptoConfigPath){
        this.name = peers.getOrgName() ;
        this.mspid = peers.getOrgMSPID() ;
        this.caLocation = peers.getCaLocation() ;//当前节点证书
        this.domainName = peers.getOrgDomainName(); // domainName=tk.anti-moth.com

        //获取所有的Peer节点
        for (FabricPeer peer : peers.get()){
            addPeerLocation(peer.getPeerName() , peer.getPeerLocation());
            addEventHubLocation(peer.getPeerEventHubName() , peer.getPeerEventHubLocation());
        }
        File skFile = Paths.get(cryptoConfigPath, "/peerOrganizations/", peers.getOrgDomainName(), String.format("/users/Admin@%s/msp/keystore", peers.getOrgDomainName())).toFile();
        File certificateFile = Paths.get(cryptoConfigPath, "/peerOrganizations/", peers.getOrgDomainName(),
                String.format("/users/Admin@%s/msp/signcerts/Admin@%s-cert.pem", peers.getOrgDomainName(), peers.getOrgDomainName())).toFile();
        /*File skFile = Paths.get(cryptoConfigPath , "peerOrganizations/org1.bctrustmachine.cn/users/Admin@org1.bctrustmachine.cn/msp/keystore").toFile() ;
        File certificateFile = Paths.get(cryptoConfigPath ,"peerOrganizations/org1.bctrustmachine.cn/tlsca/tlsca.org1.bctrustmachine.cn-cert.pem").toFile();*/
        this.peerAdmin = new FabricUser(peers.getOrgName() + "Admin",  peers.getOrgMSPID(), findFileSk(skFile), certificateFile); // 一个特殊的用户，可以创建通道，连接对等点，并安装链码
    }

    /**
     * 添加本地节点
     *
     * @param name
     *            节点key
     * @param location
     *            节点
     */
    public void addPeerLocation(String name, String location) {
        peerLocations.put(name, location);
    }

    /**
     * 添加本地事件
     *
     * @param name
     *            事件key
     * @param location
     *            事件
     */
    public void addEventHubLocation(String name, String location) {
        eventHubLocations.put(name, location);
    }

    /**
     * 从指定路径中获取后缀为 _sk 的文件，且该路径下有且仅有该文件
     *
     * @param directory
     *            指定路径
     * @return File
     */
    private File findFileSk(File directory) {
        File[] matches = directory.listFiles((dir, name) -> name.endsWith("_sk"));
        if (null == matches) {
            throw new RuntimeException(String.format("Matches returned null does %s directory exist?", directory.getAbsoluteFile().getName()));
        }
        if (matches.length != 1) {
            throw new RuntimeException(String.format("Expected in %s only 1 sk file but found %d", directory.getAbsoluteFile().getName(), matches.length));
        }
        return matches[0];
    }
}
