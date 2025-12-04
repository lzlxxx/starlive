package com.starlive.org.repository;

import com.starlive.org.model.Live;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * 直播数据访问层
 **/
public interface LiveRepository extends JpaRepository<Live, Long> {
    boolean existsByRoomId(Long roomId);
    Optional<Live> findByUserIdAndRoomId(Long userId, Long roomId);
}

