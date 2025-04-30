/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.publictransport.repositories;

import com.publictransport.models.User;
import com.publictransport.models.VehicleType;

/**
 *
 * @author duong
 */
public interface UserRepository {
    User getUserByUsername(String username);

}
