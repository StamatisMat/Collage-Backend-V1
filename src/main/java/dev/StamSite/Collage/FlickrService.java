package dev.StamSite.Collage;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.people.User;

import com.flickr4java.flickr.photos.Size;
import dev.StamSite.Collage.lib.PhotoBasicInfo;
import dev.StamSite.Collage.lib.PhotoInfo;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;


@Getter
@Setter
@Service
public class FlickrService {
    private Flickr flickr;
    @Value("${flickr.apiKey}")
    private String apiKey;
    @Value("${flickr.sharedSecret}")
    private String sharedSecret;
    @Value("${flickr.nsid}")
    private String nsid;

    @Autowired
    private PhotoInfoRepository photoRepository;

    private String RealName;

    private List<PhotoInfo> allPicList;

    private String[] sizes =     {"_t.jpg","_m.jpg","_w.jpg",".jpg","_b.jpg","_k.jpg"};
    private String[] sizeNames = {"Thumbnail","Small","Small 400","Medium","Large","Large 2048"};
    Logger logger = LoggerFactory.getLogger(FlickrService.class);

    // RealName is kinda constant, cache it to save api calls
    @PostConstruct
    public void getRealName() {
        User user;
        try {
            user = flickr.getPeopleInterface().getInfo(nsid);
            RealName = user.getRealName();
        }catch (FlickrException e) {
            logger.error(e.getMessage());
        }
    }

    // Deprecated since it gets the urls directly from the flickr api or db
    public Collection<String> getUrls(Photo photo) {
        String url = photo.getUrl();
        Collection<String> urls = new HashSet<>();
        for(String s : sizes) {
            urls.add(url+s);
        }
        return urls;
    }
    // Deprecated since it gets the urls directly from the flickr api or db
    public String getUrl(Photo photo) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("https://live.staticflickr.com/");
        buffer.append(photo.getServer());
        buffer.append("/");
        buffer.append(photo.getId());
        buffer.append("_");
        buffer.append(photo.getSecret());
        return buffer.toString();
    }
    public Optional<PhotoInfo> getSinglePhoto(String id) {
        return photoRepository.findByphotoId(id);
    }

    public List<PhotoInfo> getAll() {
        return photoRepository.findAll();
    }

    public List<PhotoInfo> getUserPhotos(int perPage, int page) {
        if(allPicList==null) allPicList = photoRepository.findAll();
        // TODO: Add logic so it can check for caching issues, and update the cache
        if(!allPicList.isEmpty()){
            int from = (page - 1) * perPage;
            int to = page * perPage;
            if(from > allPicList.size()) {
                from = 0;
                to = allPicList.size();
            }else if(to > allPicList.size()) {
                to = allPicList.size();
            }

            return allPicList.subList(from, to);
        }else {
            return getUserPhotosFromFlickr(perPage,page);
        }
    }

    // Very slow, we should just get everything from repo cache
    public List<PhotoInfo> getUserPhotosFromFlickr(int perPage, int page){
        allPicList = new ArrayList<>();
        try {
            while (flickr == null) {
                init();
            }
            HashSet<String> extras = new HashSet<>();
            extras.add("sizes");
            extras.add("description");
            extras.add("media");
            PhotoList<Photo> flickrList = flickr.getPeopleInterface().getPublicPhotos(nsid, extras, perPage, page);
            /*
                For every photo get its details and populate the final list of photos
                Flickr4java uses a prepriatary object called Photo that keeps useless(for me) info
                that usually is null so a new PhotoInfo object is created. PhotoBasicInfo contains
                the least amount of info needed for the react photo album gallery.
            */
            for(Photo photo:flickrList) {
                Collection<Size> photoSizes =flickr.getPhotosInterface().getSizes(photo.getId());
                ArrayList<PhotoBasicInfo> sizes = new ArrayList<>();
                String Url = null;
                int defaultPictureWidth=0, defaultPictureHeight=0;
                int sizeIndex = 0;
                /*
                    Populate the list of sizes because flickr doesn't provide those via the one api call
                    so we have to go for 1 api call per photo obtained to populate the different sizes for the gallery
                */
                for(Size sz: photoSizes) {
                    // Keep only the 5 sizes needed
                    if(!sz.getLabelName().equals(sizeNames[sizeIndex])) continue;
                    sizeIndex++;
                    if(sz.getLabelName().equals("Medium")) {
                        Url = sz.getSource();
                        defaultPictureHeight=sz.getHeight();
                        defaultPictureWidth=sz.getWidth();
                        continue;
                    }
                    PhotoBasicInfo sizeInfo = new PhotoBasicInfo(photo,sz.getSource(),sz.getHeight(),sz.getWidth());
                    sizes.add(sizeInfo);
                }
                while(RealName==null) getRealName();
                allPicList.add(new PhotoInfo(photo,Url,RealName,defaultPictureWidth,defaultPictureHeight,sizes));
            }
            return allPicList; // list of public photos
        }catch (FlickrException e) {
            logger.error(e.getMessage());
        }
        return null;
    }
    public FlickrService() {
    }

    @PostConstruct
    public void init() {
        System.out.println("Creating Flickr object");
        flickr = new Flickr(apiKey,sharedSecret,new REST());
        System.out.println("Successfully created Flickr object");
        //allPicList = getUserPhotos(110,1);
    }

}
