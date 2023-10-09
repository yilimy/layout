import com.gomain.layout.integration.SealService;
import com.gomain.layout.integration.impl.SealServiceImpl;
import com.gomain.layout.pdf.v5.PdfStampService;
import com.gomain.layout.pojo.Constant;
import com.gomain.layout.pojo.StampStrategy;
import com.gomain.layout.pojo.StrategyPosition;
import org.junit.Test;

import java.util.Arrays;

/**
 * @author caimeng
 * @date 2023/10/8 17:55
 */
public class PdfStampTests {

    /**
     * 签章单元测试
     * @throws Exception Exception
     */
    @Test
    public void stamp() throws Exception{
        String pdfPath = "E:\\tmp\\blank.pdf";
        String destPath = "E:\\tmp\\blank_signed_" + System.currentTimeMillis() + ".pdf";
        String url = "http://192.168.200.143:9001/core/sdk";
        String userId = "99999";
        String sealId = "51010409013105";
        // 初始化签章服务
        SealService sealService = new SealServiceImpl(url);
        PdfStampService stampService = new PdfStampService(sealService);
        // 制定签章策略
        StrategyPosition position1 = new StrategyPosition(1, 36, 144, Constant.COORDINATE_LEFT_TOP);
        StrategyPosition position2 = new StrategyPosition(1, 100, 300, Constant.COORDINATE_LEFT_TOP);
        StampStrategy strategy = new StampStrategy(userId, sealId, Arrays.asList(position1, position2));
        // 签章
        stampService.stampSingle(pdfPath, destPath, strategy);
    }
}
