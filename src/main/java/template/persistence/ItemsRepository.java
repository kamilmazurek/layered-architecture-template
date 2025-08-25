package template.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemsRepository extends JpaRepository<ItemEntity, Long> {
    @Query("select max(item.id) from ItemEntity item")
    Long findMaxID();

}
