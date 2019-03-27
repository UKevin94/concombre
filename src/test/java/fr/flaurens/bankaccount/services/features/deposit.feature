Feature: Deposit

Scenario: I want to make a deposit of E100 when my balance is positive
Given I am a bank client
And my account balance is at E100.00
When I make a deposit of E100.00 to my account
Then the new balance of my account is E200.00
