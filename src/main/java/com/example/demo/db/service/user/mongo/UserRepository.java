package com.example.demo.db.service.user.mongo;

import com.example.demo.db.data.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

/**
 * @author xuliduo
 * @date 2019/7/3
 * @description class UserRepository
 */
public interface UserRepository extends MongoRepository<User, Long>, UserOperations {
    @Query("{ ?0 : ?1 }")
    List<User> findByDynamicField(String field, Object value);

}
