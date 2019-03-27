package fr.flaurens.bankaccount.services;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import fr.flaurens.bankaccount.adapters.AccountDAO;
import fr.flaurens.bankaccount.adapters.OperationDAO;
import fr.flaurens.bankaccount.model.Account;
import fr.flaurens.bankaccount.model.Operation;
import fr.flaurens.bankaccount.model.OperationType;
import org.junit.Assert;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import util.OperationMatcher;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration
@SpringBootTest
public class AccountServiceSteps {

    @Mock
    private AccountDAO accountDAOMock;

    @Mock
    private OperationDAO operationDAOMock;

    @InjectMocks
    private AccountService accountService;

    private long accountId;

    private List<Operation> operationList;

    private List<Operation> expectedOperationList;

    private Date startingTime;

    public AccountServiceSteps(){

    }

    @Given("^I am a bank client$")
    public void i_am_a_bank_client() throws Throwable {
        this.accountId=100;
        MockitoAnnotations.initMocks(this);
        this.expectedOperationList = new ArrayList<>();
        this.startingTime = Date.from(Instant.now());
    }

    @Given("^my account balance is at E(\\d+.\\d+)$")
    public void my_account_balance_is_at_E(float amount) throws Throwable {
        Account account = new Account(accountId);
        account.updateBalance(amount);
        when(accountDAOMock.getAccountById(accountId)).thenReturn(account);
    }

    @When("^I make a deposit of E(\\d+.\\d+) to my account$")
    public void i_make_a_deposit_of_E_to_my_account(float amount) throws Throwable {
        accountService.makeDepositOnAccount(accountId, amount);
        Operation expected = new Operation(accountId,amount,OperationType.DEPOSIT);
        this.expectedOperationList.add(expected);
        verify(operationDAOMock, times(1)).persistOperation(argThat(new OperationMatcher(expected)));
    }

    @Then("^the new balance of my account is E(\\d+.\\d+)$")
    public void the_new_balance_of_my_account_is_E(float amount) throws Throwable {
        Assert.assertEquals(amount, accountService.getCurrentBalance(accountId), 0.001);
    }

    @When("^I ask to see the history \\(operation, date, amount, balance\\) of my operations$")
    public void i_ask_to_see_the_history_operation_date_amount_balance_of_my_operations() throws Throwable {
        operationList = accountService.getAccountHistory(accountId);
    }

    @When("^I retrieve E(\\d+.\\d+) from my account$")
    public void i_retrieve_E_from_my_account(float amount) throws Throwable {
        accountService.retrieveFromAccount(accountId, amount);
        Operation expected = new Operation(accountId,amount,OperationType.WITHDRAWAL);
        this.expectedOperationList.add(expected);
        verify(operationDAOMock, times(1)).persistOperation(argThat(new OperationMatcher(expected)));
    }

    @Then("^I get a listing of (\\d+) deposit of E(\\d+.\\d+) then (\\d+) withdrawal E(\\d+.\\d+) of today$")
    public void i_get_a_listing_of_deposit_of_E_then_withdrawal_E_of_today(int nbOfDeposits, float amountOfDeposit, int nbOfWithdrawals, float amountOfWithdrawal) throws Throwable {
        when(operationDAOMock.getOperationByAccount(accountId)).thenReturn(this.expectedOperationList);
        operationList = accountService.getAccountHistory(accountId);
        Assert.assertEquals("Number of operations",nbOfDeposits+nbOfWithdrawals,operationList.size());
        Assert.assertEquals("Deposit amount check",amountOfDeposit,operationList.get(0).getAmount(),0.001);
        Assert.assertEquals("Deposit type check", OperationType.DEPOSIT,operationList.get(0).getOperationType());
        Assert.assertTrue("Deposit date check",!operationList.get(0).getDate().after(Date.from(Instant.now()))
                                                            && !operationList.get(0).getDate().before(startingTime));
        Assert.assertEquals("Withdrawal amount check",amountOfWithdrawal,operationList.get(1).getAmount(),0.001);
        Assert.assertEquals("Withdrawal type check",OperationType.WITHDRAWAL,operationList.get(1).getOperationType());
        Assert.assertTrue("Withdrawal date check",!operationList.get(1).getDate().after(Date.from(Instant.now()))
                        && !operationList.get(1).getDate().before(startingTime));
        Assert.assertTrue("Date order check",!operationList.get(0).getDate().after(operationList.get(1).getDate()));
    }

}
