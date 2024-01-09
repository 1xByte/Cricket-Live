package com.panda.CricketLive.repo;

import com.panda.CricketLive.model.MatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CricketRepo extends JpaRepository<MatchEntity,Integer> {
    MatchEntity findByTeamHeading(String teamHeading);
}
