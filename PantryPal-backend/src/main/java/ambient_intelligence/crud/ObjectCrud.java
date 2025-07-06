package ambient_intelligence.crud;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import ambient_intelligence.data.ObjectEntity;
import ambient_intelligence.enums.ObjectTypeEnum;


public interface ObjectCrud extends MongoRepository<ObjectEntity, String>{
		
	public List<ObjectEntity> findByChildren_IdAndActiveTrue(
			@Param("childId") String childId, 
			Pageable pageable);
	
	public List<ObjectEntity> findByChildren_Id(
			@Param("childId") String childId, 
			Pageable pageable);
	
	public List<ObjectEntity> findByParents_IdAndActiveTrue(
			@Param("parentId") String parentId, 
			Pageable pageable);
	
	public List<ObjectEntity> findByParents_Id(
			@Param("parentId") String parentId, 
			Pageable pageable);
 	
	public List<ObjectEntity> findByIdIn(
			@Param("id") String id,
			Pageable pageable);
	
	public List<ObjectEntity> findAllByActiveIsTrue(
			Pageable pageable);
	
	public Optional<ObjectEntity> findByIdAndActiveIsTrue(
			@Param("id") String id
			);
		
	public List<ObjectEntity> findAllByAlias(
			@Param("alias") String alias, 
			Pageable pageable);
	
	public List<ObjectEntity> findAllByAliasAndActiveIsTrue(
			@Param("alias") String alias, 
			Pageable pageable);

	public List<ObjectEntity> findAllByAliasLike(
			@Param("pattern") String pattern, 
			Pageable pageable);
	
	public List<ObjectEntity> findAllByAliasLikeAndActiveIsTrue(
			@Param("pattern") String pattern, 
			Pageable pageable);

	public List<ObjectEntity> findAllByType(
			@Param("type") ObjectTypeEnum type, 
			Pageable pageable);
	
	public List<ObjectEntity> findAllByTypeAndActiveIsTrue(
			@Param("type") ObjectTypeEnum type, 
			Pageable pageable);
	
	public List<ObjectEntity> findAllByStatus(
			@Param("status") String status, 
			Pageable pageable);
	
	public List<ObjectEntity> findAllByStatusAndActiveIsTrue(
			@Param("status") String status, 
			Pageable pageable);

	
	public List<ObjectEntity> findAllByTypeAndStatus(
			@Param("type") ObjectTypeEnum type, 
			@Param("status") String status, 
			Pageable pageable);
	
	public List<ObjectEntity> findAllByTypeAndStatusAndActiveIsTrue(
			@Param("type") ObjectTypeEnum type, 
			@Param("status") String status, 
			Pageable pageable);
}
