package bracquib.coopcycle.repository;

import bracquib.coopcycle.domain.Commande;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Commande entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CommandeRepository extends ReactiveCrudRepository<Commande, Long>, CommandeRepositoryInternal {
    @Override
    Mono<Commande> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Commande> findAllWithEagerRelationships();

    @Override
    Flux<Commande> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM commande entity WHERE entity.client_id = :id")
    Flux<Commande> findByClient(Long id);

    @Query("SELECT * FROM commande entity WHERE entity.client_id IS NULL")
    Flux<Commande> findAllWhereClientIsNull();

    @Query("SELECT * FROM commande entity WHERE entity.restaurant_id = :id")
    Flux<Commande> findByRestaurant(Long id);

    @Query("SELECT * FROM commande entity WHERE entity.restaurant_id IS NULL")
    Flux<Commande> findAllWhereRestaurantIsNull();

    @Query("SELECT * FROM commande entity WHERE entity.livreur_id = :id")
    Flux<Commande> findByLivreur(Long id);

    @Query("SELECT * FROM commande entity WHERE entity.livreur_id IS NULL")
    Flux<Commande> findAllWhereLivreurIsNull();

    @Override
    <S extends Commande> Mono<S> save(S entity);

    @Override
    Flux<Commande> findAll();

    @Override
    Mono<Commande> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface CommandeRepositoryInternal {
    <S extends Commande> Mono<S> save(S entity);

    Flux<Commande> findAllBy(Pageable pageable);

    Flux<Commande> findAll();

    Mono<Commande> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Commande> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Commande> findOneWithEagerRelationships(Long id);

    Flux<Commande> findAllWithEagerRelationships();

    Flux<Commande> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
