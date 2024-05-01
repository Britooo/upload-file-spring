package school.sptech.filesexample.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.filesexample.entity.FileEntity;

public interface FileEntityRepository extends JpaRepository<FileEntity, Integer> {
}
