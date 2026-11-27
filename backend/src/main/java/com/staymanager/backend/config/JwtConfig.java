package com.staymanager.backend.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

/** Configures JWT encoding and decoding using a shared secret key. */
@Configuration
public class JwtConfig {
  @Value("${app.jwt.secret}")
  private String secret;

  /**
   * Configures JWT signature verification and issuer validation.
   *
   * @return the configured JWT decoder
   */
  @Bean
  public JwtDecoder jwtDecoder() {
    byte[] keyBytes = Base64.getDecoder().decode(secret);
    SecretKey secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");

    NimbusJwtDecoder decoder =
        NimbusJwtDecoder.withSecretKey(secretKey).macAlgorithm(MacAlgorithm.HS256).build();

    decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer("staymanager"));

    return decoder;
  }

  /**
   * Configures the JWT encoder for signing tokens.
   *
   * @return the configured JWT encoder
   */
  @Bean
  public JwtEncoder jwtEncoder() {
    byte[] keyBytes = Base64.getDecoder().decode(secret);

    SecretKey secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");

    return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));
  }
}
