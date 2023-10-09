import com.gomain.layout.integration.SealService;
import com.gomain.layout.integration.impl.SealServiceImpl;
import com.gomain.layout.pdf.v5.PdfStampService;
import com.gomain.layout.pojo.Constant;
import com.gomain.layout.pojo.StampStrategy;
import com.gomain.layout.pojo.StrategyPosition;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author caimeng
 * @date 2023/10/8 17:55
 */
public class PdfStampTests {
    private final String pdfPath = "E:\\tmp\\blank.pdf";
    private final String destPath = "E:\\tmp\\blank_signed_" + System.currentTimeMillis() + ".pdf";
    private final String url = "http://192.168.200.143:9001/core/sdk";
    private final String userId = "99999";
    private final String sealId = "51010409013105";

    /**
     * 签章单元测试
     * @throws Exception Exception
     */
    @Test
    public void stamp() throws Exception{
        // 初始化签章服务
        SealService sealService = new SealServiceImpl(url);
        PdfStampService stampService = new PdfStampService(sealService);
        // 制定签章策略
        StrategyPosition position = new StrategyPosition(1, 36, 144, Constant.COORDINATE_LEFT_TOP);
        StampStrategy strategy = new StampStrategy(userId, sealId, Collections.singletonList(position));
        // 签章
        stampService.stampSingle(pdfPath, destPath, strategy);
    }

    /**
     * 单元测试，追加签章
     * @throws Exception Exception
     */
    @Test
    public void stampAgain() throws Exception{
        // 初始化签章服务
        SealService sealService = new SealServiceImpl(url);
        PdfStampService stampService = new PdfStampService(sealService);
        // 制定签章策略
        StrategyPosition position = new StrategyPosition(1, 36, 144, Constant.COORDINATE_LEFT_TOP);
        StampStrategy strategy = new StampStrategy(userId, sealId, Collections.singletonList(position));
        // 签章
        stampService.stampSingle(pdfPath, destPath, strategy);
        // ============= 第二次签章 =============
        // 制定签章策略
        position = new StrategyPosition(1, 100, 300, Constant.COORDINATE_LEFT_TOP);
        strategy = new StampStrategy(userId, sealId, Collections.singletonList(position));
        String againPath = "E:\\tmp\\blank_signed_" + System.currentTimeMillis() + ".pdf";
        // 签章
        stampService.stampSingle(destPath, againPath, strategy);
        System.out.println("签章完成: " + againPath);
    }

    /**
     * 多个签章
     * @deprecated 目前还有问题
     * @throws Exception Exception
     */
    @Test
    @Deprecated
    public void stampTwice() throws Exception{
        // 初始化签章服务
        SealService sealService = new SealServiceImpl(url);
        PdfStampService stampService = new PdfStampService(sealService);
        // 制定签章策略
        StrategyPosition position1 = new StrategyPosition(1, 36, 144, Constant.COORDINATE_LEFT_TOP);
        StrategyPosition position2 = new StrategyPosition(1, 100, 300, Constant.COORDINATE_LEFT_TOP);
        StampStrategy strategy = new StampStrategy(userId, sealId, Arrays.asList(position1, position2));
        // 签章
//        stampService.stampBatch(pdfPath, destPath, strategy);
    }
}
