package datachain.fabric.entity;

import lombok.*;

/**
 * @Author 侯明峰
 * @Email houmingefeng@datachain.cc
 * @Data 2018/2/7 下午4:56
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FabricCode {

    private String code = "1000" ;

    private Object data ;

    private String error ;

    public FabricCode(String code , String error ){
        this.code = code ;
        this.error = error ;
    }

    public FabricCode(Object data){
        this.data = data ;
    }

}
