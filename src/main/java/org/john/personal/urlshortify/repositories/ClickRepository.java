package org.john.personal.urlshortify.repositories;

import org.john.personal.urlshortify.models.Click;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClickRepository extends JpaRepository<Click, Long> {

}
