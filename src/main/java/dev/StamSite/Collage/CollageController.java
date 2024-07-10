package dev.StamSite.Collage;

import dev.StamSite.Collage.lib.PhotoInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/collage")
public class CollageController {
    @Autowired
    private FlickrService flickrService;

    public CollageController() {}

    @GetMapping()
    public ResponseEntity<List<PhotoInfo>> getCollage(@RequestParam(defaultValue = "20") int perPage, @RequestParam(defaultValue = "1") int page) {
        List<PhotoInfo> picList = flickrService.getUserPhotos(perPage,page);
        return new ResponseEntity<List<PhotoInfo>>(picList,HttpStatus.OK);
    }
    @GetMapping("/all")
    public ResponseEntity<List<PhotoInfo>> getAllCollage() {
        return new ResponseEntity<>(flickrService.getAll(),HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<PhotoInfo>> getCollageById(@PathVariable String id) {
        return new ResponseEntity<Optional<PhotoInfo>>(flickrService.getSinglePhoto(id),HttpStatus.OK);

    }
}
