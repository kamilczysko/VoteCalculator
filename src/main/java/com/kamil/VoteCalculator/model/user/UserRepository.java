package com.kamil.VoteCalculator.model.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByPesel(String pesel);

    @Override
    Optional<User> findById(Long aLong);

    @Query(nativeQuery = true, value = "SELECT COUNT(*) FROM user WHERE voided_vote IS TRUE")
    Integer getVoidedVotes();

    @Query(nativeQuery = true, value = "SELECT COUNT(*) FROM user WHERE disallowed is TRUE")
    Integer getDisallowedVotes();
}
