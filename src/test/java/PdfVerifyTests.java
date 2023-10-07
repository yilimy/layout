import com.gomain.layout.pdf.v5.PdfVerifyService;
import org.junit.Test;

/**
 * PDF验证单元测试
 * @author caimeng
 * @date 2023/10/7 15:49
 */
public class PdfVerifyTests {

    /**
     * PDF文件验证
     * @throws Exception Exception
     */
    @Test
    public void verify() throws Exception{
        String pdfPath = "E:\\tmp\\pdf\\blank_signed.pdf";
        PdfVerifyService pdfVerifyService = new PdfVerifyService();
        pdfVerifyService.verifySignatures(pdfPath);
    }
}
