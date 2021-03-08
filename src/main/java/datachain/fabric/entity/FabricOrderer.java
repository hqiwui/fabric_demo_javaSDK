package datachain.fabric.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author 侯明峰
 * @Email houmingefeng@datachain.cc
 * @Data 2018/1/26 下午4:32
 *
 * 排序服务器对象
 */
@Getter
@Setter
@AllArgsConstructor
public class FabricOrderer {

    /**
     * orderer 排序服务器的域名
     */
    private String ordererName;
    /**
     * orderer 排序服务器的访问地址
     */
    private String ordererLocation;
}
