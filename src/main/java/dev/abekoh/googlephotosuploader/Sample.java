package dev.abekoh.googlephotosuploader;

import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.proto.BatchCreateMediaItemsResponse;
import com.google.photos.library.v1.proto.NewMediaItem;
import com.google.photos.library.v1.proto.NewMediaItemResult;
import com.google.photos.library.v1.upload.UploadMediaItemRequest;
import com.google.photos.library.v1.upload.UploadMediaItemResponse;
import com.google.photos.library.v1.util.NewMediaItemFactory;
import com.google.photos.types.proto.MediaItem;
import com.google.rpc.Code;
import com.google.rpc.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.RandomAccessFile;
import java.util.List;

@Component
public class Sample {

    private PhotosLibraryClient client;

    @Autowired
    public Sample(PhotosLibraryClient client) {
        this.client = client;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        try {
            UploadMediaItemRequest uploadRequest = UploadMediaItemRequest.newBuilder()
                    .setFileName("test.png")
                    .setDataFile(new RandomAccessFile("/Users/abekoh/Pictures/vlcsnap-2019-10-12-18h27m23s494.png", "r"))
                    .build();
            UploadMediaItemResponse uploadResponse = this.client.uploadMediaItem(uploadRequest);
            if (uploadResponse.getError().isPresent()) {
                UploadMediaItemResponse.Error error = uploadResponse.getError().get();
                System.err.println("Error: " + error);
                return;
            }
            String uploadToken = uploadResponse.getUploadToken().get();
            System.out.println("UploadToken: " + uploadToken);

            NewMediaItem newMediaItem = NewMediaItemFactory.createNewMediaItem(uploadToken, "test image");
            List<NewMediaItem> newItems = List.of(newMediaItem);

            BatchCreateMediaItemsResponse response = this.client.batchCreateMediaItems(newItems);

            for (NewMediaItemResult itemsResponse : response.getNewMediaItemResultsList()) {
                Status status = itemsResponse.getStatus();
                if (status.getCode() == Code.OK_VALUE) {
                    MediaItem createdItem = itemsResponse.getMediaItem();
                    System.out.println("baseUrl: " + createdItem.getBaseUrl() + "productUrl: " + createdItem.getProductUrl());
                } else {
                    System.err.println("failed to upload.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
