package datachain.fabric.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Author 侯明峰
 * @Email houmingefeng@datachain.cc
 * @Data 2018/1/26 下午4:14
 *
 * Fabric创建的chainCode信息，涵盖所属channel等信息
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FabricChainCode {


    /** 当前将要访问的智能合约所属频道名称 */
    private String channelName; // ffetest
    /** 智能合约名称 */
    private String chaincodeName; // ffetestcc
    /** 智能合约安装路径 */
    private String chaincodePath; // github.com/hyperledger/fabric/xxx/chaincode/go/example/test
    /** 智能合约版本号 */
    private String chaincodeVersion; // 1.0
    /** 执行智能合约操作等待时间 */
    private int invokeWatiTime = 100000;
    /** 执行智能合约实例等待时间 */
    private int deployWatiTime = 120000;
}
