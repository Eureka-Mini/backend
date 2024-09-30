package com.dangun.miniproject.common.repository;

import com.dangun.miniproject.common.code.Code;
import com.dangun.miniproject.common.code.CodeKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CodeRepository extends JpaRepository<Code, CodeKey> {
	@Query("select c from Code c where c.id.groupCode = :groupCode order by c.orderNo")
	Page<Code> findByGroupCode(@Param("groupCode") String groupCode, Pageable pageable);
}
