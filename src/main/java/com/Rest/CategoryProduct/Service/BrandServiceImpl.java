package com.Rest.CategoryProduct.Service;

import com.Rest.CategoryProduct.Entity.Brand;
import com.Rest.CategoryProduct.Exceptions.ResourceNotFoundExceptions;
import com.Rest.CategoryProduct.Repositories.BrandRepositories;
/*import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BrandServiceImpl implements BrandService{

    private static final Logger logger = LoggerFactory.getLogger(BrandServiceImpl.class);
    @Autowired
    private BrandRepositories brandRepo;

//    @PersistenceContext
//    private EntityManager entityManager;

    @Override
    public Brand getBrandById(Long id) {
        logger.info("Requesting to Fetch the brand {}",id);
        Optional<Brand> findById = brandRepo.findById(id);

        if (findById.isPresent()){
            logger.info("Brand found with id : {}",id);
            return findById.get();
        }else{
            logger.info("Brand does not found with id : {}",id);
            return null;
        }
    }

    @Override
    public List<Brand> getAllBrand() {
        logger.info("Requesting to fetch all the brands");
        logger.info("Fetched all the brands");
        return brandRepo.findAll();
    }

//    @Override
//    public String insertBrand(String name, Long categoryId) {
//        return "Data inserted successfully";
//    }

    @Override
    public String insertBrand(Brand brand) {
        logger.info("Request received to create a new brand with name : {}",brand.getBrandName());
        Optional<Brand> brandExist = brandRepo.findByBrandName(brand.getBrandName());
        if(brandExist.isPresent()){
            logger.error("{} brand already exists !!!",brand.getBrandName());
            return "Brand already exists";
        }
        brandRepo.save(brand);
        logger.info("Successfully created a new category : {}",brand.getBrandName());
        return "Data inserted successfully";
    }


    @Override
    public String updateBrand(Brand brand, Long id) {
        logger.info("Request received to update the brand with id : {}",id);
        Optional<Brand> brandExist = Optional.ofNullable(brandRepo.findById(id)
                .orElseThrow(() -> {
                    logger.error("brand Not Found With Id : {}", id);
                    return new ResourceNotFoundExceptions("brand Does Not Found With Id : {}" + id);
                }));
//        if(!brandExist.isPresent()){
//            return "There is no such brand";
//        }

        Brand brand1 = brandExist.get();
        brand1.setBrandName(brand.getBrandName());
        brandRepo.save(brand1);
        logger.info("Updated a brand with id : {}",id);
        return "Data updated successfully";
    }

    @Override
    public String deleteBrand(Long id) {
        logger.info("Request received to delete the brand with id : {}",id);
        Optional<Brand> brandExist = brandRepo.findById(id);
        if(!brandExist.isPresent()){
            logger.error("Brand Not Found With Id : {}",id);
            return "Product Does Not Found With Id : "+id;
        }
        brandRepo.deleteById(id);
        logger.info("Deleted brand with id : {}",id);

//        resetAutoIncrement();
        return "Brand deleted successfully";
    }

/*    @Transactional
    public void resetAutoIncrement(){
        entityManager.createNativeQuery("ALTER TABLE brand AUTO_INCREMENT = 1").executeUpdate();
    }*/
}

//                                          Status
//Prometheus
//Grafana
//Unit testing
//apply splunk / OpenObserve
//Kafka
//OAthu2  : Github, Google,

//Docker
//Jenkins CICD

//Microservices
//Github : Branching, revert, rebase, cherrypick, go back to previous commit
//Kubernetes : architecture, Commands,



// use exception handling                   Done
// use @ControllerAdvice                    Done
//User module and apply jwt or oAuth2
// Method level Authorization               Done
//Add some custom finder methods  -         Done
//use logging framework : log4j, slf4j -    Done


//Theroy:
//OS Working and internal working
