import com.gomain.layout.integration.SealService;
import com.gomain.layout.integration.impl.SealServiceImpl;
import com.gomain.layout.pojo.QuerySealRsp;
import com.gomain.layout.pojo.SdkStampRsp;
import org.junit.Test;

/**
 * 印章服务单元测试
 * @author caimeng
 * @date 2023/10/7 18:03
 */
public class SealServiceTests {

    /**
     * 单元测试：查询印章数据
     */
    @Test
    public void findSealTest() {
        String url = "http://192.168.200.143:9001/core/sdk";
        String userId = "99999";
        String sealId = "51010409013105";
        SealService sealService = new SealServiceImpl(url);
        QuerySealRsp seal = sealService.findSeal(userId, sealId);
        System.out.println(seal);
    }

    /**
     * 单元测试：调用SDK签章
     */
    @Test
    public void sdkStampTest() {
        String url = "http://192.168.200.143:9001/core/sdk";
        String userId = "99999";
        String sealId = "51010409013105";
        String digest = "hqC6xFBDDP47Br6ASuSDmDnidFpT5Wa2K2O9+7iEKo4=";
        SealService sealService = new SealServiceImpl(url);
        SdkStampRsp sdkStamp = sealService.sdkStamp(userId, sealId, digest);
        System.out.println(sdkStamp);
    }
}
