package dev.StamSite.Collage;


import dev.StamSite.Collage.lib.PhotoInfo;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhotoInfoRepository extends MongoRepository<PhotoInfo, ObjectId> {
    Optional<PhotoInfo> findByphotoId(String id);

}
