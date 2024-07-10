package dev.StamSite.Collage.lib;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flickr4java.flickr.photos.Photo;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


@Data
@JsonIgnoreProperties({"referencedPhoto"})
@Document(collection = "images")
@NoArgsConstructor
@AllArgsConstructor
public class PhotoInfo {
    @Id
    private ObjectId id;

    private String photoId;

    private String src;

    private int width;

    private int height;

    //private Photo referencedPhoto;

    private String description;

    private String author;

    private String title;

    private List<PhotoBasicInfo> sizeList;

    public PhotoInfo(Photo photo, String src, String realName, int width, int height, List<PhotoBasicInfo> sizes) {
        this.photoId = photo.getId();
        this.src=src;
        this.width=width;
        this.height=height;
        //this.referencedPhoto = photo;
        this.description = photo.getDescription();
        this.author = realName;
        this.title = photo.getTitle();
        this.sizeList = sizes;
    }
}
