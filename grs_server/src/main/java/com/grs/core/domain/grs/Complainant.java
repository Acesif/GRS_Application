package com.grs.core.domain.grs;

import com.grs.core.domain.AddressTypeValue;
import com.grs.core.domain.BaseEntity;
import com.grs.core.domain.Gender;
import com.grs.core.domain.IdentificationType;
import lombok.*;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Complainant extends BaseEntity {

    private Long id;
    private String name;
    private String email;
    private String identificationValue;
    private IdentificationType identificationType;
    private Gender gender;
    private Long nationalityId;
    private String occupation;
    private String education;
    private Long presentAddressCountryId;
    private Long permanentAddressCountryId;
    private String presentAddressStreet;
    private String presentAddressHouse;
    private String presentAddressDivisionNameBng;
    private String presentAddressDivisionNameEng;
    private Integer presentAddressDivisionId;
    private String presentAddressDistrictNameBng;
    private String presentAddressDistrictNameEng;
    private Integer presentAddressDistrictId;
    private AddressTypeValue presentAddressTypeValue;
    private Integer presentAddressTypeId;
    private String presentAddressTypeNameBng;
    private String presentAddressTypeNameEng;
    private String presentAddressPostalCode;
    private String permanentAddressStreet;
    private String permanentAddressHouse;
    private String permanentAddressDivisionNameBng;
    private String permanentAddressDivisionNameEng;
    private Integer permanentAddressDivisionId;
    private String permanentAddressDistrictNameBng;
    private String permanentAddressDistrictNameEng;
    private Integer permanentAddressDistrictId;
    private AddressTypeValue permanentAddressTypeValue;
    private Integer permanentAddressTypeId;
    private String permanentAddressTypeNameEng;
    private String permanentAddressTypeNameBng;
    private String permanentAddressPostalCode;
    private Date birthDate;
    private String username;
    private String password;
    private boolean authenticated;
    private String phoneNumber;
    private String foreignPermanentAddressLine1;
    private String foreignPermanentAddressLine2;
    private String foreignPermanentAddressCity;
    private String foreignPermanentAddressState;
    private String foreignPermanentAddressZipCode;
    private String foreignPresentAddressLine1;
    private String foreignPresentAddressLine2;
    private String foreignPresentAddressCity;
    private String foreignPresentAddressState;
    private String foreignPresentAddressZipCode;
}
