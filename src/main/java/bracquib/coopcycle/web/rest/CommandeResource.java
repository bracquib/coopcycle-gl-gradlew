package bracquib.coopcycle.web.rest;

import bracquib.coopcycle.repository.CommandeRepository;
import bracquib.coopcycle.service.CommandeService;
import bracquib.coopcycle.service.dto.CommandeDTO;
import bracquib.coopcycle.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link bracquib.coopcycle.domain.Commande}.
 */
@RestController
@RequestMapping("/api")
public class CommandeResource {

    private final Logger log = LoggerFactory.getLogger(CommandeResource.class);

    private static final String ENTITY_NAME = "commande";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CommandeService commandeService;

    private final CommandeRepository commandeRepository;

    public CommandeResource(CommandeService commandeService, CommandeRepository commandeRepository) {
        this.commandeService = commandeService;
        this.commandeRepository = commandeRepository;
    }

    /**
     * {@code POST  /commandes} : Create a new commande.
     *
     * @param commandeDTO the commandeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new commandeDTO, or with status {@code 400 (Bad Request)} if the commande has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/commandes")
    public Mono<ResponseEntity<CommandeDTO>> createCommande(@Valid @RequestBody CommandeDTO commandeDTO) throws URISyntaxException {
        log.debug("REST request to save Commande : {}", commandeDTO);
        if (commandeDTO.getId() != null) {
            throw new BadRequestAlertException("A new commande cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return commandeService
            .save(commandeDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/commandes/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /commandes/:id} : Updates an existing commande.
     *
     * @param id the id of the commandeDTO to save.
     * @param commandeDTO the commandeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated commandeDTO,
     * or with status {@code 400 (Bad Request)} if the commandeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the commandeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/commandes/{id}")
    public Mono<ResponseEntity<CommandeDTO>> updateCommande(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CommandeDTO commandeDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Commande : {}, {}", id, commandeDTO);
        if (commandeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, commandeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return commandeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return commandeService
                    .update(commandeDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /commandes/:id} : Partial updates given fields of an existing commande, field will ignore if it is null
     *
     * @param id the id of the commandeDTO to save.
     * @param commandeDTO the commandeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated commandeDTO,
     * or with status {@code 400 (Bad Request)} if the commandeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the commandeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the commandeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/commandes/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<CommandeDTO>> partialUpdateCommande(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CommandeDTO commandeDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Commande partially : {}, {}", id, commandeDTO);
        if (commandeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, commandeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return commandeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<CommandeDTO> result = commandeService.partialUpdate(commandeDTO);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /commandes} : get all the commandes.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of commandes in body.
     */
    @GetMapping("/commandes")
    public Mono<List<CommandeDTO>> getAllCommandes(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Commandes");
        return commandeService.findAll().collectList();
    }

    /**
     * {@code GET  /commandes} : get all the commandes as a stream.
     * @return the {@link Flux} of commandes.
     */
    @GetMapping(value = "/commandes", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<CommandeDTO> getAllCommandesAsStream() {
        log.debug("REST request to get all Commandes as a stream");
        return commandeService.findAll();
    }

    /**
     * {@code GET  /commandes/:id} : get the "id" commande.
     *
     * @param id the id of the commandeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the commandeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/commandes/{id}")
    public Mono<ResponseEntity<CommandeDTO>> getCommande(@PathVariable Long id) {
        log.debug("REST request to get Commande : {}", id);
        Mono<CommandeDTO> commandeDTO = commandeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(commandeDTO);
    }

    /**
     * {@code DELETE  /commandes/:id} : delete the "id" commande.
     *
     * @param id the id of the commandeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/commandes/{id}")
    public Mono<ResponseEntity<Void>> deleteCommande(@PathVariable Long id) {
        log.debug("REST request to delete Commande : {}", id);
        return commandeService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
