package com.kamil.VoteCalculator.model.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.validation.Valid;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Override
    <S extends User> S save(@Valid  S entity);

    User findUserByPesel(String pesel);

    @Query("SELECT u.disallowed FROM User u WHERE u.pesel = '?1'")
    boolean isUserDisallowed(String peselHash);

    @Override
    Optional<User> findById(Long aLong);

    @Query(nativeQuery = true, value = "SELECT COUNT(*) FROM user WHERE voided_vote IS TRUE")
    Integer getVoidedVotes();

    @Query(nativeQuery = true, value = "SELECT COUNT(*) FROM user WHERE disallowed is TRUE")
    Integer getDisallowedVotes();
}
