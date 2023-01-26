package com.epam.esm.controller;

import com.epam.esm.controller.constraint.FilterConstraint;
import com.epam.esm.dto.GiftCertificateDto;
import com.epam.esm.dto.converter.GiftCertificateConverter;
import com.epam.esm.dto.group.OnPersist;
import com.epam.esm.entity.*;
import com.epam.esm.service.GiftCertificateService;
import com.epam.esm.service.exception.PersistentException;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public GiftCertificateController(GiftCertificateService giftCertificateService, GiftCertificateConverter giftCertificateDtoConverter) {
        this.giftCertificateService = giftCertificateService;
        this.giftCertificateDtoConverter = giftCertificateDtoConverter;
    }

    /**
     * @return a {@link List} of found {@link GiftCertificate} entities.
     */
    @GetMapping()
    public List<GiftCertificateDto> findAllCertificates(
            @RequestParam(required = false, defaultValue = "1") @Min(value = 1, message = "40013") int page,
            @RequestParam(required = false, defaultValue = "5") @Min(value = 1, message = "40014") int size
    ) {
        List<GiftCertificate> giftCertificates = giftCertificateService.findAll(page, size);
        return giftCertificates.stream().map(giftCertificateDtoConverter::toDto).collect(Collectors.toList());
    }

    @GetMapping("/filter")
    public List<GiftCertificateDto> findAllCertificatesFiltered(
            @RequestParam(required = false, defaultValue = "1") @Min(value = 1, message = "40013") int page,
            @RequestParam(required = false, defaultValue = "5") @Min(value = 1, message = "40014") int size,
            @RequestParam @FilterConstraint MultiValueMap<String, String> filterParams
    ) {
        List<GiftCertificate> giftCertificates = giftCertificateService.findAllWithFilter(page, size, filterParams);
        return giftCertificates.stream().map(giftCertificateDtoConverter::toDto).collect(Collectors.toList());
    }

    /**
     * Gets a {@link com.epam.esm.entity.GiftCertificate} by its <code>id</code> from database.
     * @param id for {@link com.epam.esm.entity.GiftCertificate}
     * @return requested {@link com.epam.esm.entity.GiftCertificate} entity. Response code 200.
     * @throws PersistentException if {@link com.epam.esm.entity.GiftCertificate} is not found.
     */
    @GetMapping("/{id}")
    public GiftCertificateDto giftCertificateById(@PathVariable @Valid @Min(value = 1, message = "40001") Long id) throws PersistentException {
        GiftCertificate giftCertificate = giftCertificateService.findById(id);
        return giftCertificateDtoConverter.toDto(giftCertificate);
    }

    /**
     * Creates a new {@link GiftCertificate} entity with a
     * {@link List} of {@link Tag} entities.
     * If new {@link Tag} entities are passed during creation – they will be created in the database.
     * @param giftCertificateDto must be valid according to {@link com.epam.esm.entity.GiftCertificate} entity.
     * @return ResponseEntity with saved {@link com.epam.esm.entity.Tag}. Response code 201.
     * @throws PersistentException if {@link com.epam.esm.entity.Tag} an error occurred during saving.
     */
    @PostMapping
    public ResponseEntity<Object> saveGiftCertificate(@RequestBody @Validated(OnPersist.class) GiftCertificateDto giftCertificateDto) throws PersistentException {
        GiftCertificate savedCert = giftCertificateService.save(giftCertificateDtoConverter.toEntity(giftCertificateDto));
        URI locationUri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedCert.getId())
                .toUri();
        return ResponseEntity.created(locationUri).body(giftCertificateDtoConverter.toDto(savedCert));
    }

    /**
     * Updates a {@link GiftCertificate} by specified <code>id</code>.
     * @param id a {@link GiftCertificate} id.
     * @param giftCertificateDto a {@link GiftCertificate} that contains information for updating.
     * Updates only fields, that are passed in request body.
     * @return ResponseEntity with message. Response code 203.
     * @throws PersistentException if the {@link GiftCertificate} entity do not exist.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateGiftCertificate(@PathVariable @Min(value = 1, message="40001") Long id, @RequestBody @Valid GiftCertificateDto giftCertificateDto) throws PersistentException {
        giftCertificateDto.setId(id);
        giftCertificateService.update(giftCertificateDtoConverter.toEntity(giftCertificateDto));
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
