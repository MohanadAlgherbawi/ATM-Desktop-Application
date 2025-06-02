package com.example.javabuilder;

import java.io.*;
import java.util.*;

public class UserAccount implements Serializable  {
    private int accountId;
    private int accountUserPhoneNumber;
    private String accountUserFullName;
    private String accountPassword;
    private double accountBalance;
    private ArrayList<String> accountDepositDate = new ArrayList<>();
    private ArrayList<String> accountWithdrawDate = new ArrayList<>();
    private  Set<Integer> numbers = new HashSet<>();
    private Random rand = new Random();
    private boolean locked = false;
    public UserAccount(String accountUserFullName,int accountUserPhoneNumber ,String accountPassword, double accountBalance){
        this.accountUserFullName = accountUserFullName;
        this.accountUserPhoneNumber = accountUserPhoneNumber;
        this.accountPassword = accountPassword;
        this.accountBalance = accountBalance;
        this.accountId = getRandomNumber();
    }
    public int getAccountId() {
        return accountId;
    }
    public String getAccountPassword() {
        return accountPassword;
    }
    public double getAccountBalance() {
        return accountBalance;
    }
    public void setAccountPassword(String accountPassword) {
        this.accountPassword = accountPassword;
    }

    public void deposit(double amount) {
            accountBalance += amount;
            accountDepositDate.add("Deposit Operation found in: "+new Date().toString()+" ( "+amount+" )");
    }
    public void withdraw(double amount) {
            if(accountBalance - amount >= 0){
                accountBalance -= amount;
                accountWithdrawDate.add("Withraw Operation found in: "+new Date().toString()+" ( "+amount+" )");
            }
    }
    public ArrayList<String> getAccountDepositDate() {
        return accountDepositDate;
    }
    public ArrayList<String> getAccountWithdrawalDate() {
        return accountWithdrawDate;
    }
    public int getRandomNumber(){
        int accountId;
        do{
            accountId = 1000000+ rand.nextInt(9000000);
        }while (numbers.contains(accountId));
        numbers.add(accountId);
        return accountId;
    }
    public void setLocked(boolean locked) {
        this.locked = locked;
    }
    public boolean isLocked() {
        return locked;
    }
    public String getAccountUserFullName(){
        return accountUserFullName;
    }
}
