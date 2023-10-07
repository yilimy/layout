import com.gomain.layout.ofd.OfdVerifyService;
import org.junit.Test;

/**
 * OFD验证单元测试
 * @author caimeng
 * @date 2023/10/7 15:53
 */
public class OfdVerifyTests {
    /**
     * OFD文件验证
     * @throws Exception Exception
     */
    @Test
    public void verify() throws Exception{
        // 单个签章文件
//        String path = "E:\\tmp\\ofd\\blank_signed.ofd";
        // 多个签章的文件
        String path = "E:\\tmp\\ofd\\signed_two.ofd";
        // TODO 签章和签名同时存在的文件
        OfdVerifyService ofdVerifyService = new OfdVerifyService();
        ofdVerifyService.verifySignatures(path);
    }

}
