import com.gomain.layout.pdf.v5.PdfStampService;
import org.junit.Test;

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
        PdfStampService stampService = new PdfStampService(userId, sealId, url);
        stampService.stamp(pdfPath, destPath);
    }
}
