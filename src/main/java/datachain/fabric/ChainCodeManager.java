package datachain.fabric;

import com.alibaba.fastjson.JSON;
import com.google.protobuf.ByteString;
import datachain.fabric.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @Author Leo Hope
 * @Email houmingefeng@datachain.cc
 * @Data 2018/5/4 下午1:55
 */
public class ChainCodeManager {

    private FabricConfig fabricConfig ;

    private List<FabricOrderers> fabricOrderersList ;

    private FabricPeers fabricPeers ;

    private FabricChainCode fabricChainCode;

    private HFClient client ;

    private FabricOrg fabricOrg ;

    private ChaincodeID chaincodeID;

    private Channel channel;

    public ChainCodeManager(FabricConfig fabricConfig)
            throws IllegalAccessException, InvocationTargetException,
            InvalidArgumentException, InstantiationException, NoSuchMethodException,
            CryptoException, ClassNotFoundException, TransactionException {

        this.fabricConfig = fabricConfig ;
        this.fabricOrderersList = this.fabricConfig.getFabricOrderersList() ;
        this.fabricPeers = this.fabricConfig.getFabricPeers() ;
        this.fabricChainCode = this.fabricConfig.getChaincode() ;
        this.fabricOrg = this.getFabricOrg() ;

        this.chaincodeID = getChaincodeID();
        this.client = HFClient.createNewInstance() ;
        this.client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());

        this.channel = getChannel();
        this.client.setUserContext(fabricOrg.getPeerAdmin()); // 也许是1.0.0测试版的bug，只有节点管理员可以调用链码
    }

    private FabricOrg getFabricOrg() {
        return new FabricOrg(fabricPeers , this.fabricConfig.getCryptoConfigPath()) ;
    }

    private Channel getChannel() throws InvalidArgumentException , TransactionException{
        this.client.setUserContext(fabricOrg.getPeerAdmin());
        return getChannel(client);
    }

    private Channel getChannel(HFClient client) throws InvalidArgumentException ,TransactionException {
        Channel channel = client.newChannel(fabricChainCode.getChannelName()) ;
        for (FabricPeer peer : this.fabricPeers.get()){
            //获取Peer密钥
            File peerCert = Paths.get(this.fabricConfig.getCryptoConfigPath(),
                    "/peerOrganizations", fabricPeers.getOrgDomainName(),
                    "peers", peer.getPeerName(),
                    "tls/server.crt")
                    .toFile();

            if (!peerCert.exists()) {
                throw new RuntimeException(
                        String.format("Missing cert file for: %s. Could not find at location: %s", peer.getPeerName(), peerCert.getAbsolutePath()));
            }
            Properties peerProperties = new Properties();
            peerProperties.setProperty("pemFile", peerCert.getAbsolutePath());
            // ret.setProperty("trustServerCertificate", "true"); //testing
            // environment only NOT FOR PRODUCTION!
            peerProperties.setProperty("hostnameOverride", peer.getPeerName());
            peerProperties.setProperty("sslProvider", "openSSL");
            peerProperties.setProperty("negotiationType", "TLS");
            // 在grpc的NettyChannelBuilder上设置特定选项
            peerProperties.put("grpc.ManagedChannelBuilderOption.maxInboundMessageSize", 9000000);
            channel.addPeer(client.newPeer(peer.getPeerName(), peer.getPeerLocation(), peerProperties));
            if (peer.isAddEventHub()) {
                channel.addEventHub(
                        client.newEventHub(peer.getPeerEventHubName(),
                                peer.getPeerEventHubLocation(), peerProperties));
            }

        }

        for (FabricOrderers fabricOrderers : this.fabricOrderersList){
            for (FabricOrderer orderer : fabricOrderers.get()){
                File ordererCert = Paths.get(this.fabricConfig.getCryptoConfigPath(),
                        "/ordererOrganizations", fabricOrderers.getOrdererDomainName(),
                        "orderers", orderer.getOrdererName(),
                        "tls/server.crt").toFile();
                if (!ordererCert.exists()) {
                    throw new RuntimeException(
                            String.format("Missing cert file for: %s. Could not find at location: %s",
                                    orderer.getOrdererName(), ordererCert.getAbsolutePath()));
                }
            /*File pem = Paths.get(config.getCryptoConfigPath(), "/ordererOrganizations", orderers.getOrdererDomainName(),
                    String.format("tlsca/tlsca.%s-cert.pem", orderers.getOrdererDomainName() )).toFile();*/
                Properties ordererProperties = new Properties();
                ordererProperties.setProperty("pemFile", ordererCert.getAbsolutePath());
                ordererProperties.setProperty("hostnameOverride", orderer.getOrdererName());
                ordererProperties.setProperty("sslProvider", "openSSL");
                ordererProperties.setProperty("negotiationType", "TLS");
                ordererProperties.put("grpc.ManagedChannelBuilderOption.maxInboundMessageSize", 9000000);
                ordererProperties.setProperty("ordererWaitTimeMilliSecs", "300000");
                channel.addOrderer(
                        client.newOrderer(orderer.getOrdererName(), orderer.getOrdererLocation(), ordererProperties));

            }
        }
        if (!channel.isInitialized()) {
            channel.initialize();
        }
        if (this.fabricConfig.isRegisterEvent()) {
            channel.registerBlockListener(new BlockListener() {

                @Override
                public void received(BlockEvent event) {
                    // TODO
                    //"========================Event事件监听开始========================"
                    ByteString byteString = event.getBlock().getData().getData(0);
                    String result = byteString.toStringUtf8();

                    String r1[] = result.split("END CERTIFICATE");
                    String rr = r1[2];
                    //"========================Event事件监听结束========================"
                }
            });
        }
        return channel ;
    }


    private ChaincodeID getChaincodeID() {
        return ChaincodeID.newBuilder().setName(this.fabricChainCode.getChaincodeName())
                .setVersion(this.fabricChainCode.getChaincodeVersion())
                .setPath(this.fabricChainCode.getChaincodePath())
                .build();
    }


    /**
     * 执行智能合约
     *
     * @param fcn   方法名
     * @param arrayList  参数数组
     * @return
     * @throws InvalidArgumentException
     * @throws ProposalException
     * @throws IOException
     */
    public FabricCode invoke(String fcn ,ArrayList<String> arrayList)
            throws InvalidArgumentException, ProposalException ,IOException{

        TransactionProposalRequest transactionProposalRequest = client.newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeID(this.chaincodeID);
        transactionProposalRequest.setFcn(fcn);
        transactionProposalRequest.setArgs(arrayList);

        Map<String, byte[]> tm2 = new HashMap<>();
        tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8));
        tm2.put("method", "TransactionProposalRequest".getBytes(UTF_8));
        tm2.put("result", ":)".getBytes(UTF_8));
        transactionProposalRequest.setTransientMap(tm2);

        Collection<ProposalResponse> successful = new LinkedList<>();
        Collection<ProposalResponse> failed = new LinkedList<>();
        Collection<ProposalResponse> transactionPropResp = this.channel.sendTransactionProposal(transactionProposalRequest, this.channel.getPeers());
        for (ProposalResponse response : transactionPropResp) {
            if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
                successful.add(response);
            } else {
                failed.add(response);
            }
        }
        Collection<Set<ProposalResponse>> proposalConsistencySets = SDKUtils.getProposalConsistencySets(transactionPropResp);
        if (proposalConsistencySets.size() != 1)
            return new FabricCode("5601" ,"Expected only one set of consistent proposal responses but got " + proposalConsistencySets.size() ) ;
            //log.error("Expected only one set of consistent proposal responses but got " + proposalConsistencySets.size());

        FabricCode code = new FabricCode() ;
        //如果有一个peer通过 就成功了
        if (successful.size() > 0) {
            //log.info("Successfully received transaction proposal responses.");
            ProposalResponse resp = transactionPropResp.iterator().next();
            byte[] x = resp.getChaincodeActionResponsePayload();
            String resultAsString = null;
            if (x != null) {
                resultAsString = new String(x, "UTF-8");
            }
            //log.info("resultAsString = " + resultAsString);
            //共识发送给其他的peer
            this.channel.sendTransaction(successful);
            FabricCode fabricCode = JSON.parseObject(resultAsString ,FabricCode.class) ;
            if ("1000".equals(fabricCode.getCode()))
                return fabricCode ;
            else
                return new FabricCode("5701" , fabricCode.getError());
        }
        //所有节点都失败了
        ProposalResponse firstTransactionProposalResponse = failed.iterator().next();
        return new FabricCode("5602" , firstTransactionProposalResponse.getMessage());

    }


    /**
     * 查询智能合约
     *
     * @param fcn
     *            方法名
     * @param args
     *            参数数组
     * @return
     * @throws InvalidArgumentException
     * @throws ProposalException
     */
    public FabricCode query(String fcn, ArrayList<String> args) throws InvalidArgumentException, ProposalException {

        QueryByChaincodeRequest queryByChaincodeRequest = client.newQueryProposalRequest();
        queryByChaincodeRequest.setArgs(args);
        queryByChaincodeRequest.setFcn(fcn);
        queryByChaincodeRequest.setChaincodeID(chaincodeID);

        Map<String, byte[]> tm2 = new HashMap<>();
        tm2.put("HyperLedgerFabric", "QueryByChaincodeRequest:JavaSDK".getBytes(UTF_8));
        tm2.put("method", "QueryByChaincodeRequest".getBytes(UTF_8));
        queryByChaincodeRequest.setTransientMap(tm2);

        Collection<ProposalResponse> queryProposals = channel.queryByChaincode(queryByChaincodeRequest, channel.getPeers());

        ProposalResponse response = null ;
        for (ProposalResponse proposalResponse : queryProposals) {

            if (proposalResponse.isVerified() && proposalResponse.getStatus() == ProposalResponse.Status.SUCCESS){
                String payload = proposalResponse.getProposalResponse().getResponse().getPayload().toStringUtf8();
                /*log.debug("Query payload from peer: " + proposalResponse.getPeer().getName());
                log.debug("" + payload);*/
                FabricCode fabricCode = JSON.parseObject(payload ,FabricCode.class) ;
                if ("1000".equals(fabricCode.getCode()))
                    return fabricCode ;
                else
                    return new FabricCode("5702" , fabricCode.getError());
            }
            response = proposalResponse ;
        }
        //所有节点查询失败 失败消息按照最后一个节点提示
        return new FabricCode("5603" , " status: " + response.getStatus() + ". Messages: " + response.getMessage()) ;


    }


}
