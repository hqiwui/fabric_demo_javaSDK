package datachain.fabric.entity;

import datachain.fabric.ChainCodeManager;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @Author 侯明峰
 * @Email houmingefeng@datachain.cc
 * @Data 2018/1/26 下午4:13
 *
 * 智能合约操作总参数配置器
 */
@Setter
@Getter
public class FabricConfig {

    /** 节点服务器对象 */
    private FabricPeers fabricPeers;
    /** 排序服务器对象 */
    private List<FabricOrderers> fabricOrderersList;
    /** 智能合约对象 */
    private FabricChainCode chaincode;
    /** channel-artifacts所在路径：默认channel-artifacts所在路径/xxx/WEB-INF/classes/fabric/channel-artifacts/ */
    private String channelArtifactsPath;
    /** crypto-config所在路径：默认crypto-config所在路径/xxx/WEB-INF/classes/fabric/crypto-config/ */
    private String cryptoConfigPath;
    private boolean registerEvent = false;

    public FabricConfig() {
        // 默认channel-artifacts所在路径 /xxx/WEB-INF/classes/fabric/channel-artifacts/
        this.channelArtifactsPath = getChannlePath() + "/channel-artifacts/";
        // 默认crypto-config所在路径 /xxx/WEB-INF/classes/fabric/crypto-config/
        this.cryptoConfigPath = getChannlePath() + "/crypto-config/";
    }

    /**
     * 默认fabric配置路径
     *
     * @return D:/installSoft/apache-tomcat-9.0.0.M21-02/webapps/xxx/WEB-INF/classes/fabric/channel-artifacts/
     */
    private String getChannlePath() {

        return "src/main/resources/fabric/";
    }

}
