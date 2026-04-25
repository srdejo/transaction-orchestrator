package com.tumipay.transaction_orchestrator.domain.model;

import com.tumipay.transaction_orchestrator.domain.model.valueobject.DocumentType;

public class Customer {
    private final DocumentType documentType;
    private final String documentNumber;
    private final String countryCallCode;
    private final String phone;
    private final String email;
    private final String firstName;
    private final String middleName;
    private final String lastName;
    private final String secondLastName;

    public Customer(DocumentType documentType, String documentNumber, String countryCallCode, 
                    String phone, String email, String firstName, String middleName, 
                    String lastName, String secondLastName) {
        if (documentType == null) throw new IllegalArgumentException("DocumentType is required");
        if (documentNumber == null || documentNumber.isBlank()) throw new IllegalArgumentException("DocumentNumber is required");
        if (countryCallCode == null || countryCallCode.isBlank()) throw new IllegalArgumentException("CountryCallCode is required");
        if (phone == null || phone.isBlank()) throw new IllegalArgumentException("Phone is required");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email is required");
        if (firstName == null || firstName.isBlank()) throw new IllegalArgumentException("FirstName is required");
        if (lastName == null || lastName.isBlank()) throw new IllegalArgumentException("LastName is required");

        this.documentType = documentType;
        this.documentNumber = documentNumber;
        this.countryCallCode = countryCallCode;
        this.phone = phone;
        this.email = email;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.secondLastName = secondLastName;
    }

    public DocumentType getDocumentType() { return documentType; }
    public String getDocumentNumber() { return documentNumber; }
    public String getCountryCallCode() { return countryCallCode; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getMiddleName() { return middleName; }
    public String getLastName() { return lastName; }
    public String getSecondLastName() { return secondLastName; }
}
