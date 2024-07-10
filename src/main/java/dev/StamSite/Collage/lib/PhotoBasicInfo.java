package dev.StamSite.Collage.lib;

import com.flickr4java.flickr.photos.Photo;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhotoBasicInfo {

    private String photoId;

    private String src;

    private int width;

    private int height;

    public PhotoBasicInfo(Photo photo, String src,int width,int height) {
        this.photoId = photo.getId();
        this.src=src;
        this.width=width;
        this.height=height;
    }
}
