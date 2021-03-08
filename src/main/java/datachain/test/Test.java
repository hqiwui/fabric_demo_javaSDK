package datachain.test;

import datachain.fabric.ChainCodeManager;
import datachain.fabric.entity.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Leo Hope
 * @Email houmingefeng@datachain.cc
 * @Data 2018/5/7 下午8:42
 */
public class Test {

    public static void main(String[] args) {
        FabricConfig config = new FabricConfig() ;
        config.setChaincode(getChainCode("mychannel" ,"medicalChain" ,"medicalChain" , "1.0"));
        config.setFabricOrderersList(getFabricOrderersList());
        config.setFabricPeers(getFabricPeers());
        try{
            ChainCodeManager manager = new ChainCodeManager(config) ;
            ArrayList<String> list = new ArrayList();
            list.add("15921002625") ;
            FabricCode code = manager.query("ReadUserInfoByPhoneNumber" , list) ;
            System.out.println(code.toString());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static FabricChainCode getChainCode(String channelName, String chaincodeName, String chaincodePath, String chaincodeVersion) {
        FabricChainCode chainCode = new FabricChainCode();
        chainCode.setChannelName(channelName);//设置当前将要访问的智能合约所属频道名称
        chainCode.setChaincodeName(chaincodeName);//设置智能合约名称
        chainCode.setChaincodePath(chaincodePath);//设置智能合约安装路径
        chainCode.setChaincodeVersion(chaincodeVersion);//设置智能合约版本号
        chainCode.setInvokeWatiTime(100000);
        chainCode.setDeployWatiTime(120000);
        return chainCode;
    }

    public static List<FabricOrderers> getFabricOrderersList(){
        List<FabricOrderers> list = new ArrayList<>();
        FabricOrderers fabricOrderers = new FabricOrderers() ;
        fabricOrderers.setOrdererDomainName("example.com");
        fabricOrderers.addOrderer("orderer.example.com" ,"grpcs://47.94.226.157:7050" );
        list.add(fabricOrderers);
        return list ;
    }

    public static FabricPeers getFabricPeers(){
        FabricPeers peers = new FabricPeers() ;
        peers.setOrgName("peerOrg1");//设置组织名称
        peers.setOrgMSPID("Org1MSP");//组织名称+MSP
        peers.setCaLocation("http://47.94.226.157:7054"); // Ca地址
        peers.setOrgDomainName("org1.example.com");//设置组织根域名

        peers.addPeer("peer0.org1.example.com", "peer0.org1.example.com",
                "grpcs://47.94.226.157:7051", "grpcs://47.94.226.157:7053");//添加排序服务器
        peers.addPeer("peer1.org1.example.com", "peer1.org1.example.com",
                "grpcs://47.94.226.157:8051", "grpcs://47.94.226.157:8053");//添加排序服务器
        return peers ;
    }
}
