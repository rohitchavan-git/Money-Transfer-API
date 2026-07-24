Feature: Debit card allocation and lookup

  Scenario: AC-1 allocate a debit card for an eligible existing account
    Given an existing account with a valid AccountId
    And the account balance is at least 5000
    When the requesting account user requests debit-card allocation
    Then the request succeeds
    And exactly one debit-card record is persisted for the account

  Scenario: AC-2 reject allocation for an invalid AccountId
    Given an invalid or nonexistent AccountId
    When the requesting account user requests debit-card allocation
    Then the operation returns or throws an invalid-account error
    And no debit-card record is created

  Scenario: AC-3 reject allocation for insufficient balance
    Given an existing account with a balance below 5000
    When the requesting account user requests debit-card allocation
    Then the operation returns or throws an insufficient-balance error
    And no debit-card record is created

  Scenario: AC-4 allocate a debit card at the minimum eligible balance
    Given an existing account with a valid AccountId
    And the account balance is exactly 5000
    When the requesting account user requests debit-card allocation
    Then the request succeeds
    And exactly one debit-card record is persisted for the account

  Scenario: AC-5 reject duplicate debit-card allocation
    Given an account that already has one debit card
    When the requesting account user requests another debit card for the same account
    Then the operation returns or throws a duplicate-card error
    And the account still has exactly one debit-card record

  Scenario: AC-6 initialize the generated card number and PIN
    Given an eligible existing account
    When a debit card is successfully allocated
    Then the stored card number contains exactly 16 numeric digits
    And the stored PIN/password is exactly "0000"

  Scenario: AC-7 generate unique card numbers for different accounts
    Given two different eligible accounts
    When a debit card is allocated for each account
    Then each persisted card number contains exactly 16 numeric digits
    And the two card numbers are different

  Scenario: AC-8 persist and associate the debit-card details
    Given an eligible existing account
    When a debit card is successfully allocated
    Then the debit-card details are persisted in the debit-card table or equivalent model
    And the debit-card record is associated with the correct Account record

  Scenario: AC-9 retrieve a debit-card number by account number
    Given an account number associated with an allocated debit card
    When the authorized API consumer performs a card-number lookup
    Then the operation returns the corresponding card number
    And the returned card number contains exactly 16 numeric digits

  Scenario: AC-10 reject lookup when the account has no debit card
    Given a valid account number with no allocated debit card
    When the authorized API consumer performs a card-number lookup
    Then the operation returns a not-found result or equivalent error
    And no card number is returned

  Scenario: AC-11 reject lookup for an invalid account number
    Given an invalid or nonexistent account number
    When the authorized API consumer performs a card-number lookup
    Then the operation returns an account-not-found or equivalent error
    And no card number is returned

  Scenario: AC-12 do not expose card secrets in failures
    Given an allocation or lookup operation fails
    When the failure is returned or logged
    Then the error message and application logs do not contain a debit-card number
    And the error message and application logs do not contain a PIN

  Scenario: AC-13 prevent concurrent duplicate allocation
    Given two concurrent allocation requests for the same eligible account
    When both requests are processed
    Then at most one request succeeds
    And the database contains no more than one debit-card record for the account

  Scenario: AC-14 provide automated coverage for debit-card behaviour
    Given the debit-card allocation and lookup implementation exists
    When the automated test suite is executed
    Then it covers valid allocation
    And invalid AccountId
    And balance below 5000
    And balance exactly 5000
    And duplicate allocation
    And 16-digit card generation
    And default PIN "0000"
    And persistence and account association
    And successful lookup
    And lookup failures