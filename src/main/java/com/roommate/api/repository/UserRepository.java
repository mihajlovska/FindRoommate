package com.roommate.api.repository;

import com.roommate.api.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<AppUser,Long> {

    AppUser findAppUserByInterest(String interest);
    List<AppUser> findAppUserByFirstNameIgnoreCaseContaining(String firstName);
    List<AppUser> findAppUserByNextDestinationIgnoreCaseContaining(String nextDestination);
}
