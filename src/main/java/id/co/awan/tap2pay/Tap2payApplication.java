package id.co.awan.tap2pay;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.Security;

@SpringBootApplication
public class Tap2payApplication {

    public static void main(String[] args) {

        Security.addProvider(new BouncyCastleProvider());
        SpringApplication.run(Tap2payApplication.class, args);
    }

}
