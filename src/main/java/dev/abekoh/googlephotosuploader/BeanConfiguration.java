package dev.abekoh.googlephotosuploader;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.UserCredentials;
import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.PhotosLibrarySettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class BeanConfiguration {

    @Bean
    public GoogleCredentials googleCredentials(@Value("${gcp.client-id:#{null}}") String clientId,
                                               @Value("${gcp.client-secret:#{null}}") String clientSecret,
                                               @Value("${gcp.refresh-token:#{null}}") String refreshToken) {
        return UserCredentials.newBuilder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRefreshToken(refreshToken)
                .build();
    }

    @Bean
    public PhotosLibrarySettings photosLibrarySettings(GoogleCredentials credentials) throws IOException {
        return PhotosLibrarySettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build();
    }

    @Bean
    public PhotosLibraryClient photosLibraryClient(PhotosLibrarySettings photosLibrarySettings) throws IOException {
        return PhotosLibraryClient.initialize(photosLibrarySettings);
    }

}
