package datachain.fabric.entity;

import lombok.Getter;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.Set;

/**
 * @Author Leo Hope
 * @Email houmingefeng@datachain.cc
 * @Data 2018/5/7 下午2:38
 */
public class FabricUser implements User {

    /** 名称 */
    @Getter
    private String name;
    /** 规则 */
    @Getter
    private Set<String> roles;
    /** 账户 */
    @Getter
    private String account;
    /** 从属联盟 */
    @Getter
    private String affiliation;
    /** 组织 */
    private String organization;
    /** 注册操作的密保 */
    @Getter
    private String enrollmentSecret;
    /** 会员id */
    @Getter
    private String mspId;
    /** 注册登记操作 */
    @Getter
    Enrollment enrollment = null;


    public FabricUser(String name , String mspId, File privateKeyFile, File certificateFile){
        try {
            // 创建User，并尝试从键值存储中恢复它的状态(如果找到的话)
            this.name = name ;
            this.mspId = mspId ;
            String certificate = new String(IOUtils.toByteArray(new FileInputStream(certificateFile)), "UTF-8");
            PrivateKey privateKey = this.getPrivateKeyFromBytes(IOUtils.toByteArray(new FileInputStream(privateKeyFile)));
            this.setEnrollment(new FabricEnrollement(privateKey, certificate));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过字节数组信息获取私钥
     *
     * @param data
     *            字节数组
     *
     * @return 私钥
     *
     * @throws IOException
     * @throws NoSuchProviderException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    private PrivateKey getPrivateKeyFromBytes(byte[] data) throws IOException {
        final Reader pemReader = new StringReader(new String(data));
        final PrivateKeyInfo pemPair;
        try (PEMParser pemParser = new PEMParser(pemReader)) {
            pemPair = (PrivateKeyInfo) pemParser.readObject();
        }
        PrivateKey privateKey = new JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME).getPrivateKey(pemPair);
        return privateKey;
    }

    static {
        try {
            Security.addProvider(new BouncyCastleProvider());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 设置注册登记操作信息并将用户状�?�更新至存储配置对象
     *
     * @param enrollment
     *            注册登记操作
     */
    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
    }
}
