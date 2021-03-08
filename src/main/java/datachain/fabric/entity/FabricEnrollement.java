package datachain.fabric.entity;

import lombok.Getter;
import org.hyperledger.fabric.sdk.Enrollment;

import java.security.PrivateKey;

/**
 *
 * 自定义注册登记操作类
 * @Author Leo Hope
 * @Email houmingefeng@datachain.cc
 * @Data 2018/5/7 下午8:23
 */
@Getter
public class FabricEnrollement implements Enrollment {

    /** 私钥 */
    private final PrivateKey key;
    /** 授权证书 */
    private final String cert;

    public FabricEnrollement(PrivateKey privateKey, String certificate) {
        this.cert = certificate;
        this.key = privateKey;
    }
}
