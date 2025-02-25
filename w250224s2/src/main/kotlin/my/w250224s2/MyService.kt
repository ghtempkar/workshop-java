package my.w250224s2


import dev.samstevens.totp.code.HashingAlgorithm.SHA1
import dev.samstevens.totp.exceptions.QrGenerationException
import dev.samstevens.totp.qr.QrData.Builder
import dev.samstevens.totp.qr.QrGenerator
import dev.samstevens.totp.qr.ZxingPngQrGenerator
import dev.samstevens.totp.secret.DefaultSecretGenerator
import dev.samstevens.totp.util.Utils.getDataUriForImage
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component


@Configuration
class MyConfig {

    @Bean
    fun init1(myService: MyService): CommandLineRunner = CommandLineRunner {
        myService.run1()
    }
}

@Component
class MyService {

    fun generateNewSecret(): String {
        return DefaultSecretGenerator().generate()
    }

    fun generateQrCodeImageUri(secret: String?): String {
        val data = Builder()
            .label("Alibou Coding 2FA example")
            .secret(secret)
            .issuer("Alibou-Coding")
            .algorithm(SHA1)
            .digits(6)
            .period(30)
            .build()

        val generator: QrGenerator = ZxingPngQrGenerator()
        var imageData: ByteArray? = ByteArray(0)
        try {
            imageData = generator.generate(data)
        } catch (e: QrGenerationException) {
            throw e
//            log.error("Error while generating QR-CODE")
        }

        return getDataUriForImage(imageData, generator.imageMimeType)
    }

    fun run1() {
        val secret = generateNewSecret()
        val x = generateQrCodeImageUri(secret)
        println(x)
    }

}
