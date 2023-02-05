package com.epam.esm.controller;

import com.epam.esm.assembler.GiftCertificateAssembler;
import com.epam.esm.controller.constraint.FilterConstraint;
import com.epam.esm.dto.GiftCertificateDto;
import com.epam.esm.dto.converter.GiftCertificateConverter;
import com.epam.esm.dto.group.OnPersist;
import com.epam.esm.entity.*;
import com.epam.esm.service.GiftCertificateService;
import com.epam.esm.service.exception.PersistentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * This class is an endpoint of the API which allows to perform CRUD operations
 * with {@link com.epam.esm.entity.GiftCertificate} entities accessed through <i>api/certificates</i>.
 * @author Lobur Yaroslav
 * @version 1.0
 */
@RestController
@RequestMapping("api/certificates")
@Validated
public class GiftCertificateController {

    private final GiftCertificateService giftCertificateService;

    private final GiftCertificateConverter giftCertificateDtoConverter;

    private final GiftCertificateAssembler giftCertificateAssembler;


    @Autowired
    public GiftCertificateController(GiftCertificateService giftCertificateService, GiftCertificateConverter giftCertificateDtoConverter, GiftCertificateAssembler giftCertificateAssembler) {
        this.giftCertificateService = giftCertificateService;
        this.giftCertificateDtoConverter = giftCertificateDtoConverter;
        this.giftCertificateAssembler = giftCertificateAssembler;
    }

    /**
     * Allows to get a list of {@link GiftCertificate} entities with tags.
     * @param page         page number.
     * @param size         number of showed entities on page.
     * @return a {@link List} of found {@link GiftCertificate} entities with specified parameters.
     */
    @GetMapping()
    public CollectionModel<GiftCertificateDto> findAllCertificates(
            @RequestParam(required = false, defaultValue = "1") @Min(value = 1, message = "40013") int page,
            @RequestParam(required = false, defaultValue = "5") @Min(value = 1, message = "40014") int size
    ) {
        List<GiftCertificateDto> giftCertificates = giftCertificateService
                .getAll(page, size)
                .stream()
                .map(giftCertificateDtoConverter::toDto)
                .map(giftCertificateAssembler::toModel)
                .collect(Collectors.toList());
        Link selfRel = linkTo(methodOn(this.getClass()).findAllCertificates(page, size)).withSelfRel();
        return CollectionModel.of(giftCertificates, selfRel);
    }

    /**
     * Allows to get certificates with tags (all params are optional and can be used in conjunction):
     * <ul>
     *  <li>search for gift certificates by several tags</li>
     *  <li>search by part of name/description</li>
     *  <li>sort by date or by name ASC/DESC</li>
     * </ul>
     *
     * @param page         page number.
     * @param size         number of showed entities on page.
     * @param filterParams is a {@link MultiValueMap} collection that contains {@link String} as
     *                     key and {@link String} as value.
     *                     <pre><ul>
     *                     <li>name as {@link GiftCertificate} name</li>
     *                     <li>description as {@link GiftCertificate} description</li>
     *                     <li>tags as {@link Tag} name (multiple times)</li>
     *                     <li>name_sort as {@link  String} for sorting certificates by name (asc/desc)</li>
     *                     <li>date_sort as {@link  String} for sorting certificates by create date (asc/desc)</li>
     *                     </ul></pre>
     * @return a {@link List} of found {@link GiftCertificate} entities with specified parameters. Response code 200.
     */
    @GetMapping("/filter")
    public CollectionModel<GiftCertificateDto> findAllCertificatesFiltered(
            @RequestParam(required = false, defaultValue = "1") @Min(value = 1, message = "40013") int page,
            @RequestParam(required = false, defaultValue = "5") @Min(value = 1, message = "40014") int size,
            @RequestParam @FilterConstraint MultiValueMap<String, String> filterParams
    ) {
        List<GiftCertificateDto> giftCertificates = giftCertificateService
                .getAllWithFilter(page, size, filterParams)
                .stream()
                .map(giftCertificateDtoConverter::toDto)
                .map(giftCertificateAssembler::toModel)
                .collect(Collectors.toList());
        Link selfRel = linkTo(methodOn(this.getClass()).findAllCertificates(page, size)).withSelfRel();
        return CollectionModel.of(giftCertificates, selfRel);
    }

    /**
     * Gets a {@link GiftCertificate} by its <code>id</code> from database.
     * @param id for {@link GiftCertificate}
     * @return requested {@link GiftCertificate} entity. Response code 200.
     * @throws PersistentException if {@link GiftCertificate} is not found.
     */
    @GetMapping("/{id}")
    public GiftCertificateDto giftCertificateById(@PathVariable @Valid @Min(value = 1, message = "40001") Long id) throws PersistentException {
        GiftCertificate giftCertificate = giftCertificateService.getById(id);
        GiftCertificateDto giftCertificateDto = giftCertificateDtoConverter.toDto(giftCertificate);
        return giftCertificateAssembler.toModel(giftCertificateDto);
    }

    /**
     * Creates a new {@link GiftCertificate} entity with a
     * {@link List} of {@link Tag} entities.
     * If new {@link Tag} entities are passed during creation – they will be created in the database.
     * @param giftCertificateDto must be valid according to {@link GiftCertificateDto} entity.
     * @return ResponseEntity with saved {@link GiftCertificate}. Response code 201.
     * @throws PersistentException if {@link GiftCertificate} an error occurred during saving.
     */
    @PostMapping
    public ResponseEntity<Object> saveGiftCertificate(@RequestBody @Validated(OnPersist.class) GiftCertificateDto giftCertificateDto) throws PersistentException {
        GiftCertificate savedCert = giftCertificateService.save(giftCertificateDtoConverter.toEntity(giftCertificateDto));
        URI locationUri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedCert.getId())
                .toUri();
        GiftCertificateDto savedCertDto = giftCertificateDtoConverter.toDto(savedCert);
        return ResponseEntity.created(locationUri).body(giftCertificateAssembler.toModel(savedCertDto));
    }

    /**
     * Updates a {@link GiftCertificate} by specified <code>id</code>.
     * @param id a {@link GiftCertificate} id.
     * @param giftCertificateDto a {@link GiftCertificateDto} that contains information for updating.
     * Updates only fields, that are passed in request body.
     * @return ResponseEntity with message. Response code 203.
     * @throws PersistentException if the {@link GiftCertificate} entity do not exist.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateGiftCertificate(@PathVariable @Min(value = 1, message="40001") Long id, @RequestBody @Valid GiftCertificateDto giftCertificateDto) throws PersistentException {
        giftCertificateService.update(id, giftCertificateDtoConverter.toEntity(giftCertificateDto));
        return ResponseEntity.status(HttpStatus.CREATED).body("Success");
    }

    /**
     * Deletes {@link com.epam.esm.entity.GiftCertificate} entity from database.
     *
     * @param id for {@link com.epam.esm.entity.GiftCertificate} to delete.
     * @return ResponseEntity with empty body. Response code 204.
     * @throws PersistentException if {@link com.epam.esm.entity.GiftCertificate} entity do not exist.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteGiftCertificate(@PathVariable @Valid @Min(value = 1, message = "40001") Long id) throws PersistentException {
        giftCertificateService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
