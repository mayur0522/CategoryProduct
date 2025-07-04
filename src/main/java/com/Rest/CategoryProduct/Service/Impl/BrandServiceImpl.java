package com.Rest.CategoryProduct.Service.Impl;

import com.Rest.CategoryProduct.Entity.Brand;
import com.Rest.CategoryProduct.Exceptions.ResourceNotFoundExceptions;
import com.Rest.CategoryProduct.Repositories.BrandRepositories;
/*import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;*/
import com.Rest.CategoryProduct.Service.BrandService;
import com.Rest.CategoryProduct.pubsub.brand.BrandPubSubPublisher;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class BrandServiceImpl implements BrandService {
//    private static final Logger log = LoggerFactory.getLogger(BrandServiceImpl.class);
    @Autowired
    private BrandPubSubPublisher pubSubPublisher;
    @Autowired
    private BrandRepositories brandRepo;

/*    @PersistenceContext
    private EntityManager entityManager; */

    @Override
    public Brand getBrandById(Long id) {
        log.info("Requesting to Fetch the brand record with id {}",id);
        Optional<Brand> findById = Optional.ofNullable(brandRepo.findById(id)
                .orElseThrow(() -> {
                    log.info("Brand does not found with id : {}",id);
                    return new ResourceNotFoundExceptions("Brand does not found with id : " + id);
                }));

        Brand brand = findById.get();
        log.info("Brand found with id : {}",id);

        // Using pub/sub
        String message = String.format("{\"event\":\"GET_BRAND\", \"id\":%d, \"name\":\"%s\"}",id,brand.getBrandName());
        pubSubPublisher.publish(message);
        return brand;

    }

    @Override
    public List<Brand> getAllBrand() {
        log.info("Requesting to fetch all the brands");
        List<Brand> brands = brandRepo.findAll();
        if (brands.isEmpty()){
            log.warn("No brands found in the database.");
            return brands;
        }
        log.info("Fetched {} brands",brands.size());
        // Using pub/sub
        String message = String.format("{\"event\":\"GET_All_BRANDS\", \"Count\":%s}",brands.size());
        pubSubPublisher.publish(message);
        return brands;
    }

/*    @Override
    public String insertBrand(String name, Long categoryId) {
        return "Data inserted successfully";
    }*/

    @Override
    public String insertBrand(Brand brand) {
        log.info("Request received to create a new brand with name : {}",brand.getBrandName());
        Optional<Brand> brandExist = brandRepo.findByBrandName(brand.getBrandName());
        if(brandExist.isPresent()){
            log.error("{} brand already exists !!!",brand.getBrandName());
            return brand.getBrandName()+" Brand already exists";
        }
        brandRepo.save(brand);
        log.info("Successfully created a new category : {}",brand.getBrandName());

        // Using pub-sub
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("event", "BRAND_CREATED");
        jsonObject.addProperty("id", brand.getBrandId());
        jsonObject.addProperty("name", brand.getBrandName());

        String message = jsonObject.toString();
        pubSubPublisher.publish(message);
        return "Brand inserted successfully";
    }


    @Override
    public String updateBrand(Brand brand, Long id) {
        log.info("Request received to update the brand with id : {}",id);
        Optional<Brand> brandExist = Optional.ofNullable(brandRepo.findById(id)
                .orElseThrow(() -> {
                    log.error("Brand Not Found With Id : {}", id);
                    return new ResourceNotFoundExceptions("Brand Does Not Found With Id : {}" + id);
                }));
/*        if(!brandExist.isPresent()){
            return "There is no such brand";
        }*/

        Brand brand1 = brandExist.get();
        brand1.setBrandName(brand.getBrandName());
        brandRepo.save(brand1);
        log.info("Updated a brand with id : {}",id);

        // Using pub-sub
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("event", "BRAND_UPDATED");
        jsonObject.addProperty("id", id);
        jsonObject.addProperty("name", brand.getBrandName());

        String message = jsonObject.toString();
        pubSubPublisher.publish(message);
        return "Brand updated successfully";
    }

    @Override
    public String deleteBrand(Long id) {
        log.info("Request received to delete the brand with id : {}",id);
        Brand brandExist = brandRepo.findById(id)
                .orElseThrow(()->{
                    log.error("Brand Not Found With Id : {}",id);
                    return new ResourceNotFoundExceptions("Brand Not Found With Id : "+id);
                });
        brandRepo.deleteById(id);
        log.info("Deleted brand with id : {}",id);

        // Using pub/sub
        String message = String.format("{\"event\":\"BRAND_DELETED\", \"id\":%d, \"name\":\"%s\"}",id,brandExist.getBrandName());
        pubSubPublisher.publish(message);
//        resetAutoIncrement();
        return "Brand deleted successfully";
    }

/*    @Transactional
    public void resetAutoIncrement(){
        entityManager.createNativeQuery("ALTER TABLE brand AUTO_INCREMENT = 1").executeUpdate();
    }*/
}

//                                          Status
//Prometheus                                Done
//Grafana                                   Done
//Unit testing                              Done
//apply splunk / OpenObserve
//Pub-Sub GCP                               Done
//Kafka
//OAuth2  : Github, Google,

//Docker
//Jenkins CI-CD

//Microservices
//GitHub : Branching, revert, rebase, cherry-pick, go back to previous commit
//Kubernetes : architecture, Commands,



// use exception handling                   Done
// use @ControllerAdvice                    Done
//User module and apply jwt or oAuth2       Done
// Method level Authorization               Done
//Add some custom finder methods  -         Done
//use logging framework : log4j, slf4j -    Done


//Theory:
//OS Working and internal working
