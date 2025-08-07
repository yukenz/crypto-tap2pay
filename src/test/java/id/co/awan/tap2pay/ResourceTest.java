package id.co.awan.tap2pay;

import id.co.awan.tap2pay.service.ERC20Service;
import id.co.awan.tap2pay.service.EthereumService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class})
@ContextConfiguration(classes = {ERC20Service.class, EthereumService.class})
public class ResourceTest {

    @Test
    void loadResource() {

    }
}
