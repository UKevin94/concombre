Feature: Account history

Scenario: History of 1 deposit then 1 withdrawal today
Given I am a bank client
And my account balance is at E200.00
When I make a deposit of E100.00 to my account
And I retrieve E55.50 from my account
And I ask to see the history (operation, date, amount, balance) of my operations
Then I get a listing of 1 deposit of E100.00 then 1 withdrawal E55.50 of today
And the new balance of my account is E244.50