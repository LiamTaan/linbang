package cn.iocoder.yudao.module.linbang.service.sensitiveword;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SensitiveContentDetectServiceImplTest {

    private final SensitiveContentDetectServiceImpl service = new SensitiveContentDetectServiceImpl();

    @Test
    void readBoundedImageShouldAcceptNormalImage() throws Exception {
        BufferedImage image = new BufferedImage(32, 24, BufferedImage.TYPE_INT_RGB);

        BufferedImage decoded = service.readBoundedImage(writePng(image));

        assertEquals(32, decoded.getWidth());
        assertEquals(24, decoded.getHeight());
    }

    @Test
    void readBoundedImageShouldRejectOversizedDimensionsBeforeDecode() throws Exception {
        BufferedImage image = new BufferedImage(10_001, 1, BufferedImage.TYPE_INT_RGB);
        byte[] content = writePng(image);

        assertThrows(SensitiveContentDetectServiceImpl.ImageValidationException.class,
                () -> service.readBoundedImage(content));
    }

    private byte[] writePng(BufferedImage image) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(image, "png", output);
        return output.toByteArray();
    }
}
