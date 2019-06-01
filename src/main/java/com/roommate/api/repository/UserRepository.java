package com.roommate.api.repository;

import com.roommate.api.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser,Long> {

    AppUser findAppUserByInterest(String interest);
}
