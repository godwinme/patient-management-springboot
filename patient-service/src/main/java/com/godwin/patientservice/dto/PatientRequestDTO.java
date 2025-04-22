package com.godwin.patientservice.dto;

import com.godwin.patientservice.dto.validators.CreatePatientValidationGroup;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PatientRequestDTO {

    @NotNull(message = "Name is required")
    @Size(max = 100, message = "Max is 100 characters")
    private String name;

    @NotNull(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Address should be blank")
    private String address;

    @NotBlank(message = "Date of birth should not be blank")
    private String dateOfBirth;

    @NotNull(groups = CreatePatientValidationGroup.class, message = "Date of birth should not be blank")
    private String registeredDate;
}
