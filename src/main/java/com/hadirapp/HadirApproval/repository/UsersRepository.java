/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hadirapp.HadirApproval.repository;

import com.hadirapp.HadirApproval.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author herli
 */
@Repository
public interface UsersRepository extends JpaRepository<Users, String>{
    
    @Query(value="SELECT * FROM users WHERE user_id = ?1",nativeQuery = true)
    public Users findUserById(@Param ("id") String id);
    
    //select bootcamp id trainer
    @Query(value="SELECT b.bootcamp_id FROM users u JOIN bootcamp_detail bd ON u.user_id = bd.user_id JOIN bootcamp b ON b.bootcamp_id = bd.bootcamp_id WHERE u.user_id = ?1", nativeQuery = true)
    public String findBootcampidByUserId(@Param ("id") String id);
    
}
