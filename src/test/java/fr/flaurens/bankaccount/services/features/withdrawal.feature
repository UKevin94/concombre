Feature: Withdrawal

Scenario: I want to retrieve E100 when my balance is above E100
Given I am a bank client
And my account balance is at E200.00
When I retrieve E100.00 from my account
Then the new balance of my account is E100.00
